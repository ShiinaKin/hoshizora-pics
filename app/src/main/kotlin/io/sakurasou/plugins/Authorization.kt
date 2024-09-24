package io.sakurasou.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.*
import io.sakurasou.config.InstanceCenter
import io.sakurasou.exception.controller.access.PrincipalNotFoundException
import io.sakurasou.exception.controller.status.UnauthorizedAccessException
import io.sakurasou.extension.Principal

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
            val roles: List<String> = principal.payload.getClaim("roles").asList(String::class.java)
            call.attributes.put(AttributeKey("principal"), Principal(id, groupId, username, roles))

            roles.forEach {
                val permissions = InstanceCenter.rolePermissions[it]!!
                if (permission !in permissions) throw UnauthorizedAccessException()
            }
        }
    }
}

class AuthorizationConfig {
    lateinit var permission: String
}