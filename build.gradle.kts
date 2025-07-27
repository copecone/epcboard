plugins {
    kotlin("jvm") version Dependency.Kotlin.VERSION
    kotlin("plugin.serialization") version Dependency.Serialization.VERSION
    application
}

group = "io.github.copecone"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-logging:${Dependency.Ktor.VERSION}")
    implementation("io.ktor:ktor-server-core:${Dependency.Ktor.VERSION}")
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependency.Coroutines.VERSION}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Dependency.Serialization.Json.VERSION}")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:${Dependency.Datetime.VERSION}")

    implementation("io.ktor:ktor-server-core-jvm:${Dependency.Ktor.VERSION}")
    implementation("io.ktor:ktor-server-netty:${Dependency.Ktor.VERSION}")

    implementation("io.ktor:ktor-server-content-negotiation:${Dependency.Ktor.VERSION}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${Dependency.Ktor.VERSION}")

    implementation("io.ktor:ktor-client-core:${Dependency.Ktor.VERSION}")
    implementation("io.ktor:ktor-client-cio:${Dependency.Ktor.VERSION}")

    implementation("ch.qos.logback:logback-classic:${Dependency.Logback.VERSION}")
    implementation("io.ktor:ktor-server-call-logging-jvm:${Dependency.Ktor.VERSION}")

    implementation("io.github.cdimascio:dotenv-kotlin:${Dependency.DotEnv.VERSION}")

    implementation("org.xerial:sqlite-jdbc:${Dependency.SQLiteJDBC.VERSION}")
    implementation("org.xerial.snappy:snappy-java:${Dependency.Snappy.VERSION}")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application { mainClass.set("${group}.MainKt") }
tasks.jar {
    manifest { attributes["Main-Class"] = application.mainClass.get() }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}