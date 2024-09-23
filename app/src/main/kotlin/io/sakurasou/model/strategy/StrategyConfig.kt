package io.sakurasou.model.strategy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:32
 */
@Serializable
@SerialName("StrategyConfig")
sealed class StrategyConfig(
    val strategyType: StrategyType
)

@Serializable
enum class StrategyType {
    LOCAL,
    S3
}