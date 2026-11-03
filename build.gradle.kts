@file:Suppress("PropertyName")

plugins {
    application
    kotlin("jvm") version "2.3.10"
    id("io.ktor.plugin") version "3.4.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.10"
}

group = "org.yaken"
version = "0.0.1"

val logback_version: String by project
val koin_version: String by project
val kord_version: String by project
val kord_common_version: String by project

repositories {
    mavenCentral()
    maven("https://snapshots.kord.dev") {
        content { includeGroup("dev.kord") }
        metadataSources {
            mavenPom()
            ignoreGradleMetadataRedirection()
        }
    }
}

application {
    mainClass.set("org.yaken.demoji.DemojiApplicationKt")
}


// Pin transitive Kord modules to the same build containing the nullable application_id fix.
// https://github.com/kordlib/kord/pull/1072
configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "dev.kord") {
            useVersion(if (requested.name.startsWith("kord-common")) kord_common_version else kord_version)
        }
    }
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
    implementation("dev.kord:kord-core-jvm:${kord_version}")
    implementation("dev.kord:kord-common-jvm:${kord_common_version}")
    implementation("dev.kord:kord-rest-jvm:${kord_version}")
    implementation("dev.kord:kord-gateway-jvm:${kord_version}")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    // OpenTelemetry - manual instrumentation
    implementation(platform("io.opentelemetry:opentelemetry-bom:1.62.0"))
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-sdk")
    implementation("io.opentelemetry:opentelemetry-exporter-logging")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")

    // Test
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.opentelemetry:opentelemetry-sdk-testing")
    // mockk
    testImplementation("io.mockk:mockk:1.13.16")

}

kotlin {
    jvmToolchain(21)
}
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    jvmArgs("--add-opens", "java.base/java.util=ALL-UNNAMED")
}
