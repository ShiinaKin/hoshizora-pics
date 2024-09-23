package io.sakurasou.controller.request

import io.sakurasou.model.strategy.StrategyConfig
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:30
 */
@Serializable
data class StrategyInsertRequest(
    val name: String,
    val config: StrategyConfig
)

@Serializable
data class StrategyPatchRequest(
    val name: String? = null,
    val config: StrategyConfig? = null,
)