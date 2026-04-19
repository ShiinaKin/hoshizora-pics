package io.sakurasou.hoshizora.util

import com.auth0.jwt.exceptions.JWTVerificationException
import io.sakurasou.hoshizora.config.JwtConfig
import io.sakurasou.hoshizora.model.entity.User
import kotlinx.datetime.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * @author Shiina Kin
 * 2026/4/19 00:00
 */
class JwtUtilsTest {
    @BeforeTest
    fun setUp() {
        JwtConfig.init(
            jwtSecret = "test-secret",
            jwtIssuer = "hoshizora-test",
            jwtAudience = "hoshizora-client",
            jwtRealm = "hoshizora",
        )
    }

    @Test
    fun `generateJwtToken should create verifiable role token`() {
        val token = JwtUtils.generateJwtToken(testUser(), listOf("ROLE_USER", "ROLE_ADMIN"), "1h")

        val decoded = JwtUtils.verifier().verify(token)

        assertEquals("hoshizora-test", decoded.issuer)
        assertEquals(listOf("hoshizora-client"), decoded.audience)
        assertEquals(7L, decoded.getClaim("id").asLong())
        assertEquals("shiina", decoded.getClaim("username").asString())
        assertEquals(3L, decoded.getClaim("groupId").asLong())
        assertEquals(listOf("ROLE_USER", "ROLE_ADMIN"), decoded.getClaim("roles").asList(String::class.java))
    }

    @Test
    fun `generateJwtToken should create verifiable PAT token`() {
        val token =
            JwtUtils.generateJwtToken(
                user = testUser(),
                patId = 11L,
                permissions = listOf("image:read", "album:write"),
                expireTime = LocalDateTime(2099, 1, 1, 0, 0),
            )

        val decoded = JwtUtils.verifier().verify(token)

        assertEquals(7L, decoded.getClaim("id").asLong())
        assertEquals("shiina", decoded.getClaim("username").asString())
        assertEquals(3L, decoded.getClaim("groupId").asLong())
        assertEquals(11L, decoded.getClaim("patId").asLong())
        assertEquals(listOf("image:read", "album:write"), decoded.getClaim("permissions").asList(String::class.java))
    }

    @Test
    fun `verifier should reject token signed with a different secret`() {
        val token = JwtUtils.generateJwtToken(testUser(), listOf("ROLE_USER"), "1h")
        JwtConfig.init(
            jwtSecret = "other-secret",
            jwtIssuer = "hoshizora-test",
            jwtAudience = "hoshizora-client",
            jwtRealm = "hoshizora",
        )

        assertFailsWith<JWTVerificationException> {
            JwtUtils.verifier().verify(token)
        }
    }

    private fun testUser(): User {
        val now = LocalDateTime(2026, 4, 19, 0, 0)
        return User(
            id = 7L,
            groupId = 3L,
            name = "shiina",
            password = "hashed",
            email = "shiina@example.com",
            isDefaultImagePrivate = true,
            defaultAlbumId = 5L,
            isBanned = false,
            createTime = now,
            updateTime = now,
        )
    }
}
