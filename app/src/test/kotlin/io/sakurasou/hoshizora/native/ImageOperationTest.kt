package io.sakurasou.hoshizora.native

import io.github.oshai.kotlinlogging.KLogger
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.sakurasou.hoshizora.di.DI
import io.sakurasou.hoshizora.di.DIManager
import io.sakurasou.hoshizora.exception.native.ImageOperationException
import io.sakurasou.hoshizora.model.group.ImageType
import org.junit.Assume.assumeTrue
import java.io.ByteArrayInputStream
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.exists
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * @author Shiina Kin
 * 2026/4/10 00:17
 */
class ImageOperationTest {
    private lateinit var logger: KLogger
    private lateinit var infoMessages: MutableList<String>
    private lateinit var debugMessages: MutableList<String>
    private lateinit var errorMessages: MutableList<String>
    private lateinit var imageMagickLibraryPath: Path

    @BeforeTest
    fun setUp() {
        imageMagickLibraryPath = resolveImageMagickLibraryPath()

        logger = mockk(relaxed = true)
        infoMessages = mutableListOf()
        debugMessages = mutableListOf()
        errorMessages = mutableListOf()

        every { logger.info(any<() -> Any?>()) } answers {
            infoMessages += firstArg<() -> Any?>().invoke().toString()
        }
        every { logger.debug(any<() -> Any?>()) } answers {
            debugMessages += firstArg<() -> Any?>().invoke().toString()
        }
        every { logger.error(any<() -> Any?>()) } answers {
            errorMessages += firstArg<() -> Any?>().invoke().toString()
        }
    }

    @AfterTest
    fun tearDown() {
        unmockkObject(DIManager)
    }

    @Test
    fun `genesis and terminus should log lifecycle messages`() {
        withNativeContext {
            ImageOperation.genesis()
            ImageOperation.terminus()
        }

        assertLogged(infoMessages, "Initializing ImageMagick runtime")
        assertLogged(infoMessages, "ImageMagick initialized")
        assertLogged(infoMessages, "Terminating ImageMagick runtime")
        assertLogged(infoMessages, "ImageMagick terminated")
    }

    @Test
    fun `readImageBlob and getImageBlob should round trip source image`() {
        val blobBytes =
            withManagedWand { wand ->
                ImageOperation.readImageBlob(wand, sourceImageBytes)
                val blob = ImageOperation.getImageBlob(wand)
                try {
                    assertTrue(blob.size > 0)
                    blob.bytes
                } finally {
                    ImageOperation.relinquishMemory(blob.blobPtr)
                }
            }

        assertImageDimensions(blobBytes, 1080, 1920)

        assertLogged(debugMessages, "Created MagickWand")
        assertLogged(debugMessages, "Read image blob into wand")
        assertLogged(debugMessages, "Fetched image blob from wand")
        assertLogged(debugMessages, "Relinquished native memory")
        assertLogged(debugMessages, "Destroyed MagickWand")
    }

    @Test
    fun `getImageFormat width and height should return image metadata`() {
        withManagedWand { wand ->
            ImageOperation.readImageBlob(wand, sourceImageBytes)

            assertEquals("PNG", ImageOperation.getImageFormat(wand))
            assertEquals(1080L, ImageOperation.getImageWidth(wand))
            assertEquals(1920L, ImageOperation.getImageHeight(wand))
        }

        assertLogged(debugMessages, "Fetched image format=PNG")
        assertLogged(debugMessages, "Fetched image width=1080")
        assertLogged(debugMessages, "Fetched image height=1920")
    }

    @Test
    fun `resizeImage should resize image and write it to disk`() {
        val tempDir = Files.createTempDirectory("image-operation-resize-test")
        try {
            val outputPath = tempDir.resolve("resized.png")

            withManagedWand { wand ->
                ImageOperation.readImageBlob(wand, sourceImageBytes)
                ImageOperation.resizeImage(wand, 320, 180)
                ImageOperation.writeImage(wand, outputPath.toString())
            }

            assertTrue(outputPath.exists(), "Expected output image to be written")
            assertImageDimensions(Files.readAllBytes(outputPath), 320, 180)

            assertLogged(debugMessages, "Resized image in wand")
            assertLogged(debugMessages, "Wrote image from wand")
        } finally {
            tempDir.toFile().deleteRecursively()
        }
    }

    @Test
    fun `scaleImage should scale image to target size`() {
        val blobBytes =
            withManagedWand { wand ->
                ImageOperation.readImageBlob(wand, sourceImageBytes)
                ImageOperation.scaleImage(wand, 160, 90)
                val blob = ImageOperation.getImageBlob(wand)
                try {
                    blob.bytes
                } finally {
                    ImageOperation.relinquishMemory(blob.blobPtr)
                }
            }

        assertImageDimensions(blobBytes, 160, 90)

        assertLogged(debugMessages, "Scaled image in wand")
    }

    @Test
    fun `thumbnailImage should keep aspect ratio within bounds`() {
        val blobBytes =
            withManagedWand { wand ->
                ImageOperation.readImageBlob(wand, sourceImageBytes)
                ImageOperation.thumbnailImage(wand, 320, 320)
                val blob = ImageOperation.getImageBlob(wand)
                try {
                    blob.bytes
                } finally {
                    ImageOperation.relinquishMemory(blob.blobPtr)
                }
            }

        val (width, height) = readImageDimensions(blobBytes)

        assertTrue(width > 0)
        assertTrue(height > 0)
        assertTrue(width <= 320)
        assertTrue(height <= 320)

        assertLogged(debugMessages, "Generated thumbnail in wand")
    }

    @Test
    fun `setImageFormat and setImageQuality should convert image to jpeg`() {
        val blobBytes =
            withManagedWand { wand ->
                ImageOperation.readImageBlob(wand, sourceImageBytes)
                ImageOperation.setImageFormat(wand, ImageType.JPEG)
                ImageOperation.setImageQuality(wand, 65)
                val blob = ImageOperation.getImageBlob(wand)
                try {
                    blob.bytes
                } finally {
                    ImageOperation.relinquishMemory(blob.blobPtr)
                }
            }

        assertTrue(blobBytes.size > 3)
        assertEquals(0xFF.toByte(), blobBytes[0])
        assertEquals(0xD8.toByte(), blobBytes[1])
        assertImageDimensions(blobBytes, 1080, 1920)

        assertLogged(debugMessages, "Set image format for wand")
        assertLogged(debugMessages, "Set image quality for wand")
    }

    @Test
    fun `readImageBlob should throw ImageMagick error for invalid bytes`() {
        val exception =
            assertFailsWith<ImageOperationException> {
                withManagedWand { wand ->
                    ImageOperation.readImageBlob(wand, byteArrayOf(1, 2, 3, 4))
                }
            }

        assertTrue(exception.message.contains("MagickReadImageBlob"))
        assertLogged(errorMessages, "MagickReadImageBlob")
    }

    @Test
    fun `writeImage failure should expose exception type and message`() {
        val tempDir = Files.createTempDirectory("image-operation-exception-test")
        try {
            val outputPath = tempDir.resolve("empty.png")

            withManagedWand { wand ->
                val exception =
                    assertFailsWith<ImageOperationException> {
                        ImageOperation.writeImage(wand, outputPath.toString())
                    }

                val exceptionType = ImageOperation.getExceptionType(wand)
                val exceptionMessage = ImageOperation.getException(wand)

                assertTrue(exception.message.contains("MagickWriteImage"))
                assertTrue(exceptionType > 0)
                assertTrue(exceptionMessage.contains("no images", ignoreCase = true))
            }

            assertLogged(errorMessages, "MagickWriteImage")
            assertLogged(debugMessages, "Fetched ImageMagick exception type=")
            assertLogged(debugMessages, "Fetched ImageMagick exception from wand")
        } finally {
            tempDir.toFile().deleteRecursively()
        }
    }

    private fun ensureImageOperationInitialized() {
        val linker = Linker.nativeLinker()
        val imageMagickLib = SymbolLookup.libraryLookup(imageMagickLibraryPath, Arena.global())
        val di = mockk<DI>()
        every { di.get(Linker::class) } returns linker
        every { di.get(SymbolLookup::class) } returns imageMagickLib
        mockkObject(DIManager)
        every { DIManager.getDIInstance() } returns di
    }

    private fun <T> withNativeContext(block: context(KLogger, Arena) () -> T): T =
        Arena.ofConfined().use { arena ->
            ensureImageOperationInitialized()
            context(logger, arena) {
                block()
            }
        }

    private fun <T> withManagedWand(block: context(KLogger, Arena) (MemorySegment) -> T): T =
        withNativeContext {
            ImageOperation.genesis()
            val wand = ImageOperation.newMagickWand()
            try {
                block(wand)
            } finally {
                ImageOperation.destroyMagickWand(wand)
                ImageOperation.terminus()
            }
        }

    private fun assertImageDimensions(
        bytes: ByteArray,
        expectedWidth: Int,
        expectedHeight: Int,
    ) {
        val (width, height) = readImageDimensions(bytes)
        assertEquals(expectedWidth, width)
        assertEquals(expectedHeight, height)
    }

    private fun readImageDimensions(bytes: ByteArray): Pair<Int, Int> {
        val image = ImageIO.read(ByteArrayInputStream(bytes))
        requireNotNull(image) { "Failed to decode image bytes" }
        return image.width to image.height
    }

    private fun assertLogged(
        messages: List<String>,
        expectedSubstring: String,
    ) {
        assertTrue(
            messages.any { it.contains(expectedSubstring) },
            "Expected a log containing \"$expectedSubstring\", actual logs=$messages",
        )
    }

    private fun resolveImageMagickLibraryPath(): Path {
        val configuredPath =
            System
                .getenv(MAGICK_WAND_LIB_PATH_ENV)
                ?.takeIf { it.isNotBlank() }
                ?.let(Path::of)
                ?.takeIf(Files::exists)
        val skippedMessage = "Unable to locate libMagickWand. Set $MAGICK_WAND_LIB_PATH_ENV to the installed library path."
        assumeTrue(
            skippedMessage,
            configuredPath != null,
        )
        return configuredPath ?: error(skippedMessage)
    }

    private companion object {
        private const val MAGICK_WAND_LIB_PATH_ENV = "MAGICK_WAND_LIB_PATH"

        private val sourceImageBytes: ByteArray by lazy {
            ImageOperationTest::class.java.classLoader
                .getResourceAsStream("test-img.png")
                ?.readBytes()
                ?: error("Missing test resource: test-img.png")
        }
    }
}
