package io.sakurasou.service.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.sakurasou.config.JwtConfig.audience
import io.sakurasou.config.JwtConfig.issuer
import io.sakurasou.config.JwtConfig.jwkProvider
import io.sakurasou.controller.request.UserLoginRequest
import io.sakurasou.exception.UnauthorizedAccessException
import io.sakurasou.exception.UserNotFoundException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.entity.User
import io.sakurasou.util.JwtUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import kotlin.time.Duration

/**
 * @author Shiina Kin
 * 2024/9/12 12:55
 */
class AuthServiceImpl(
    private val userDao: UserDao,
    private val relationDao: RelationDao
) : AuthService {
    override suspend fun login(loginRequest: UserLoginRequest): String {
        val user = dbQuery {
            userDao.findUserByName(loginRequest.username) ?: run { throw UserNotFoundException() }
        }

        val isCorrectPassword = BCrypt.verifyer().verify(loginRequest.password.toCharArray(), user.password)
        if (!isCorrectPassword.verified) throw UnauthorizedAccessException()

        val roles: List<String> = dbQuery {
            relationDao.listRoleByGroupId(user.groupId)
        }

        return JwtUtils.generateJwtToken(user, roles)
    }
}