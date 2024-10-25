package io.sakurasou.controller.request

import io.github.smiley4.schemakenerator.core.annotations.Name
import io.sakurasou.model.strategy.StrategyConfig
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:30
 */
@Serializable
@Name("StrategyInsertRequest")
data class StrategyInsertRequest(
    val name: String,
    val config: StrategyConfig
)

@Serializable
@Name("StrategyPatchRequest")
data class StrategyPatchRequest(
    val name: String? = null,
    val config: StrategyConfig? = null,
)