pluginManagement {
    repositories {
        gradlePluginPortal()
        google()            // ✅ Required for Firebase
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()            // ✅ Required for Firebase
        mavenCentral()
    }
}

rootProject.name = "Taskly"
include(":app")
