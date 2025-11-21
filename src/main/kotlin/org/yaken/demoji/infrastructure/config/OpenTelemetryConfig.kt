package org.yaken.demoji.infrastructure.config

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor

object OpenTelemetryConfig {
    lateinit var openTelemetry: OpenTelemetry
    lateinit var tracer: Tracer
    private var tracerProvider: SdkTracerProvider? = null

    /**
     * Initialize OpenTelemetry.
     * If [otlpEndpoint] is provided, use OTLP HTTP exporter with custom headers.
     * Otherwise, fall back to logging exporter.
     */
    fun initialize(serviceName: String = "demoji", otlpEndpoint: String? = null) {
        val builder = OtlpHttpSpanExporter.builder().addHeader("Accept", "*/*")

        val mackerelApiKey = System.getenv("MACKEREL_API_KEY")
        if (!mackerelApiKey.isNullOrBlank()) {
            builder.addHeader("Mackerel-Api-Key", mackerelApiKey)
        }

        val exporter = if (!otlpEndpoint.isNullOrBlank()) {
            builder.setEndpoint(otlpEndpoint).build()
        } else {
            LoggingSpanExporter()
        }

        val resource = Resource.getDefault().merge(
            Resource.create(
                Attributes.of(
                    AttributeKey.stringKey("service.name"), serviceName,
                )
            )
        )

        val provider = SdkTracerProvider.builder().addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
            .setResource(resource).build()
        tracerProvider = provider

        openTelemetry = OpenTelemetrySdk.builder().setTracerProvider(provider).buildAndRegisterGlobal()

        tracer = GlobalOpenTelemetry.getTracer("org.yaken.demoji")

        Runtime.getRuntime().addShutdownHook(Thread {
            try {
                tracerProvider?.shutdown()
            } catch (_: Exception) {
            }
        })
    }
}
