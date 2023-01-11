pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("com.gradle.enterprise") version("3.9")
}

// Minecraft
val minecraftVersion: String by extra

// Depenendencies
val paperlibVersion: String by extra

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("paperdev", "${minecraftVersion}-R0.1-SNAPSHOT")
            version("paperlib", paperlibVersion)

            library("paperlib", "io.papermc", "paperlib").versionRef("paperlib")
        }
    }
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}

rootProject.name = "example-plugin"
