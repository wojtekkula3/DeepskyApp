import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {
        namespace = "com.wojciechkula.deepskyapp.core.designsystem"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        // Required for the composeResources drawables to be packaged into the Android artifact —
        // without it painterResource throws MissingResourceException at runtime.
        androidResources {
            enable = true
        }
        withHostTest {}
    }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// Pinned so the generated accessors keep a stable package regardless of the module coordinates.
// The class is public and renamed because features read these accessors directly instead of going
// through a hand-written facade: a feature already imports its own `Res`, and two classes with that
// name in one file would need an import alias in every call site. Renaming here does not affect the
// packaged resource paths — those follow `packageOfResClass`.
compose.resources {
    publicResClass = true
    packageOfResClass = "com.wojciechkula.deepskyapp.core.designsystem.resources"
    nameOfResClass = "DesignSystemRes"
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
