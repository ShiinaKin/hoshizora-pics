package io.sakurasou.model.strategy

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 09:26
 */
@Serializable
@SerialName("LOCAL")
@Name("LocalStrategy")
data class LocalStrategy(
    val uploadFolder: String,
    val thumbnailFolder: String,
) : StrategyConfig(StrategyType.LOCAL)
