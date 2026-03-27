package io.sakurasou.hoshizora.controller.request

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/11/16 04:07
 */

@Serializable
data class PersonalAccessTokenInsertRequest(
    val name: String,
    val description: String? = null,
    val expireTime: LocalDateTime,
    val permissions: List<String>,
)

@Serializable
data class PersonalAccessTokenPatchRequest(
    val name: String? = null,
    val description: String? = null,
)
