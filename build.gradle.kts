plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
    // Vault
    maven("https://nexus.hc.to/content/repositories/pub_releases")
    maven("https://nexus.heroslender.com/repository/maven-public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("net.milkbowl.vault:VaultAPI:1.6")
    compileOnly("com.heroslender:StackDrops:1.11.2")

    implementation(project(":nms"))
    implementation(project(":nms:v1_8_R3"))
    implementation(project(":nms:v1_13_R1"))

    compileOnly(fileTree("libs") {
        include("*.jar")
    })
}

allprojects {
    group = "com.heroslender"
    version = "1.4.0"

    apply {
        plugin("org.gradle.java")
    }

    repositories {
        mavenCentral()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/sonatype-nexus-snapshots/")
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.24")
        annotationProcessor("org.projectlombok:lombok:1.18.24")

        compileOnly("org.jetbrains:annotations:23.1.0")
    }

    java {
        withJavadocJar()
        withSourcesJar()

        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        }

        javadoc {
            options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        }

        processResources {
            filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        }
    }
}