package io.sakurasou.model.strategy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 09:26
 */
@Serializable
@SerialName("LOCAL")
data class LocalStrategy(
    val uploadFolder: String
) : StrategyConfig(StrategyType.LOCAL)