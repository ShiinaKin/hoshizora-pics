package io.sakurasou.service.user

import at.favre.lib.crypto.bcrypt.BCrypt
import io.mockk.*
import io.sakurasou.controller.request.UserInsertRequest
import io.sakurasou.controller.request.UserManageInsertRequest
import io.sakurasou.controller.request.UserManagePatchRequest
import io.sakurasou.controller.request.UserSelfPatchRequest
import io.sakurasou.controller.vo.UserVO
import io.sakurasou.exception.controller.access.SignupNotAllowedException
import io.sakurasou.exception.service.user.UserDeleteFailedException
import io.sakurasou.exception.service.user.UserInsertFailedException
import io.sakurasou.exception.service.user.UserNotFoundException
import io.sakurasou.exception.service.user.UserUpdateFailedException
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.group.GroupDao
import io.sakurasou.model.dao.image.ImageDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.ImageCountAndTotalSizeDTO
import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.dto.UserManageUpdateDTO
import io.sakurasou.model.dto.UserSelfUpdateDTO
import io.sakurasou.model.entity.Album
import io.sakurasou.model.entity.Group
import io.sakurasou.model.entity.User
import io.sakurasou.model.group.GroupConfig
import io.sakurasou.model.group.GroupStrategyConfig
import io.sakurasou.model.setting.SystemSetting
import io.sakurasou.service.setting.SettingService
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
    private lateinit var instant: Instant
    private lateinit var now: LocalDateTime

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
        instant = Clock.System.now()
        every { Clock.System.now() } returns instant
        now = instant.toLocalDateTime(TimeZone.UTC)
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
            allowSignup = false,
            allowRandomFetch = false
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
            allowSignup = true,
            allowRandomFetch = false
        )

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

    @Test
    fun `saveUserManually should be successful`() = runBlocking {
        val userManageInsertRequest = UserManageInsertRequest(
            groupId = 1,
            username = "newUser",
            password = "newPassword123",
            email = "newuser@example.com",
            isDefaultImagePrivate = false
        )
        val encodedPassword = "encodedPassword123"
        val userInsertDTO = UserInsertDTO(
            groupId = userManageInsertRequest.groupId,
            username = userManageInsertRequest.username,
            password = encodedPassword,
            email = userManageInsertRequest.email,
            isDefaultImagePrivate = userManageInsertRequest.isDefaultImagePrivate,
            defaultAlbumId = null,
            isBanned = false,
            createTime = now,
            updateTime = now
        )

        every {
            BCrypt.withDefaults().hashToString(12, userManageInsertRequest.password.toCharArray())
        } returns encodedPassword
        every { userDao.saveUser(userInsertDTO) } returns 1L
        every { albumDao.initAlbumForUser(1L) } returns 1L
        every { userDao.updateUserDefaultAlbumId(1L, 1L) } returns 1

        userService.saveUserManually(userManageInsertRequest)

        verify(exactly = 1) { userDao.saveUser(userInsertDTO) }
        verify(exactly = 1) { albumDao.initAlbumForUser(1L) }
        verify(exactly = 1) { userDao.updateUserDefaultAlbumId(1L, 1L) }
    }

    @Test
    fun `saveUserManually should throw UserInsertFailedException`() = runBlocking {
        val userManageInsertRequest = UserManageInsertRequest(
            groupId = 1,
            username = "newUser",
            password = "newPassword123",
            email = "newuser@example.com",
            isDefaultImagePrivate = false
        )
        val encodedPassword = "encodedPassword123"
        val userInsertDTO = UserInsertDTO(
            groupId = userManageInsertRequest.groupId,
            username = userManageInsertRequest.username,
            password = encodedPassword,
            email = userManageInsertRequest.email,
            isDefaultImagePrivate = userManageInsertRequest.isDefaultImagePrivate,
            defaultAlbumId = null,
            isBanned = false,
            createTime = now,
            updateTime = now
        )

        every {
            BCrypt.withDefaults().hashToString(12, userManageInsertRequest.password.toCharArray())
        } returns encodedPassword
        every { userDao.saveUser(userInsertDTO) } throws RuntimeException("some database error")

        assertFailsWith<UserInsertFailedException> {
            userService.saveUserManually(userManageInsertRequest)
        }

        verify(exactly = 1) { userDao.saveUser(userInsertDTO) }
        verify(exactly = 0) { albumDao.initAlbumForUser(any()) }
        verify(exactly = 0) { userDao.updateUserDefaultAlbumId(any(), any()) }
    }

    @Test
    fun `deleteUser should delete user successfully when user exists`() = runBlocking {
        val userId = 1L
        every { imageDao.deleteImageByUserId(userId) } returns 1
        every { userDao.deleteUserById(userId) } returns 1

        userService.deleteUser(userId)

        verify(exactly = 1) { userDao.deleteUserById(userId) }
    }

    @Test
    fun `deleteUser should throw UserDeleteFailedException when user not found`() = runBlocking {
        val userId = 1L
        every { imageDao.deleteImageByUserId(userId) } returns 1
        every { userDao.deleteUserById(userId) } returns 0

        assertFailsWith<UserDeleteFailedException> {
            userService.deleteUser(userId)
        }

        verify(exactly = 1) { userDao.deleteUserById(userId) }
    }

    @Test
    fun `patchSelf should update user successfully when all data is valid`() = runBlocking {
        val userId = 1L
        val oldUserInfo = User(
            id = userId,
            name = "oldUser",
            password = "oldPassword",
            email = "old@example.com",
            isDefaultImagePrivate = false,
            defaultAlbumId = 1,
            groupId = 1,
            isBanned = false,
            updateTime = now,
            createTime = now
        )
        val newAlbum = Album(
            id = 2,
            name = "newAlbum",
            userId = userId,
            createTime = now,
            isUncategorized = false,
        )
        val patchRequest = UserSelfPatchRequest(
            password = "newPassword",
            email = "new@example.com",
            isDefaultImagePrivate = true,
            defaultAlbumId = 2
        )
        val encodedPassword = "encodedNewPassword"

        val exceptedUpdateDTO = UserSelfUpdateDTO(
            id = userId,
            password = encodedPassword,
            email = patchRequest.email ?: oldUserInfo.email,
            isDefaultImagePrivate = true,
            defaultAlbumId = patchRequest.defaultAlbumId,
            updateTime = now
        )

        every { albumDao.findAlbumById(exceptedUpdateDTO.defaultAlbumId!!) } returns newAlbum
        every { userDao.findUserById(userId) } returns oldUserInfo
        every { BCrypt.withDefaults().hashToString(12, patchRequest.password!!.toCharArray()) } returns encodedPassword
        every { userDao.updateSelfById(exceptedUpdateDTO) } returns 1

        userService.patchSelf(userId, patchRequest)

        verify(exactly = 1) { userDao.updateSelfById(exceptedUpdateDTO) }
    }

    @Test
    fun `patchSelf should throw UserNotFoundException if user does not exist`(): Unit = runBlocking {
        val userId = 1L
        val patchRequest = UserSelfPatchRequest()

        every { userDao.findUserById(userId) } returns null

        assertFailsWith<UserNotFoundException> {
            userService.patchSelf(userId, patchRequest)
        }
    }

    @Test
    fun `patchSelf should throw UserUpdateFailedException if user does not exist`(): Unit = runBlocking {
        val userId = 1L
        val patchRequest = UserSelfPatchRequest()
        val oldUser = User(
            id = userId,
            name = "oldUser",
            password = "oldPassword",
            email = "old@example.com",
            isDefaultImagePrivate = false,
            defaultAlbumId = 1,
            groupId = 1,
            isBanned = false,
            updateTime = now,
            createTime = now
        )

        every { userDao.findUserById(userId) } returns oldUser
        every { userDao.updateSelfById(any()) } returns 0

        assertFailsWith<UserUpdateFailedException> {
            userService.patchSelf(userId, patchRequest)
        }
    }

    @Test
    fun `patchSelf should retain old password if new password is not provided`() = runBlocking {
        val userId = 1L
        val oldPassword = "oldPassword"
        val oldUserInfo = User(
            id = userId,
            name = "oldUser",
            password = oldPassword,
            email = "old@example.com",
            isDefaultImagePrivate = false,
            defaultAlbumId = 1,
            groupId = 1,
            isBanned = false,
            updateTime = now,
            createTime = now
        )
        val patchRequest = UserSelfPatchRequest(
            email = "new@example.com"
        )

        every { userDao.findUserById(userId) } returns oldUserInfo
        every { userDao.updateSelfById(any()) } returns 1

        userService.patchSelf(userId, patchRequest)

        verify(exactly = 1) {
            userDao.updateSelfById(match { it.password == oldPassword })
        }
    }

    @Test
    fun `patchUser should update user successfully with all new data`() = runBlocking {
        val userId = 1L
        val oldUserInfo = User(
            id = userId,
            name = "oldUser",
            password = "oldPassword",
            email = "old@example.com",
            isDefaultImagePrivate = false,
            defaultAlbumId = 1,
            groupId = 1,
            isBanned = false,
            updateTime = now,
            createTime = now
        )
        val newAlbum = Album(
            id = 2,
            name = "newAlbum",
            userId = userId,
            createTime = now,
            isUncategorized = false,
        )
        val patchRequest = UserManagePatchRequest(
            groupId = 2,
            password = "newPassword",
            email = "new@example.com",
            isDefaultImagePrivate = true,
            defaultAlbumId = 2
        )
        val encodedPassword = "encodedNewPassword"

        val exceptedUpdateDTO = UserManageUpdateDTO(
            id = userId,
            groupId = 2,
            password = encodedPassword,
            email = patchRequest.email ?: oldUserInfo.email,
            isDefaultImagePrivate = true,
            defaultAlbumId = patchRequest.defaultAlbumId,
            updateTime = now
        )

        every { albumDao.findAlbumById(exceptedUpdateDTO.defaultAlbumId!!) } returns newAlbum
        every { userDao.findUserById(userId) } returns oldUserInfo
        every { BCrypt.withDefaults().hashToString(12, patchRequest.password!!.toCharArray()) } returns encodedPassword
        every { userDao.updateUserById(exceptedUpdateDTO) } returns 1
        every { imageDao.updateImageGroupIdByUserId(userId, 2) } returns 1

        userService.patchUser(userId, patchRequest)

        verify(exactly = 1) { userDao.updateUserById(exceptedUpdateDTO) }
        coVerify(exactly = 1) { imageDao.updateImageGroupIdByUserId(userId, 2) }
    }

    @Test
    fun `patchUser should throw UserNotFoundException if user does not exist`(): Unit = runBlocking {
        val userId = 1L
        val patchRequest = UserManagePatchRequest()

        every { userDao.findUserById(userId) } returns null

        assertFailsWith<UserNotFoundException> {
            userService.patchUser(userId, patchRequest)
        }
    }

    @Test
    fun `patchUser should throw UserUpdateFailedException if user does not exist`(): Unit = runBlocking {
        val userId = 1L
        val oldUserInfo = User(
            id = userId,
            name = "oldUser",
            password = "oldPassword",
            email = "old@example.com",
            isDefaultImagePrivate = false,
            defaultAlbumId = 1,
            groupId = 1,
            isBanned = false,
            updateTime = now,
            createTime = now
        )
        val patchRequest = UserManagePatchRequest()

        every { userDao.findUserById(userId) } returns oldUserInfo
        every { userDao.updateUserById(any()) } returns 0

        assertFailsWith<UserUpdateFailedException> {
            userService.patchUser(userId, patchRequest)
        }
    }

    @Test
    fun `patchUser should retain old password if new password is not provided`() = runBlocking {
        val userId = 1L
        val oldPassword = "oldPassword"
        val oldUserInfo = User(
            id = userId,
            name = "oldUser",
            password = oldPassword,
            email = "old@example.com",
            isDefaultImagePrivate = false,
            defaultAlbumId = 1,
            groupId = 1,
            isBanned = false,
            updateTime = now,
            createTime = now
        )
        val patchRequest = UserManagePatchRequest(
            email = "new@example.com"
        )

        every { userDao.findUserById(userId) } returns oldUserInfo
        every { userDao.updateUserById(any()) } returns 1

        userService.patchUser(userId, patchRequest)

        verify(exactly = 1) {
            userDao.updateUserById(match { it.password == oldPassword })
        }
    }

    @Test
    fun `banUser should update ban status to true when user exists`() = runBlocking {
        val userId = 1L

        every { userDao.updateUserBanStatusById(userId, true) } returns 1

        userService.banUser(userId)

        verify(exactly = 1) { userDao.updateUserBanStatusById(userId, true) }
    }

    @Test
    fun `banUser should throw UserUpdateFailedException when user does not exist`(): Unit = runBlocking {
        val userId = 1L

        every { userDao.updateUserBanStatusById(userId, true) } returns 0

        assertFailsWith<UserUpdateFailedException> {
            userService.banUser(userId)
        }
    }

    @Test
    fun `unbanUser should update ban status to false when user exists`() = runBlocking {
        val userId = 1L

        every { userDao.updateUserBanStatusById(userId, false) } returns 1

        userService.unbanUser(userId)

        verify(exactly = 1) { userDao.updateUserBanStatusById(userId, false) }
    }

    @Test
    fun `unbanUser should throw UserUpdateFailedException when user does not exist`(): Unit = runBlocking {
        val userId = 1L

        every { userDao.updateUserBanStatusById(userId, false) } returns 0

        assertFailsWith<UserUpdateFailedException> {
            userService.unbanUser(userId)
        }
    }

    @Test
    fun `fetchUser should return valid UserVO when user exists and all related data is available`() = runBlocking {
        val userId = 1L
        val user = User(
            id = userId,
            name = "oldUser",
            password = "oldPassword",
            email = "old@example.com",
            isDefaultImagePrivate = false,
            defaultAlbumId = 1,
            groupId = 1,
            isBanned = false,
            updateTime = now,
            createTime = now
        )
        val group = Group(
            id = 2,
            name = "UserGroup",
            description = null,
            strategyId = 1,
            config = GroupConfig(GroupStrategyConfig())
        )
        val imageCountAndTotalSizeDTO = ImageCountAndTotalSizeDTO(10, 204800)

        val exceptedUserVO = UserVO(
            id = userId,
            username = "oldUser",
            groupName = "UserGroup",
            email = "old@example.com",
            isDefaultImagePrivate = false,
            isBanned = false,
            createTime = now,
            imageCount = imageCountAndTotalSizeDTO.count,
            albumCount = 1,
            totalImageSize = imageCountAndTotalSizeDTO.totalSize / 1024 / 1024.0,
            allSize = group.config.groupStrategyConfig.maxSize / 1024 / 1024.0
        )

        coEvery { DatabaseSingleton.dbQuery<UserVO>(any()) } coAnswers {
            this.arg<suspend () -> UserVO>(0).invoke()
        }
        every { userDao.findUserById(userId) } returns user
        every { albumDao.countAlbumByUserId(userId) } returns 1L
        every { groupDao.findGroupById(user.groupId) } returns group
        every { imageDao.getImageCountAndTotalSizeOfUser(userId) } returns imageCountAndTotalSizeDTO

        val result = userService.fetchUser(userId)

        assertEquals(exceptedUserVO, result)
    }

    @Test
    fun `fetchUser should throw UserNotFoundException when user does not exist`(): Unit = runBlocking {
        val userId = 1L

        coEvery { userDao.findUserById(userId) } returns null

        assertFailsWith<UserNotFoundException> {
            userService.fetchUser(userId)
        }
    }
}