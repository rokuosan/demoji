import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.yaken.demoji.emoji.EmojiBuilder
import java.awt.Color
import java.io.File

class EmojiBuilderTest : FunSpec({

    context("EmojiBuilder") {
        test("build with default values") {
            val builder = EmojiBuilder()
            val image = builder.build()
            image.width shouldBe 128
            image.height shouldBe 128
        }

        test("build with custom text") {
            val builder = EmojiBuilder().apply {
                text = "Hello"
            }
            val image = builder.build()
            image.width shouldBe 128
            image.height shouldBe 128
        }

        test("build with custom font file") {
            val fontFile = File("fonts/NotoSansMono-Bold.otf")
            val builder = EmojiBuilder().apply {
                this.fontFile = fontFile
            }
            val image = builder.build()
            image.width shouldBe 128
            image.height shouldBe 128
        }

        test("build with transparent background") {
            val builder = EmojiBuilder().apply {
                bgColor = null
            }
            val image = builder.build()
            image.width shouldBe 128
            image.height shouldBe 128
        }

        test("build with custom background color") {
            val builder = EmojiBuilder().apply {
                bgColor = Color.RED
            }
            val image = builder.build()
            image.width shouldBe 128
            image.height shouldBe 128
        }

        test("build with auto width disabled") {
            val builder = EmojiBuilder().apply {
                autoWidth = false
            }
            val image = builder.build()
            image.width shouldBe 128
            image.height shouldBe 128
        }

        test("saveImage saves the image to file") {
            val builder = EmojiBuilder()
            val image = builder.build()
            val outputFile = File("output.png")
            EmojiBuilder.saveImage(image, outputFile)
            outputFile.exists() shouldBe true
            outputFile.delete()
        }
    }
})
