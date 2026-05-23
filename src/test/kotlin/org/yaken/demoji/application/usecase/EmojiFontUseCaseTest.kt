package org.yaken.demoji.application.usecase

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.yaken.demoji.domain.entity.EmojiFont

class EmojiFontUseCaseTest : FunSpec({
    test("returns bundled fonts") {
        val useCase = EmojiFontUseCaseImpl()

        useCase.getAvailableFonts() shouldBe listOf(
            EmojiFont(
                id = "noto-sans-mono-cjk-jp-bold",
                name = "Noto Sans Mono CJK JP Bold",
                filename = "NotoSansMonoCJKjp-Bold.otf",
            ),
        )
    }
})
