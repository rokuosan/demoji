package org.yaken.demoji.infrastructure.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OpenTelemetrySettingsTest : FunSpec({
    test("loads otel settings from environment") {
        val settings = OpenTelemetrySettings.fromEnvironment(
            MapEnvironment(
                "OTEL_EXPORTER_OTLP_TRACES_ENDPOINT" to "https://example.test/v1/traces",
                "MACKEREL_API_KEY" to "api-key",
            ),
        )

        settings.serviceName shouldBe "demoji"
        settings.otlpTracesEndpoint shouldBe "https://example.test/v1/traces"
        settings.mackerelApiKey shouldBe "api-key"
    }

    test("treats blank values as unset") {
        val settings = OpenTelemetrySettings.fromEnvironment(
            MapEnvironment(
                "OTEL_EXPORTER_OTLP_TRACES_ENDPOINT" to "",
                "MACKEREL_API_KEY" to "",
            ),
        )

        settings.otlpTracesEndpoint shouldBe null
        settings.mackerelApiKey shouldBe null
    }
})
