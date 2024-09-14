package io.sakurasou.extension

import io.ktor.util.*
import io.sakurasou.config.InstanceCenter
import io.sakurasou.exception.PrincipalNotExistException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/12 11:51
 */
@Serializable
data class Principal(val id: Long, val groupId: Long, val username: String, val roles: List<String>)

fun Attributes.getPrincipal(): Principal {
    if (!contains(AttributeKey<Long>("id"))
        || !contains(AttributeKey<Long>("groupId"))
        || !contains(AttributeKey<String>("username"))
        || !contains(AttributeKey<List<String>>("roles"))
    ) throw PrincipalNotExistException()
    return Principal(
        get(AttributeKey("id")),
        get(AttributeKey("groupId")),
        get(AttributeKey("username")),
        get(AttributeKey("roles"))
    )
}

suspend fun lackPermission(attributes: Attributes, permission: String): Boolean {
    val principal = attributes.getPrincipal()
    val roles = principal.roles
    roles.forEach { role ->
        val permissions = dbQuery {
            InstanceCenter.relationDao.listPermissionByRole(role)
        }
        if (permissions.contains(permission)) return false
    }
    return true
}