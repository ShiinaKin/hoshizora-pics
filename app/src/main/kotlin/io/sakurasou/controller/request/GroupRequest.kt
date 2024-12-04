package io.sakurasou.controller.request

import io.github.smiley4.schemakenerator.core.annotations.Name
import io.sakurasou.model.group.GroupConfig
import io.sakurasou.model.group.GroupStrategyConfig
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 11:25
 */
@Serializable
@Name("GroupInsertRequest")
data class GroupInsertRequest(
    val name: String,
    val description: String? = null,
    val config: GroupConfig,
    val strategyId: Long,
    val roles: List<String>
)

@Serializable
@Name("GroupPatchRequest")
data class GroupPatchRequest(
    val name: String? = null,
    val description: String? = null,
    val strategyId: Long? = null,
    val config: GroupConfigUpdatePatch? = null,
    val roles: List<String>? = null
)

@Serializable
@Name("GroupPutRequest")
data class GroupPutRequest(
    val name: String,
    val description: String? = null,
    val strategyId: Long,
    val config: GroupConfig,
    val roles: List<String>
)

@Serializable
@Name("GroupConfigUpdatePatch")
data class GroupConfigUpdatePatch(
    val groupStrategyConfig: GroupStrategyConfig? = null
)
