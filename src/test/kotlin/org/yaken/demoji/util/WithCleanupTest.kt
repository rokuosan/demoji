package org.yaken.demoji.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class WithCleanupTest : FunSpec({
    test("runs cleanup after successful block") {
        val calls = mutableListOf<String>()

        "resource".withCleanup(
            cleanup = { calls += "cleanup:$it" },
            block = { calls += "block:$it" },
        )

        calls shouldBe listOf("block:resource", "cleanup:resource")
    }

    test("runs cleanup when block throws") {
        val calls = mutableListOf<String>()

        val error = shouldThrow<IllegalStateException> {
            "resource".withCleanup(
                cleanup = { calls += "cleanup:$it" },
                block = {
                    calls += "block:$it"
                    throw IllegalStateException("boom")
                },
            )
        }

        error.message shouldBe "boom"
        calls shouldBe listOf("block:resource", "cleanup:resource")
    }
})
