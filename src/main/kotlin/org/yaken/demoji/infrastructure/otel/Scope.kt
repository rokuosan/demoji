package org.yaken.demoji.infrastructure.otel

import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.api.trace.Tracer

suspend inline fun <T> Tracer.inSpan(
    name: String,
    crossinline block: suspend Span.() -> T,
): T {
    val span = spanBuilder(name).startSpan()
    return try {
        span.makeCurrent().use {
            span.block()
        }
    } catch (t: Throwable) {
        span.recordException(t)
        span.setAttribute("error.type", t::class.qualifiedName ?: t.javaClass.name)
        span.setStatus(StatusCode.ERROR, t.message ?: "Unhandled exception")
        throw t
    } finally {
        span.end()
    }
}
