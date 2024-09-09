package io.sakurasou.controller.vo

import io.sakurasou.model.strategy.StrategyConfig
import kotlinx.serialization.json.JsonElement

/**
 * @author Shiina Kin
 * 2024/9/9 12:31
 */
data class StrategyVO(
    val id: Long,
    val name: String,
    val type: StrategyConfig,
    val config: JsonElement
)

data class StrategyPageVO(
    val id: Long,
    val name: String,
    val type: StrategyConfig
)