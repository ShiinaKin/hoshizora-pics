package io.sakurasou.model.entity

import io.sakurasou.model.strategy.StrategyConfig
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/7 13:32
 */
@Serializable
data class Strategy(
    val id: Long,
    val name: String,
    val isSystemReserved: Boolean,
    val config: StrategyConfig,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
)