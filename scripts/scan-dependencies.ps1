[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$scannerImage = "ghcr.io/google/osv-scanner@sha256:a70ffffc2bfd33482953e5d6b64c6fcdb30239717a07b0f0d3ab2859ce3e4cde"

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw "Docker is required for the pinned OSV dependency scan."
}

& docker run --rm `
    --mount "type=bind,source=$repoRoot,target=/src,readonly" `
    $scannerImage `
    scan source --recursive /src

if ($LASTEXITCODE -ne 0) {
    throw "OSV dependency scan failed or found a known vulnerability."
}

