@file:Suppress("PropertyName")

plugins {
    application
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "2.3.12"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
}

group = "org.yaken"
version = "0.0.1"

val logback_version: String by project
val koin_version: String by project
val kord_version: String by project

repositories {
    mavenCentral()
}

application {
    mainClass.set("org.yaken.demoji.DemojiApplicationKt")
}


dependencies {
    // Ktor Client
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-okhttp")
    implementation("io.ktor:ktor-client-auth")
    implementation("io.ktor:ktor-client-content-negotiation")
    // Logger
    implementation("ch.qos.logback:logback-classic:$logback_version")
    // Koin
    runtimeOnly("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    // Discord Wrapper
    implementation("dev.kord:kord-core:${kord_version}")
    implementation("dev.kord:kord-common:${kord_version}")
    implementation("dev.kord:kord-rest:${kord_version}")
    implementation("dev.kord:kord-gateway:${kord_version}")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // Test
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
