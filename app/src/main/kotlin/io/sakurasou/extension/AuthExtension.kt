package io.sakurasou.extension

import io.ktor.server.application.*
import io.ktor.util.*
import io.sakurasou.exception.controller.access.PrincipalNotFoundException
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/12 11:51
 */
@Serializable
data class Principal(
    val id: Long,
    val groupId: Long,
    val username: String,
)

fun ApplicationCall.getPrincipal(): Principal {
    return attributes.getOrNull(AttributeKey("principal")) ?: throw PrincipalNotFoundException()
}