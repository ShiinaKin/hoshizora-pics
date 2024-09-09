package io.sakurasou.model.entity

import io.sakurasou.model.strategy.StrategyConfig
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * @author ShiinaKin
 * 2024/9/7 13:32
 */
@Serializable
data class Strategy(
    val id: Long,
    val name: String,
    val config: StrategyConfig,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
)