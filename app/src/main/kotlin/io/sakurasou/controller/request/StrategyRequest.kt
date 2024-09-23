package io.sakurasou.controller.request

import io.sakurasou.model.strategy.StrategyConfig

/**
 * @author Shiina Kin
 * 2024/9/9 12:30
 */
data class StrategyInsertRequest(
    val name: String,
    val config: StrategyConfig
)

data class StrategyPatchRequest(
    val name: String? = null,
    val config: StrategyConfig? = null,
)