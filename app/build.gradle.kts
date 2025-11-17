plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.ufpr.equilibrium"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ufpr.equilibrium"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }



    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.1.10"))
    implementation(project(":core-common"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    // Removed heavy TensorFlow dependencies to reduce APK size
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.compilercommon)
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Google Places Autocomplete

    testImplementation(libs.junit)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}

detekt {
    config = files(rootProject.file("config/detekt/detekt.yml"))
}

ktlint {
    android.set(true)
    ignoreFailures.set(false)
}

kapt {
    javacOptions {
        option("-J--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED")
        option("-J--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED")
        option("-J--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED")
        option("-J--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED")
        option("-J--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
        option("-J--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
    }
}

// Task to install and launch the debug build on a connected device
tasks.register("runDebugOnDevice") {
    dependsOn("installDebug")
    doLast {
        // Requires 'adb' available in PATH (Android SDK platform-tools)
        exec {
            commandLine("adb", "shell", "am", "start", "-n", "com.ufpr.equilibrium/.MainActivity")
        }
    }
}