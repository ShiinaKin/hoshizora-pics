package io.sakurasou.hoshizora.service.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import io.sakurasou.hoshizora.controller.request.UserLoginRequest
import io.sakurasou.hoshizora.exception.common.UserBannedException
import io.sakurasou.hoshizora.exception.service.auth.LoginFailedException
import io.sakurasou.hoshizora.model.DatabaseSingleton.dbQuery
import io.sakurasou.hoshizora.model.dao.relation.RelationDao
import io.sakurasou.hoshizora.model.dao.user.UserDao
import io.sakurasou.hoshizora.util.JwtUtils

/**
 * @author Shiina Kin
 * 2024/9/12 12:55
 */
class AuthServiceImpl(
    private val userDao: UserDao,
    private val relationDao: RelationDao,
) : AuthService {
    override suspend fun login(loginRequest: UserLoginRequest): String =
        dbQuery {
            val user = userDao.findUserByName(loginRequest.username) ?: throw LoginFailedException()

            if (user.isBanned) throw UserBannedException()

            val isCorrectPassword = BCrypt.verifyer().verify(loginRequest.password.toCharArray(), user.password)
            if (!isCorrectPassword.verified) throw LoginFailedException()

            val roles: List<String> = relationDao.listRoleByGroupId(user.groupId)

            JwtUtils.generateJwtToken(user, roles)
        }
}
