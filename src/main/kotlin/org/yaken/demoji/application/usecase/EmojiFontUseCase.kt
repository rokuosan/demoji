package org.yaken.demoji.application.usecase

import org.yaken.demoji.domain.entity.EmojiFont

interface EmojiFontUseCase {
    fun getAvailableFonts(): List<EmojiFont>
}

class EmojiFontUseCaseImpl : EmojiFontUseCase {
    override fun getAvailableFonts(): List<EmojiFont> {
        return listOf(
            EmojiFont(
                id = "noto-sans-mono-cjk-jp-bold",
                name = "Noto Sans Mono CJK JP Bold",
                filename = "NotoSansMonoCJKjp-Bold.otf"
            ),
        )
    }
}
