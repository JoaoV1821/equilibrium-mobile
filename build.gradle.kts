// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}

// Fix duplicate class: com.google.common.util.concurrent.ListenableFuture
// Some transitive AndroidX libs pull `com.google.guava:listenablefuture:1.0`
// while others bring `com.google.guava:guava`. Exclude the standalone
// `listenablefuture` artifact so only Guava's classes remain.
subprojects {
    configurations.all {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
}