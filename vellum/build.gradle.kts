plugins {
    id("net.fabricmc.fabric-loom")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName = project.property("archives_base_name") as String
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}")
    implementation("com.google.code.gson:gson:2.13.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }

    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${project.name}" }
    }
}
