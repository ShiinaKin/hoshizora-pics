package io.sakurasou.controller.request

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/11/16 04:07
 */

@Serializable
@Name("PersonalAccessTokenInsertRequest")
data class PersonalAccessTokenInsertRequest(
    val name: String,
    val description: String? = null,
    val expireTime: LocalDateTime,
    val permissions: List<String>
)

@Serializable
@Name("PersonalAccessTokenPatchRequest")
data class PersonalAccessTokenPatchRequest(
    val name: String? = null,
    val description: String? = null
)