package io.sakurasou.controller.vo

import io.sakurasou.model.strategy.StrategyConfig
import io.sakurasou.model.strategy.StrategyType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement

/**
 * @author Shiina Kin
 * 2024/9/9 12:31
 */
data class StrategyVO(
    val id: Long,
    val name: String,
    val config: StrategyConfig,
    val type: StrategyType,
    val createTime: LocalDateTime
)

data class StrategyPageVO(
    val id: Long,
    val name: String,
    val type: StrategyType
)