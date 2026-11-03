package org.yaken.demoji.domain.entity

import org.yaken.demoji.common.Result
import org.yaken.demoji.common.err
import org.yaken.demoji.common.ok
import java.io.File
import java.util.*

data class Emoji(
    val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val text: String? = null,
    val color: String? = null,
    val bgColor: String? = null,
    val font: String? = null,
) {
    private fun hexColor(c: String?): Int? = c?.replace("#", "")?.toIntOrNull(16)
    private fun awtColor(c: String?): java.awt.Color? = hexColor(c)?.let { java.awt.Color(it) }
    fun colorInAWT() = java.awt.Color(hexColor(color) ?: 0)
    fun bgColorInAwtOrNull() = awtColor(bgColor)

    fun fontFile(): File? = font?.let {
        val tt = File("fonts", it)
        if (tt.exists()) tt else null
    }

    fun validate(strict: Boolean = false): Result<Unit, String> {
        if (strict && name == null)
            return err("名前は必須です")

        // 基本的にフォームで見ているため、ここでエラーになることはないが検証しておく
        if (name != null && name.length !in 2..32)
            return err("名前は2文字以上32文字以下である必要があります")

        // カラーコードの検証
        if (hexColor(color) == null)
            return err("文字色が不正です")

        return ok(Unit)
    }
}
