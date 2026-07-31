import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

// Resolution order for the APOD key: this checkout's local.properties, then a Gradle property, then an
// environment variable. Putting it in ~/.gradle/gradle.properties is the recommended spot — that file
// lives outside the repository, so every clone and every git worktree picks the key up automatically
// instead of needing its own local.properties (a worktree gets a fresh one with only sdk.dir).
val apodApiKeyValue: String? = (
    localProperties.getProperty("APOD_API_KEY")
        ?: providers.gradleProperty("APOD_API_KEY").orNull
        ?: providers.environmentVariable("APOD_API_KEY").orNull
    )?.trim()?.removeSurrounding("\"")

// Wrapped in quotes here so buildConfigField emits a valid Kotlin string literal.
val apodApiKey: String = if (apodApiKeyValue.isNullOrBlank()) {
    logger.warn(
        "APOD_API_KEY is not set: the app will build but every APOD request will fail. " +
            "Add 'APOD_API_KEY=<key>' to ~/.gradle/gradle.properties so all worktrees inherit it."
    )
    "\"\""
} else {
    "\"$apodApiKeyValue\""
}

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.wojciechkula.deepskyapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.wojciechkula.deepskyapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "APOD_API_KEY", apodApiKey)
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}