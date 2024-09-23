package io.sakurasou.model.dto

import io.sakurasou.model.strategy.StrategyConfig
import kotlinx.datetime.LocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/12 13:47
 */
data class StrategyInsertDTO(
    val name: String,
    val config: StrategyConfig,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
)

data class StrategyUpdateDTO(
    val id: Long,
    val name: String,
    val config: StrategyConfig,
    val updateTime: LocalDateTime
)