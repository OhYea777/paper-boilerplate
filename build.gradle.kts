import net.researchgate.release.ReleaseExtension
import java.text.SimpleDateFormat
import java.util.*

plugins {
    java
    `maven-publish`
    id("xyz.jpenilla.run-paper") version "2.0.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("net.researchgate.release") version "3.0.2"
    id("io.papermc.paperweight.userdev") version "1.4.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

// Plugin
val pluginId: String by extra
val pluginName: String by extra
val pluginGroup: String by extra
val pluginAuthor: String by extra
val pluginWebsite: String by extra
val pluginDescription: String by extra
val pluginJavaVersion: String by extra

// Minecraft
val minecraftVersion: String by extra

// Misc
val github: String by extra

group = pluginGroup
description = pluginDescription

val baseArchivesName = "${pluginId}-${minecraftVersion}"
base {
    archivesName.set(baseArchivesName)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(pluginJavaVersion))
}

dependencies {
    paperDevBundle(libs.versions.paperdev.get())

    implementation(libs.paperlib)
}

val sourcesJarTask by tasks.creating(Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
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

                    "Specification-Title" to pluginId,
                    "Specification-Vendor" to pluginAuthor,
                    "Specification-Version" to project.version,

                    "Implementation-Title" to pluginName,
                    "Implementation-Vendor" to pluginAuthor,
                    "Implementation-Version" to archiveVersion,
                    "Implementation-Timestamp" to now
                )
            )
        }
    }

    afterReleaseBuild {
        dependsOn(publish)
    }
}

configure<ReleaseExtension> {
    failOnSnapshotDependencies.set(false)

    with(git) {
        requireBranch.set(minecraftVersion)
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${github}")

            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("maven") {
            artifactId = baseArchivesName

            artifact(tasks.reobfJar)
            artifact(sourcesJarTask)
            artifact(tasks.shadowJar)

            pom {
                name.set(pluginName)
                description.set(pluginDescription)
                url.set(pluginWebsite)

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/${github}.git")
                    developerConnection.set("scm:git:ssh://github.com/${github}.git")
                    url.set("https://github.com/${github}/tree/${minecraftVersion}")
                }
            }
        }
    }
}
