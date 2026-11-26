plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val debugReadinessUrl = providers.gradleProperty("KINLY_BACKEND_READINESS_URL")
    .orElse("http://10.0.2.2:8080/actuator/health/readiness")
    .get()
fun authProperty(name: String) = providers.gradleProperty(name).orElse("").get()
fun quoted(value: String) = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

android {
    namespace = "com.kinly.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kinly.app"
        manifestPlaceholders["appAuthRedirectScheme"] = "com.kinly.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "AUTH_ISSUER", quoted(authProperty("KINLY_AUTH_ISSUER")))
            buildConfigField("String", "AUTH_AUTHORIZE_URL", quoted(authProperty("KINLY_AUTH_AUTHORIZE_URL")))
            buildConfigField("String", "AUTH_TOKEN_URL", quoted(authProperty("KINLY_AUTH_TOKEN_URL")))
            buildConfigField("String", "AUTH_CLIENT_ID", quoted(authProperty("KINLY_AUTH_CLIENT_ID")))
            buildConfigField("String", "AUTH_ME_URL", quoted("http://10.0.2.2:8080/api/v1/me"))
            buildConfigField(
                "String",
                "BACKEND_READINESS_URL",
                "\"${debugReadinessUrl.replace("\"", "\\\"")}\"",
            )
        }
        release {
            listOf("AUTH_ISSUER", "AUTH_AUTHORIZE_URL", "AUTH_TOKEN_URL", "AUTH_CLIENT_ID", "AUTH_ME_URL")
                .forEach { field -> buildConfigField("String", field, quoted("")) }
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "BACKEND_READINESS_URL",
                "\"https://invalid.kinly.example/actuator/health/readiness\"",
            )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.12.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("net.openid:appauth:0.11.1")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
}

configurations.configureEach {
    if (name == "releaseRuntimeClasspath") {
        resolutionStrategy.activateDependencyLocking()
    }
}
