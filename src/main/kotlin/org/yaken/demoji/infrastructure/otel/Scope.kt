package org.yaken.demoji.infrastructure.otel

import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.Tracer
import org.yaken.demoji.infrastructure.config.OpenTelemetryConfig

suspend fun <T> withTracer(name: String, block: suspend (Tracer) -> T): T {
    val tracer = OpenTelemetryConfig.tracer
    return block(tracer)
}

suspend fun <T> Tracer.withSpan(name: String, block: suspend (Span) -> T): T {
    val span = this.spanBuilder(name).startSpan()
    return try {
        span.makeCurrent().use{
            block(span)
        }
    } finally {
        span.end()
    }
}

suspend fun <T> withSpan(name: String, block: suspend (Span) -> T): T {
    val tracer = OpenTelemetryConfig.tracer
    val span = tracer.spanBuilder(name).startSpan()
    return try {
        block(span)
    } finally {
        span.end()
    }
}
