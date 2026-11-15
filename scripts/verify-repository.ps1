[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$trackedAndNew = @(
    & git -C $repoRoot ls-files --cached --others --exclude-standard
)
if ($LASTEXITCODE -ne 0) {
    throw "Unable to enumerate repository files."
}

$errors = [System.Collections.Generic.List[string]]::new()
$textExtensions = @(
    ".gradle", ".groovy", ".java", ".json", ".kt", ".kts", ".md",
    ".pbxproj", ".plist", ".properties", ".ps1", ".sql", ".swift",
    ".xml", ".xcscheme", ".yaml", ".yml"
)
$spaceIndentedExtensions = @(
    ".gradle", ".groovy", ".java", ".json", ".kt", ".kts", ".ps1",
    ".plist", ".sql", ".swift", ".xml", ".xcscheme", ".yaml", ".yml"
)
$forbiddenExtensions = @(".jks", ".keystore", ".p12", ".pfx", ".pem")
$secretPatterns = [ordered]@{
    "AWS access key" = "(?:AKIA|ASIA)[0-9A-Z]{16}"
    "GitHub token" = "(?:gh[pousr]_[A-Za-z0-9]{36,}|github_pat_[A-Za-z0-9_]{60,})"
    "Google API key" = "AIza[0-9A-Za-z_-]{35}"
    "Stripe live key" = "sk_live_[0-9A-Za-z]{20,}"
    "Private key" = "-----BEGIN (?:RSA |EC |OPENSSH |DSA )?PRIVATE KEY-----"
}
$secretPatternText = ($secretPatterns.Values -join "|")
foreach ($syntheticSecret in @(
    ("AKIA" + "ABCDEFGHIJKLMNOP"),
    ("-----BEGIN " + "PRIVATE KEY-----")
)) {
    if ($syntheticSecret -notmatch $secretPatternText) {
        throw "The secret-pattern self-check failed."
    }
}
if ("kinly-local-only" -match $secretPatternText) {
    throw "The local-only credential triggered the secret-pattern self-check."
}

foreach ($relativePath in $trackedAndNew) {
    $normalized = $relativePath.Replace("\", "/")
    $fileName = [System.IO.Path]::GetFileName($normalized)
    $extension = [System.IO.Path]::GetExtension($normalized).ToLowerInvariant()

    if (($fileName -eq ".env") -or
        ($fileName.StartsWith(".env.") -and $fileName -ne ".env.example") -or
        ($forbiddenExtensions -contains $extension) -or
        ($fileName -match "(?i)^(?:credentials|service-account).*[.]json$")) {
        $errors.Add("Forbidden credential-bearing file is tracked: $normalized")
        continue
    }

    if (-not ($textExtensions -contains $extension)) {
        continue
    }

    $absolutePath = Join-Path $repoRoot $relativePath
    if (-not (Test-Path -LiteralPath $absolutePath -PathType Leaf)) {
        continue
    }

    $content = [System.IO.File]::ReadAllText($absolutePath)
    if ($content.Length -gt 0 -and -not $content.EndsWith("`n")) {
        $errors.Add("Missing final newline: $normalized")
    }
    if ($extension -ne ".md" -and $content -match "(?m)[ `t]+`r?$") {
        $errors.Add("Trailing whitespace: $normalized")
    }
    if (($spaceIndentedExtensions -contains $extension) -and $content.Contains("`t")) {
        $errors.Add("Tab indentation is not allowed: $normalized")
    }

    foreach ($entry in $secretPatterns.GetEnumerator()) {
        if ($content -match $entry.Value) {
            $errors.Add("Possible $($entry.Key) in $normalized")
        }
    }

    if (($extension -eq ".java" -or $extension -eq ".kt" -or
        $extension -eq ".kts" -or $extension -eq ".swift") -and
        $content -match "(?im)(?:log(?:ger)?[.](?:trace|debug|info|warn|error)|System[.]out|printStackTrace).*?(?:authorization|password|token|latitude|longitude|coordinate|request.?body)") {
        $errors.Add("Sensitive field appears in a logging statement: $normalized")
    }
}

$compose = [System.IO.File]::ReadAllText((Join-Path $repoRoot "compose.yaml"))
if ($compose -match '(?m)^\s*-\s*[''"]?(?:0[.]0[.]0[.]0:)?(?:5433|6380):') {
    $errors.Add("Local database/cache ports must bind explicitly to 127.0.0.1.")
}
foreach ($binding in @("127.0.0.1:5433:5432", "127.0.0.1:6380:6379")) {
    if (-not $compose.Contains($binding)) {
        $errors.Add("Missing loopback-only Compose binding: $binding")
    }
}

$applicationConfig = [System.IO.File]::ReadAllText(
    (Join-Path $repoRoot "backend/src/main/resources/application.yaml")
)
foreach ($safeSetting in @(
    "show-components: never",
    "show-details: never",
    "include-binding-errors: never",
    "include-message: never",
    "include-stacktrace: never"
)) {
    if (-not $applicationConfig.Contains($safeSetting)) {
        $errors.Add("Missing privacy-safe backend setting: $safeSetting")
    }
}

if ($errors.Count -gt 0) {
    $errors | ForEach-Object { Write-Error $_ }
    exit 1
}

Write-Host "Repository formatting, secret, safe-log, and local-isolation checks passed."

