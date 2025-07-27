plugins {
    kotlin("jvm") version Dependency.Kotlin.VERSION
    kotlin("plugin.serialization") version Dependency.Serialization.VERSION
    application
}

group = "io.github.forceload"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependency.Coroutines.VERSION}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Dependency.Serialization.Json.VERSION}")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:${Dependency.Datetime.VERSION}")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application { mainClass.set("MainKt") }
tasks.jar {
    manifest { attributes["Main-Class"] = application.mainClass.get() }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}