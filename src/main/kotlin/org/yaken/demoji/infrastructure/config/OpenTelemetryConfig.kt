package org.yaken.demoji.infrastructure.config

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
    private const val instrumentationScopeName = "org.yaken.demoji"
    private const val serviceNameKey = "service.name"

    lateinit var openTelemetry: OpenTelemetry
        private set
    lateinit var tracer: Tracer
        private set
    private var tracerProvider: SdkTracerProvider? = null

    /**
     * Initialize OpenTelemetry once at process start.
     * When an OTLP endpoint is provided, spans are exported there via HTTP.
     * Otherwise, spans are written to the log exporter for local debugging.
     */
    fun initialize(serviceName: String = "demoji", otlpEndpoint: String? = null): OpenTelemetry {
        if (this::openTelemetry.isInitialized) {
            return openTelemetry
        }

        val exporter = createSpanExporter(otlpEndpoint)
        val resource = Resource.getDefault().merge(
            Resource.create(
                Attributes.of(
                    AttributeKey.stringKey(serviceNameKey), serviceName,
                ),
            ),
        )

        val provider = SdkTracerProvider.builder()
            .setResource(resource)
            .addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
            .build()

        tracerProvider = provider
        openTelemetry = OpenTelemetrySdk.builder()
            .setTracerProvider(provider)
            .buildAndRegisterGlobal()
        tracer = openTelemetry.getTracer(instrumentationScopeName)

        Runtime.getRuntime().addShutdownHook(Thread {
            try {
                tracerProvider?.shutdown()
            } catch (_: Exception) {
                // Best effort shutdown.
            }
        })

        return openTelemetry
    }

    private fun createSpanExporter(otlpEndpoint: String?): io.opentelemetry.sdk.trace.export.SpanExporter {
        if (otlpEndpoint.isNullOrBlank()) {
            return LoggingSpanExporter.create()
        }

        val builder = OtlpHttpSpanExporter.builder()
            .setEndpoint(otlpEndpoint)
            .addHeader("Accept", "*/*")

        System.getenv("MACKEREL_API_KEY")
            ?.takeIf { it.isNotBlank() }
            ?.let { builder.addHeader("Mackerel-Api-Key", it) }

        return builder.build()
    }
}
