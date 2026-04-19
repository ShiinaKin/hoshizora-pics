package io.sakurasou.hoshizora.util

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.sakurasou.hoshizora.exception.service.image.io.ImageFileNotFoundException
import io.sakurasou.hoshizora.model.entity.Strategy
import io.sakurasou.hoshizora.model.strategy.LocalStrategy
import io.sakurasou.hoshizora.model.strategy.S3Strategy
import io.sakurasou.hoshizora.model.strategy.WebDavStrategy
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import java.nio.file.Files
import kotlin.io.path.readBytes
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * @author Shiina Kin
 * 2026/4/19 00:00
 */
class ImageUtilsTest {
    @Test
    fun `uploadImageAndGetRelativePath should save local image and return relative path`() =
        runBlocking {
            withTempLocalStrategy { strategy, uploadFolder, _ ->
                val imageBytes = byteArrayOf(1, 2, 3)

                val relativePath =
                    ImageUtils.uploadImageAndGetRelativePath(
                        strategy = strategy,
                        subFolder = "users/7",
                        fileName = "photo.png",
                        imageBytes = imageBytes,
                    )

                assertEquals("users/7/photo.png", relativePath)
                assertContentEquals(imageBytes, uploadFolder.resolve(relativePath).readBytes())
            }
        }

    @Test
    fun `saveThumbnail should save thumbnail under thumbnail folder`() =
        runBlocking {
            withTempLocalStrategy { strategy, _, thumbnailFolder ->
                val thumbnailBytes = byteArrayOf(4, 5, 6)

                ImageUtils.saveThumbnail(
                    strategy = strategy,
                    thumbnailBytes = thumbnailBytes,
                    relativePath = "users/7/photo.png",
                )

                assertContentEquals(thumbnailBytes, thumbnailFolder.resolve("users/7/photo.png").readBytes())
            }
        }

    @Test
    fun `fetchLocalImage should read image and thumbnail bytes`() =
        runBlocking {
            withTempLocalStrategy { strategy, uploadFolder, thumbnailFolder ->
                val imageBytes = byteArrayOf(7, 8, 9)
                val thumbnailBytes = byteArrayOf(1, 1, 2)
                Files.createDirectories(uploadFolder.resolve("users/7"))
                Files.createDirectories(thumbnailFolder.resolve("users/7"))
                Files.write(uploadFolder.resolve("users/7/photo.png"), imageBytes)
                Files.write(thumbnailFolder.resolve("users/7/photo.png"), thumbnailBytes)

                assertContentEquals(imageBytes, ImageUtils.fetchLocalImage(strategy, "users/7/photo.png"))
                assertContentEquals(
                    thumbnailBytes,
                    ImageUtils.fetchLocalImage(strategy, "users/7/photo.png", isThumbnail = true),
                )
            }
        }

    @Test
    fun `fetchLocalImage should reject non local strategies`(): Unit =
        runBlocking {
            assertFailsWith<RuntimeException> {
                ImageUtils.fetchLocalImage(s3Strategy(), "users/7/photo.png")
            }
            assertFailsWith<RuntimeException> {
                ImageUtils.fetchLocalImage(webDavStrategy(), "users/7/photo.png")
            }
        }

    @Test
    fun `fetchS3Image should return public url when object exists`() =
        runBlocking {
            val s3Config = mockk<S3Strategy>()
            every { s3Config.uploadFolder } returns "uploads"
            every { s3Config.thumbnailFolder } returns "thumbnails"
            every { s3Config.publicUrl } returns "https://cdn.example.com"
            coEvery { s3Config.isImageExist("uploads/users/7/photo.png") } returns true

            val url = ImageUtils.fetchS3Image(strategy(s3Config), "users/7/photo.png")

            assertEquals("https://cdn.example.com/uploads/users/7/photo.png", url)
        }

    @Test
    fun `fetchS3Image should return empty thumbnail url when thumbnail is missing`() =
        runBlocking {
            val s3Config = mockk<S3Strategy>()
            every { s3Config.uploadFolder } returns "uploads"
            every { s3Config.thumbnailFolder } returns "thumbnails"
            every { s3Config.publicUrl } returns "https://cdn.example.com"
            coEvery { s3Config.isImageExist("thumbnails/users/7/photo.png") } returns false

            val url = ImageUtils.fetchS3Image(strategy(s3Config), "users/7/photo.png", isThumbnail = true)

            assertEquals("", url)
        }

    @Test
    fun `fetchS3Image should throw when original image is missing`(): Unit =
        runBlocking {
            val s3Config = mockk<S3Strategy>()
            every { s3Config.uploadFolder } returns "uploads"
            every { s3Config.thumbnailFolder } returns "thumbnails"
            every { s3Config.publicUrl } returns "https://cdn.example.com"
            coEvery { s3Config.isImageExist("uploads/users/7/photo.png") } returns false

            assertFailsWith<ImageFileNotFoundException> {
                ImageUtils.fetchS3Image(strategy(s3Config), "users/7/photo.png")
            }
        }

    @Test
    fun `fetchS3Image should reject non s3 strategies`(): Unit =
        runBlocking {
            withTempLocalStrategy { localStrategy, _, _ ->
                assertFailsWith<RuntimeException> {
                    ImageUtils.fetchS3Image(localStrategy, "users/7/photo.png")
                }
            }
            assertFailsWith<RuntimeException> {
                ImageUtils.fetchS3Image(webDavStrategy(), "users/7/photo.png")
            }
        }

    @Test
    fun `fetchWebDavImage should fetch original and thumbnail bytes`() =
        runBlocking {
            val webDavConfig = mockk<WebDavStrategy>()
            every { webDavConfig.uploadFolder } returns "uploads"
            every { webDavConfig.thumbnailFolder } returns "thumbnails"
            coEvery { webDavConfig.fetch("uploads/users/7/photo.png") } returns byteArrayOf(1, 2, 3)
            coEvery {
                webDavConfig.fetch(WebDavStrategy.addThumbnailIdentifierToFileName("thumbnails/users/7/photo.png"))
            } returns byteArrayOf(4, 5, 6)

            assertContentEquals(
                byteArrayOf(1, 2, 3),
                ImageUtils.fetchWebDavImage(strategy(webDavConfig), "users/7/photo.png"),
            )
            assertContentEquals(
                byteArrayOf(4, 5, 6),
                ImageUtils.fetchWebDavImage(strategy(webDavConfig), "users/7/photo.png", isThumbnail = true),
            )
        }

    @Test
    fun `fetchWebDavImage should reject non webdav strategies`(): Unit =
        runBlocking {
            withTempLocalStrategy { localStrategy, _, _ ->
                assertFailsWith<RuntimeException> {
                    ImageUtils.fetchWebDavImage(localStrategy, "users/7/photo.png")
                }
            }
            assertFailsWith<RuntimeException> {
                ImageUtils.fetchWebDavImage(s3Strategy(), "users/7/photo.png")
            }
        }

    private fun s3Strategy(): Strategy =
        strategy(
            S3Strategy(
                endpoint = "https://s3.example.com",
                bucketName = "bucket",
                region = "us-east-1",
                accessKey = "access",
                secretKey = "secret",
                uploadFolder = "uploads",
                thumbnailFolder = "thumbnails",
                publicUrl = "https://cdn.example.com",
            ),
        )

    private fun webDavStrategy(): Strategy =
        strategy(
            WebDavStrategy(
                serverUrl = "https://webdav.example.com",
                username = "user",
                password = "password",
                uploadFolder = "uploads",
                thumbnailFolder = "thumbnails",
            ),
        )

    private fun strategy(config: io.sakurasou.hoshizora.model.strategy.StrategyConfig): Strategy {
        val now = LocalDateTime(2026, 4, 19, 0, 0)
        return Strategy(
            id = 1L,
            name = "test",
            isSystemReserved = false,
            config = config,
            createTime = now,
            updateTime = now,
        )
    }

    private suspend fun withTempLocalStrategy(
        block: suspend (Strategy, java.nio.file.Path, java.nio.file.Path) -> Unit,
    ) {
        val tempDir = Files.createTempDirectory("hoshizora-image-utils-test")
        try {
            val uploadFolder = tempDir.resolve("uploads")
            val thumbnailFolder = tempDir.resolve("thumbnails")
            block(
                strategy(
                    LocalStrategy(
                        uploadFolder = uploadFolder.toString(),
                        thumbnailFolder = thumbnailFolder.toString(),
                    ),
                ),
                uploadFolder,
                thumbnailFolder,
            )
        } finally {
            tempDir.toFile().deleteRecursively()
        }
    }
}
