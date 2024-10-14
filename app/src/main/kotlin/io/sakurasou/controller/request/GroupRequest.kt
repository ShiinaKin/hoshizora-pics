package io.sakurasou.controller.request

import io.sakurasou.model.group.GroupStrategyConfig
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 11:25
 */
@Serializable
data class GroupInsertRequest(
    val name: String,
    val description: String? = null,
    val strategyId: Long,
    val roles: List<String>
)

@Serializable
data class GroupPatchRequest(
    val name: String? = null,
    val description: String? = null,
    val strategyId: Long? = null,
    val config: GroupConfigUpdatePatch? = null,
    val roles: List<String>? = null
)

@Serializable
data class GroupConfigUpdatePatch(
    val groupStrategyConfig: GroupStrategyConfig? = null
)
