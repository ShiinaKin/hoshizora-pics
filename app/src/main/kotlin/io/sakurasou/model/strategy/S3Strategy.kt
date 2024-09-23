package io.sakurasou.model.strategy

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 09:27
 */
@Serializable
data class S3Strategy(
    val endpoint: String,
    val bucketName: String,
    val region: String,
    val accessKey: String,
    val secretKey: String
) : StrategyConfig(StrategyType.S3)