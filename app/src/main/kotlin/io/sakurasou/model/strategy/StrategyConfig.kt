package io.sakurasou.model.strategy

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:32
 */
@Serializable
sealed class StrategyConfig

enum class StrategyType {
    LOCAL,
    S3
}