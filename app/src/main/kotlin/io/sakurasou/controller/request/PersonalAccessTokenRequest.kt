package io.sakurasou.controller.request

import kotlinx.datetime.LocalDateTime

/**
 * @author ShiinaKin
 * 2024/11/16 04:07
 */

data class PersonalAccessTokenInsertRequest(
    val name: String,
    val description: String?,
    val expireTime: LocalDateTime,
    val permissions: List<String>
)

data class PersonalAccessTokenPatchRequest(
    val name: String?,
    val description: String?
)