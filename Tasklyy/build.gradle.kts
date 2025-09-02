

// Top-level build file where you can add configuration options common to all sub-projects/modules
plugins {
    id("com.android.application") version "8.11.1" apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    id ("com.google.dagger.hilt.android")version "2.51.1" apply false
    id("com.google.devtools.ksp") version "2.0.0–1.0.23"

}

