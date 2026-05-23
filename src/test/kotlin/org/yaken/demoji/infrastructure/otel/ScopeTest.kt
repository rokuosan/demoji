package org.yaken.demoji.infrastructure.otel

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor
import kotlinx.coroutines.delay

class ScopeTest : FunSpec({
    test("keeps parent span context across coroutine suspension") {
        val exporter = InMemorySpanExporter.create()
        val provider = SdkTracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(exporter))
            .build()
        val tracer = provider.get("test")

        try {
            tracer.inSpan("parent") {
                delay(1)
                tracer.inSpan("child") {
                    delay(1)
                }
            }

            val spans = exporter.finishedSpanItems
            spans shouldHaveSize 2

            val parent = spans.single { it.name == "parent" }
            val child = spans.single { it.name == "child" }

            child.traceId shouldBe parent.traceId
            child.parentSpanId shouldBe parent.spanId
        } finally {
            provider.shutdown()
        }
    }

    test("marks thrown exceptions as span errors") {
        val exporter = InMemorySpanExporter.create()
        val provider = SdkTracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(exporter))
            .build()
        val tracer = provider.get("test")

        try {
            shouldThrow<IllegalStateException> {
                tracer.inSpan("failure") {
                    throw IllegalStateException("boom")
                }
            }

            val span = exporter.finishedSpanItems.single()
            span.status.statusCode shouldBe StatusCode.ERROR
            span.status.description shouldBe "boom"
            span.attributes.get(AttributeKey.stringKey("error.type")) shouldBe IllegalStateException::class.qualifiedName
        } finally {
            provider.shutdown()
        }
    }
})
