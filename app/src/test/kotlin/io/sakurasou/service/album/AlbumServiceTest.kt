package io.sakurasou.service.album

import io.mockk.*
import io.sakurasou.controller.request.AlbumSelfInsertRequest
import io.sakurasou.controller.request.AlbumSelfPatchRequest
import io.sakurasou.controller.vo.AlbumVO
import io.sakurasou.exception.service.album.AlbumAccessDeniedException
import io.sakurasou.exception.service.album.AlbumDeleteFailedException
import io.sakurasou.exception.service.album.AlbumInsertFailedException
import io.sakurasou.exception.service.album.AlbumUpdateFailedException
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.image.ImageDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.AlbumInsertDTO
import io.sakurasou.model.dto.AlbumUpdateDTO
import io.sakurasou.model.entity.Album
import io.sakurasou.model.entity.User
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * @author Shiina Kin
 * 2024/9/13 21:47
 */
class AlbumServiceTest {
    private lateinit var userDao: UserDao
    private lateinit var albumDao: AlbumDao
    private lateinit var imageDao: ImageDao
    private lateinit var albumService: AlbumServiceImpl
    private lateinit var instant: Instant
    private lateinit var now: LocalDateTime

    @BeforeTest
    fun setUp() {
        mockkObject(DatabaseSingleton)
        mockkObject(Clock.System)
        userDao = mockk()
        albumDao = mockk()
        imageDao = mockk()
        albumService = AlbumServiceImpl(userDao, albumDao, imageDao)
        instant = Clock.System.now()
        every { Clock.System.now() } returns instant
        now = instant.toLocalDateTime(TimeZone.UTC)
        coEvery { DatabaseSingleton.dbQuery<Unit>(any()) } coAnswers {
            this.arg<suspend () -> Unit>(0).invoke()
        }
    }

    @Test
    fun `saveSelf should insert album correctly`() = runBlocking {
        val userId = 1L
        val request = AlbumSelfInsertRequest(name = "My Album", description = "Test Album")
        val expectedDTO = AlbumInsertDTO(
            userId = userId,
            name = "My Album",
            description = "Test Album",
            imageCount = 0,
            isUncategorized = false,
            createTime = now
        )

        coEvery { DatabaseSingleton.dbQuery<Long>(any()) } coAnswers {
            this.arg<suspend () -> Long>(0).invoke()
        }
        every { albumDao.saveAlbum(expectedDTO) } returns 1L

        albumService.saveSelf(userId, request)

        verify(exactly = 1) { albumDao.saveAlbum(expectedDTO) }
    }

    @Test
    fun `saveSelf should throw AlbumInsertFailedException when save fails`() = runBlocking {
        val userId = 1L
        val request = AlbumSelfInsertRequest(name = "My Album", description = "Test Album")
        val expectedDTO = AlbumInsertDTO(
            userId = userId,
            name = "My Album",
            description = "Test Album",
            imageCount = 0,
            isUncategorized = false,
            createTime = now
        )

        every { albumDao.saveAlbum(expectedDTO) } throws RuntimeException("DB failure")

        assertFailsWith<AlbumInsertFailedException> {
            albumService.saveSelf(userId, request)
        }

        verify(exactly = 1) { albumDao.saveAlbum(expectedDTO) }
    }

    @Test
    fun `deleteSelf should delete album correctly when user is owner`() = runBlocking {
        val userId = 1L
        val albumId = 2L
        val album = Album(
            id = albumId,
            userId = userId,
            name = "My Album",
            description = "Test Album",
            isUncategorized = false,
            createTime = now
        )
        val uncategorizedAlbum = album.copy(id = 3L)

        every { albumDao.findAlbumById(albumId) } returns album
        every { albumDao.findDefaultAlbumByUserId(userId) } returns uncategorizedAlbum
        every { imageDao.updateAlbumIdByAlbumId(albumId, uncategorizedAlbum.id) } returns 1
        every { albumDao.deleteAlbumById(albumId) } returns 1

        albumService.deleteSelf(userId, albumId)

        verifyOrder {
            albumDao.findAlbumById(albumId)
            albumDao.findDefaultAlbumByUserId(userId)
            imageDao.updateAlbumIdByAlbumId(albumId, uncategorizedAlbum.id)
            albumDao.deleteAlbumById(albumId)
        }
    }

    @Test
    fun `deleteSelf should throw AlbumDeleteFailedException when album does not exist`() = runBlocking {
        val userId = 1L
        val albumId = 2L

        every { albumDao.findAlbumById(albumId) } returns null

        assertFailsWith<AlbumDeleteFailedException> {
            albumService.deleteSelf(userId, albumId)
        }

        verify(exactly = 1) { albumDao.findAlbumById(albumId) }
    }

    @Test
    fun `patchSelf should update album correctly when user is owner and data is valid`() = runBlocking {
        val userId = 1L
        val albumId = 2L
        val album = Album(
            id = albumId,
            userId = userId,
            name = "Old Album",
            description = "Old Description",
            isUncategorized = false,
            createTime = now
        )
        val patchRequest = AlbumSelfPatchRequest(name = "New Album", description = "New Description")
        val exceptedUpdateDTO = AlbumUpdateDTO(
            id = albumId,
            userId = userId,
            name = "New Album",
            description = "New Description"
        )

        coEvery { DatabaseSingleton.dbQuery<AlbumVO>(any()) } coAnswers {
            this.arg<suspend () -> AlbumVO>(0).invoke()
        }
        every { albumDao.findAlbumById(albumId) } returns album
        every { albumDao.updateAlbumById(any()) } returns 1

        albumService.patchSelf(userId, albumId, patchRequest)

        verify(exactly = 1) { albumDao.updateAlbumById(exceptedUpdateDTO) }
    }

    @Test
    fun `patchSelf should throw AlbumAccessDeniedException when user is not owner`() = runBlocking {
        val userId = 1L
        val anotherUserId = 2L
        val albumId = 3L
        val album = Album(
            id = albumId,
            userId = anotherUserId,
            name = "Old Album",
            description = "Old Description",
            isUncategorized = false,
            createTime = now
        )
        val patchRequest = AlbumSelfPatchRequest(name = "New Album", description = "New Description")

        coEvery { DatabaseSingleton.dbQuery<AlbumVO>(any()) } coAnswers {
            this.arg<suspend () -> AlbumVO>(0).invoke()
        }
        every { albumDao.findAlbumById(albumId) } returns album

        assertFailsWith<AlbumUpdateFailedException> {
            albumService.patchSelf(userId, albumId, patchRequest)
        }

        verify(exactly = 1) { albumDao.findAlbumById(albumId) }
    }

    @Test
    fun `fetchSelf should return correct AlbumVO when user is owner`() = runBlocking {
        val userId = 1L
        val albumId = 2L
        val album = Album(
            id = albumId,
            userId = userId,
            name = "My Album",
            description = "Test Album",
            isUncategorized = false,
            createTime = now
        )
        val user = User(
            id = userId,
            name = "testUser",
            password = "testPassword",
            email = "test@example.com",
            groupId = 1,
            isDefaultImagePrivate = true,
            defaultAlbumId = albumId,
            isBanned = false,
            createTime = now,
            updateTime = now
        )
        val expected = AlbumVO(
            id = albumId,
            name = "My Album",
            description = "Test Album",
            imageCount = 0,
            isUncategorized = false,
            isDefault = true,
            createTime = album.createTime
        )

        coEvery { DatabaseSingleton.dbQuery<AlbumVO>(any()) } coAnswers {
            this.arg<suspend () -> AlbumVO>(0).invoke()
        }
        every { albumDao.findAlbumById(albumId) } returns album
        every { userDao.findUserById(userId) } returns user
        every { imageDao.countImageByAlbumId(albumId) } returns 0

        val result = albumService.fetchSelf(userId, albumId)

        assertEquals(expected, result)
    }

    @Test
    fun `fetchSelf should throw AlbumAccessDeniedException when user is not owner`(): Unit = runBlocking {
        val userId = 1L
        val anotherUserId = 2L
        val albumId = 3L
        val album = Album(
            id = albumId,
            userId = anotherUserId,
            name = "Old Album",
            description = "Old Description",
            isUncategorized = false,
            createTime = now
        )
        val user = User(
            id = userId,
            name = "testUser",
            password = "testPassword",
            email = "test@example.com",
            groupId = 1,
            isDefaultImagePrivate = true,
            defaultAlbumId = albumId,
            isBanned = false,
            createTime = now,
            updateTime = now
        )

        coEvery { DatabaseSingleton.dbQuery<Album>(any()) } coAnswers {
            this.arg<suspend () -> Album>(0).invoke()
        }
        every { albumDao.findAlbumById(albumId) } returns album
        every { userDao.findUserById(userId) } returns user

        assertFailsWith<AlbumAccessDeniedException> {
            albumService.fetchSelf(userId, albumId)
        }
    }
}