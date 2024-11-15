package io.sakurasou.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.*
import io.sakurasou.di.InstanceCenter
import io.sakurasou.exception.controller.access.PrincipalNotFoundException
import io.sakurasou.exception.controller.status.UnauthorizedAccessException
import io.sakurasou.extension.Principal
import io.sakurasou.model.DatabaseSingleton.dbQuery

/**
 * @author Shiina Kin
 * 2024/9/17 18:08
 */
val AuthorizationPlugin = createRouteScopedPlugin(
    name = "AuthorizationPlugin",
    createConfiguration = ::AuthorizationConfig
) {
    pluginConfig.apply {
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
                    val rolePermissions = InstanceCenter.rolePermissions[it]!!
                    if (permission !in rolePermissions) throw UnauthorizedAccessException()
                }
            }
        }
    }
}

class AuthorizationConfig {
    lateinit var permission: String
}