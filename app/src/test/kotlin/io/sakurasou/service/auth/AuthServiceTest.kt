package io.sakurasou.service.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import io.mockk.*
import io.sakurasou.controller.request.UserLoginRequest
import io.sakurasou.exception.controller.status.UnauthorizedAccessException
import io.sakurasou.exception.service.user.UserNotFoundException
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.entity.User
import io.sakurasou.util.JwtUtils
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * @author Shiina Kin
 * 2024/9/13 21:47
 */
class AuthServiceTest {
    private lateinit var userDao: UserDao
    private lateinit var relationDao: RelationDao
    private lateinit var authService: AuthService

    private val username = "testUser"
    private val password = "testPassword"
    private val email = "test@example.com"

    @BeforeTest
    fun setUp() {
        mockkObject(DatabaseSingleton)
        mockkObject(JwtUtils)
        mockkStatic(BCrypt::class)
        userDao = mockk()
        relationDao = mockk()
        authService = AuthServiceImpl(userDao, relationDao)
    }

    @Test
    fun `login should throw UserNotFoundException for non-existent user`() = runBlocking {
        val userLoginRequest = UserLoginRequest(username, password)

        coEvery { DatabaseSingleton.dbQuery<User>(any()) } coAnswers {
            this.arg<suspend () -> User>(0).invoke()
        }
        coEvery { userDao.findUserByName(username) } returns null

        assertFailsWith<UserNotFoundException> {
            authService.login(userLoginRequest)
        }
        coVerify(exactly = 1) { userDao.findUserByName(username) }
    }

    @Test
    fun `login should throw UnauthorizedAccessException for incorrect password`() = runBlocking {
        val userLoginRequest = UserLoginRequest(username, password)

        val instant = Clock.System.now()
        val now = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val hashedPassword = BCrypt.withDefaults().hashToString(12, "correctPassword".toCharArray())
        val user = User(
            id = 1,
            groupId = 2,
            name = username,
            password = hashedPassword,
            email = email,
            isDefaultImagePrivate = true,
            defaultAlbumId = 1,
            createTime = now,
            updateTime = now
        )

        coEvery { DatabaseSingleton.dbQuery<User>(any()) } coAnswers {
            this.arg<suspend () -> User>(0).invoke()
        }
        coEvery { userDao.findUserByName(username) } returns user

        assertFailsWith<UnauthorizedAccessException> {
            authService.login(userLoginRequest)
        }
        coVerify { userDao.findUserByName(username) }
    }

    @Test
    fun `login should return a JWT token`() = runBlocking {
        val userLoginRequest = UserLoginRequest(username, password)

        val instant = Clock.System.now()
        val now = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        val user = User(
            id = 1,
            groupId = 2,
            name = username,
            password = hashedPassword,
            email = email,
            isDefaultImagePrivate = true,
            defaultAlbumId = 1,
            createTime = now,
            updateTime = now
        )
        val roles = listOf("user")
        val justAJwtToken = "mockJwtToken"

        coEvery { DatabaseSingleton.dbQuery<User>(any()) } coAnswers {
            this.arg<suspend () -> User>(0).invoke()
        }
        coEvery { DatabaseSingleton.dbQuery<List<String>>(any()) } coAnswers {
            this.arg<suspend () -> List<String>>(0).invoke()
        }
        coEvery { userDao.findUserByName(username) } returns user
        coEvery { relationDao.listRoleByGroupId(user.groupId) } returns roles
        every { JwtUtils.generateJwtToken(user, roles) } returns justAJwtToken

        val token = authService.login(userLoginRequest)
        assertEquals(justAJwtToken, token)
    }
}