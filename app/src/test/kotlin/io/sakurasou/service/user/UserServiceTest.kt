package io.sakurasou.service.user

import at.favre.lib.crypto.bcrypt.BCrypt
import io.mockk.*
import io.sakurasou.controller.request.UserInsertRequest
import io.sakurasou.exception.controller.access.SignupNotAllowedException
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.group.GroupDao
import io.sakurasou.model.dao.image.ImageDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.setting.SystemSetting
import io.sakurasou.service.setting.SettingService
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * @author Shiina Kin
 * 2024/9/12 13:38
 */
class UserServiceTest {
    private lateinit var userDao: UserDao
    private lateinit var albumDao: AlbumDao
    private lateinit var groupDao: GroupDao
    private lateinit var imageDao: ImageDao
    private lateinit var settingService: SettingService
    private lateinit var userService: UserServiceImpl

    @BeforeTest
    fun setUp() {
        mockkObject(DatabaseSingleton)
        mockkStatic(BCrypt::class)
        mockkObject(Clock.System)
        userDao = mockk()
        albumDao = mockk()
        groupDao = mockk()
        imageDao = mockk()
        settingService = mockk()
        userService = UserServiceImpl(userDao, groupDao, albumDao, imageDao, settingService)
        coEvery { DatabaseSingleton.dbQuery<Unit>(any()) } coAnswers {
            this.arg<suspend () -> Unit>(0).invoke()
        }
    }

    @Test
    fun `signup should throw SignupNotAllowedException if system not allowed signup`(): Unit = runBlocking {
        val userInsertRequest = UserInsertRequest(
            username = "testUser",
            password = "testPassword",
            email = "test@example.com",
        )
        val systemSetting = SystemSetting(
            defaultGroupId = 2,
            allowSignup = false
        )

        coEvery { settingService.getSystemSetting() } returns systemSetting

        assertFailsWith<SignupNotAllowedException> {
            userService.saveUser(userInsertRequest)
        }
    }

    @Test
    fun `signup should be successful`() = runBlocking {
        val userInsertRequest = UserInsertRequest(
            username = "testUser",
            password = "testPassword",
            email = "test@example.com",
        )
        val systemSetting = SystemSetting(
            defaultGroupId = 2,
            allowSignup = true
        )

        val instant = Clock.System.now()
        val now = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val encodedPassword = "encodedPassword"
        val userInsertDTO = UserInsertDTO(
            groupId = 2,
            username = "testUser",
            password = encodedPassword,
            email = "test@example.com",
            isDefaultImagePrivate = true,
            defaultAlbumId = null,
            isBanned = false,
            createTime = now,
            updateTime = now
        )

        every { Clock.System.now() } returns instant
        coEvery { settingService.getSystemSetting() } returns systemSetting
        every {
            BCrypt.withDefaults().hashToString(12, userInsertRequest.password.toCharArray())
        } returns encodedPassword
        every { userDao.saveUser(userInsertDTO) } returns 1
        every { albumDao.initAlbumForUser(1L) } returns 1
        every { userDao.updateUserDefaultAlbumId(1L, 1L) } returns 1

        userService.saveUser(userInsertRequest)

        coVerify(exactly = 1) { DatabaseSingleton.dbQuery<Unit>(any()) }
        verify(exactly = 1) { userDao.saveUser(userInsertDTO) }
        verify(exactly = 1) { albumDao.initAlbumForUser(1L) }
        verify(exactly = 1) { userDao.updateUserDefaultAlbumId(1L, 1L) }
    }
}