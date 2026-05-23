package org.yaken.demoji.infrastructure.otel

import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.context.Context
import io.opentelemetry.context.Scope
import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

suspend inline fun <T> Tracer.inSpan(
    name: String,
    crossinline block: suspend Span.() -> T,
): T {
    val parentContext = Context.current()
    val span = spanBuilder(name).setParent(parentContext).startSpan()
    val spanContext = parentContext.with(span)

    return try {
        withContext(OpenTelemetryContextElement(spanContext)) {
            span.block()
        }
    } catch (t: Throwable) {
        span.markError(t)
        throw t
    } finally {
        span.end()
    }
}

fun Span.markError(
    throwable: Throwable,
    recordException: Boolean = true,
    fallbackDescription: String = "Unhandled exception",
) {
    if (recordException) {
        recordException(throwable)
    }
    setAttribute("error.type", throwable::class.qualifiedName ?: throwable.javaClass.name)
    setStatus(StatusCode.ERROR, throwable.message ?: fallbackDescription)
}

@PublishedApi
internal class OpenTelemetryContextElement(
    private val context: Context,
) : ThreadContextElement<Scope>, AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<OpenTelemetryContextElement>

    override fun updateThreadContext(context: CoroutineContext): Scope {
        return this.context.makeCurrent()
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: Scope) {
        oldState.close()
    }
}
