package io.sakurasou.extension

import io.ktor.util.*
import io.sakurasou.config.InstanceCenter
import io.sakurasou.exception.PrincipalNotExistException
import io.sakurasou.model.DatabaseSingleton.dbQuery

/**
 * @author Shiina Kin
 * 2024/9/12 11:51
 */
fun Attributes.getPrincipal(): Triple<String, String, List<String>> {
    if (!contains(AttributeKey<String>("id"))
        || !contains(AttributeKey<String>("username"))
        || !contains(AttributeKey<List<String>>("role"))
    ) throw PrincipalNotExistException()
    return Triple(
        this[AttributeKey("id")],
        this[AttributeKey("username")],
        this[AttributeKey("role")]
    )
}

suspend fun lackPermission(attributes: Attributes, permission: String): Boolean {
    val principal = attributes.getPrincipal()
    val roles = principal.third
    roles.forEach { role ->
        val permissions = dbQuery {
            InstanceCenter.relationDao.listPermissionByRole(role)
        }
        if (permissions.contains(permission)) return false
    }
    return true
}