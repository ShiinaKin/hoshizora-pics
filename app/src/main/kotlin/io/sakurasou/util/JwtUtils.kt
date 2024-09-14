package io.sakurasou.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.sakurasou.config.JwtConfig.audience
import io.sakurasou.config.JwtConfig.issuer
import io.sakurasou.config.JwtConfig.secret
import io.sakurasou.model.entity.User
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import kotlin.time.Duration

/**
 * @author ShiinaKin
 * 2024/9/14 13:06
 */
object JwtUtils {
    fun generateJwtToken(user: User, roles: List<String>): String {
        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("id", user.id)
            .withClaim("username", user.name)
            .withClaim("groupId", user.groupId)
            .withClaim("roles", roles)
            .withExpiresAt(Clock.System.now().plus(Duration.parse("3d")).toJavaInstant())
            .sign(Algorithm.HMAC256(secret))
        return token
    }

    fun verifier(): JWTVerifier = JWT
        .require(Algorithm.HMAC256(secret))
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
}