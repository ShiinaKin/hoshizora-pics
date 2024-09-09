package io.sakurasou.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 11:25
 */
@Serializable
data class GroupInsertRequest(
    val name: String,
    val description: String? = null,
    val strategy: Long,
    val maxSize: Double,
    val roles: List<Long>
)

@Serializable
data class GroupPatchRequest(
    val name: String? = null,
    val description: String? = null,
    val strategy: Long? = null,
    val maxSize: Double? = null,
    val roles: List<Long>? = null
)