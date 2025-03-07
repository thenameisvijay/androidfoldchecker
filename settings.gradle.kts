pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    /*versionCatalogs {
        create("libs") {
            from(files("gradle/libs.version.toml"))
        }
    }*/
}

rootProject.name = "Foldable"
include(":app")
include(":foldablechecklibrary")
