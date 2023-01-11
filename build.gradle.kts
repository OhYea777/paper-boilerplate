import java.text.SimpleDateFormat
import java.util.*

plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.0.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("io.papermc.paperweight.userdev") version "1.4.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

// Plugin
val pluginName: String by extra
val pluginGroup: String by extra
val pluginAuthor: String by extra
val pluginVersion: String by extra
val pluginJavaVersion: String by extra

group = pluginGroup
version = pluginVersion

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(pluginJavaVersion))
}

dependencies {
    paperDevBundle(libs.versions.paperdev.get())

    implementation(libs.paperlib)
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    jar {
        enabled = false
    }

    shadowJar {
        relocate("io.papermc.lib", "${project.group}.io.papermc.paperlib")
        minimize()

        doFirst {
            archiveClassifier.set("dev")
        }
    }

    withType<ProcessResources> {
        inputs.property("version", version)

        filteringCharset = Charsets.UTF_8.name()

        filesMatching("**/plugin.yml") {
            expand(project.properties)
        }
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(JavaLanguageVersion.of(pluginJavaVersion).asInt())
    }

    withType<Jar> {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())

        manifest {
            attributes(
                mapOf(
                    "Created-By" to System.getProperty("java.vm.version") + " (" + System.getProperty("java.vm.vendor") + ")",

                    "Specification-Title" to pluginName,
                    "Specification-Vendor" to pluginAuthor,
                    "Specification-Version" to pluginVersion,

                    "Implementation-Title" to pluginName,
                    "Implementation-Vendor" to pluginAuthor,
                    "Implementation-Version" to archiveVersion,
                    "Implementation-Timestamp" to now
                )
            )
        }
    }
}
