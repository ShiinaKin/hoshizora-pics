package io.sakurasou.controller.request

/**
 * @author Shiina Kin
 * 2024/9/9 12:30
 */
data class StrategyInsertRequest(
    val name: String,
    val type: String,
    val config: String
)

data class StrategyPatchRequest(
    val name: String? = null,
    val config: String? = null,
)