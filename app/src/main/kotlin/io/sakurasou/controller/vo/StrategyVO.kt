package io.sakurasou.controller.vo

import io.github.smiley4.schemakenerator.core.annotations.Name
import io.sakurasou.model.strategy.StrategyConfig
import io.sakurasou.model.strategy.StrategyType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:31
 */
@Serializable
@Name("StrategyVO")
data class StrategyVO(
    val id: Long,
    val name: String,
    val config: StrategyConfig,
    val type: StrategyType,
    val createTime: LocalDateTime
)

@Serializable
@Name("StrategyPageVO")
data class StrategyPageVO(
    val id: Long,
    val name: String,
    val isSystemReserved: Boolean,
    val type: StrategyType,
    val createTime: LocalDateTime
)