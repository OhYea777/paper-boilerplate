pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
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

rootProject.name = "example-plugin"
