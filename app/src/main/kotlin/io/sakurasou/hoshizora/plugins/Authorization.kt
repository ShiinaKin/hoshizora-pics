package io.sakurasou.hoshizora.plugins

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.util.AttributeKey
import io.sakurasou.hoshizora.di.InstanceCenter
import io.sakurasou.hoshizora.exception.controller.access.PrincipalNotFoundException
import io.sakurasou.hoshizora.exception.controller.status.UnauthorizedAccessException
import io.sakurasou.hoshizora.extension.Principal
import io.sakurasou.hoshizora.model.DatabaseSingleton.dbQuery

/**
 * @author Shiina Kin
 * 2024/9/17 18:08
 */

private val logger = KotlinLogging.logger { }

val AuthorizationPlugin =
    createRouteScopedPlugin(
        name = "AuthorizationPlugin",
        createConfiguration = ::AuthorizationConfig,
    ) {
        val permission = pluginConfig.permission
        on(AuthenticationChecked) { call ->
            val principal = call.principal<JWTPrincipal>() ?: throw PrincipalNotFoundException()

            val id = principal.payload.getClaim("id").asLong()
            val groupId = principal.payload.getClaim("groupId").asLong()
            val username = principal.payload.getClaim("username").asString()

            val patId = principal.payload.getClaim("patId")?.asLong()
            val roles = principal.payload.getClaim("roles")?.asList(String::class.java)
            val permissions = principal.payload.getClaim("permissions")?.asList(String::class.java)

            call.attributes.put(AttributeKey("principal"), Principal(id, groupId, username))

            if (patId != null) {
                dbQuery {
                    InstanceCenter.personalAccessTokenDao.findPATById(patId) ?: throw UnauthorizedAccessException()
                }
                if (!permissions!!.contains(permission)) throw UnauthorizedAccessException()
            } else {
                roles!!.forEach {
                    runCatching {
                        val rolePermissions = dbQuery { InstanceCenter.relationDao.listPermissionByRole(it) }
                        if (permission !in rolePermissions) throw UnauthorizedAccessException()
                    }.onFailure { exception ->
                        if (exception !is UnauthorizedAccessException) logger.trace { exception }
                        throw UnauthorizedAccessException()
                    }
                }
            }
        }
    }

class AuthorizationConfig {
    lateinit var permission: String
}
