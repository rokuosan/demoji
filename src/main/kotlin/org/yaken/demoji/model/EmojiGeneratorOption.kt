package org.yaken.demoji.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmojiGeneratorOption(
    /**
     * テキストの配置
     */
    @Serializable(with = TextAlignSerializer::class)
    val align: TextAlign,

    /**
     * 背景色
     */
    @SerialName("back_color")
    val backColor: String = "00000000",

    /**
     * テキストの色
     */
    @SerialName("color")
    val color: String,

    /**
     * 生成する絵文字に使用されるフォント
     */
    @Serializable(with = FontSerializer::class)
    val font: Font,

    /**
     * 絵文字の言語設定？
     * デフォルト値から変更する必要はない
     */
    val locale: String = "ja",

    /**
     * 絵文字公開設定。
     * true の場合、絵文字ジェネレータの作成履歴に表示されます。
     */
    @SerialName("public_fg")
    val publicFg: Boolean = false,

    /**
     * 文字サイズを固定するかどうか。
     * true の場合、テキストのサイズを固定します。
     */
    @SerialName("size_fixed")
    val sizeFixed: Boolean = false,
    /**
     * テキストの自動伸縮設定
     * true の場合、テキストが枠内に収まるように自動でフォントサイズを調整します。
     */
    val stretch: Boolean = true,
    val text: String,
)
