plugins {
    java
    id("com.gradleup.shadow") version "9.0.0"
    id("com.modrinth.minotaur") version "2.9.0"
}

group = "io.github.miklires"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.112-stable")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.12.3")
    implementation("org.bstats:bstats-bukkit:3.1.0")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("mChat-${project.version}.jar")
        relocate("org.bstats", "io.github.miklires.mchat.libs.bstats")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
    build {
        dependsOn(shadowJar)
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
    test {
        useJUnitPlatform()
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN") ?: "")
    projectId.set(System.getenv("MODRINTH_PROJECT_ID") ?: "")
    versionNumber.set(project.version.toString())
    versionName.set("mChat ${project.version}")
    versionType.set("release")
    uploadFile.set(tasks.shadowJar)
    gameVersions.add("26.2")
    loaders.addAll("paper", "purpur")
    changelog.set(provider { file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "" })
    syncBodyFrom.set(provider { file("README.md").takeIf { it.exists() }?.readText() ?: "" })
}
