import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {
        namespace = "com.wojciechkula.deepskyapp.feature.about"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        // Required for the composeResources logos to be packaged into the Android artifact —
        // without it painterResource throws MissingResourceException at runtime.
        androidResources {
            enable = true
        }
    }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.designsystem)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
        }
    }
}

// Pinned so the generated accessors keep a stable package regardless of the module coordinates.
compose.resources {
    packageOfResClass = "com.wojciechkula.deepskyapp.feature.about.resources"
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
