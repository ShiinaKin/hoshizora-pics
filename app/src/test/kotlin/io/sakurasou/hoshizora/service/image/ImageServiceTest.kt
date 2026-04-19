package io.sakurasou.hoshizora.service.image

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import io.sakurasou.hoshizora.controller.request.ImageManagePatchRequest
import io.sakurasou.hoshizora.controller.request.ImagePatchRequest
import io.sakurasou.hoshizora.exception.service.image.ImageUpdateFailedException
import io.sakurasou.hoshizora.model.DatabaseSingleton
import io.sakurasou.hoshizora.model.dao.album.AlbumDao
import io.sakurasou.hoshizora.model.dao.group.GroupDao
import io.sakurasou.hoshizora.model.dao.image.ImageDao
import io.sakurasou.hoshizora.model.dao.strategy.StrategyDao
import io.sakurasou.hoshizora.model.dao.user.UserDao
import io.sakurasou.hoshizora.model.entity.Image
import io.sakurasou.hoshizora.service.setting.SettingService
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * @author Shiina Kin
 * 2024/9/13 21:47
 */
class ImageServiceTest {
    private lateinit var imageDao: ImageDao
    private lateinit var albumDao: AlbumDao
    private lateinit var userDao: UserDao
    private lateinit var groupDao: GroupDao
    private lateinit var strategyDao: StrategyDao
    private lateinit var settingService: SettingService
    private lateinit var imageService: ImageServiceImpl

    @BeforeTest
    fun setUp() {
        mockkObject(DatabaseSingleton)
        imageDao = mockk()
        albumDao = mockk()
        userDao = mockk()
        groupDao = mockk()
        strategyDao = mockk()
        settingService = mockk()
        imageService = ImageServiceImpl(imageDao, albumDao, userDao, groupDao, strategyDao, settingService)

        coEvery { DatabaseSingleton.dbQuery<Unit>(any()) } coAnswers {
            this.arg<suspend () -> Unit>(0).invoke()
        }
    }

    @Test
    fun `patchSelfImage should reject enabling random fetch for private image`(): Unit =
        runBlocking {
            every { imageDao.findImageById(1) } returns testImage(isPrivate = true, isAllowedRandomFetch = false)
            every { imageDao.updateImageById(any()) } returns 1

            val exception =
                assertFailsWith<ImageUpdateFailedException> {
                    imageService.patchSelfImage(
                        userId = 10,
                        imageId = 1,
                        selfPatchRequest = ImagePatchRequest(isAllowedRandomFetch = true),
                    )
                }

            assertTrue(exception.message.contains("Private images cannot be fetched randomly."))
            verify(exactly = 0) { imageDao.updateImageById(any()) }
        }

    @Test
    fun `patchImage should reject making random fetch image private`(): Unit =
        runBlocking {
            every { imageDao.findImageById(1) } returns testImage(isPrivate = false, isAllowedRandomFetch = true)
            every { imageDao.updateImageById(any()) } returns 1

            val exception =
                assertFailsWith<ImageUpdateFailedException> {
                    imageService.patchImage(
                        imageId = 1,
                        managePatchRequest = ImageManagePatchRequest(isPrivate = true),
                    )
                }

            assertTrue(exception.message.contains("Private images cannot be fetched randomly."))
            verify(exactly = 0) { imageDao.updateImageById(any()) }
        }

    private fun testImage(
        isPrivate: Boolean,
        isAllowedRandomFetch: Boolean,
    ) = Image(
        id = 1,
        userId = 10,
        groupId = 100,
        albumId = 1000,
        uniqueName = "0123456789abcdef0123456789abcdef",
        displayName = "test.png",
        description = null,
        path = "test.png",
        strategyId = 10000,
        originName = "test.png",
        mimeType = "image/png",
        extension = "png",
        size = 1,
        width = 1,
        height = 1,
        md5 = "0123456789abcdef0123456789abcdef",
        sha256 = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
        isPrivate = isPrivate,
        isAllowedRandomFetch = isAllowedRandomFetch,
        createTime = LocalDateTime(2026, 4, 19, 0, 0),
    )
}
