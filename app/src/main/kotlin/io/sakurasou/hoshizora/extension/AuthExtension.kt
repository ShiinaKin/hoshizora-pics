package io.sakurasou.hoshizora.extension

import io.ktor.server.application.ApplicationCall
import io.ktor.util.AttributeKey
import io.sakurasou.hoshizora.exception.controller.access.PrincipalNotFoundException
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

fun ApplicationCall.getPrincipal(): Principal = attributes.getOrNull(AttributeKey("principal")) ?: throw PrincipalNotFoundException()
