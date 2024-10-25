package io.sakurasou.model.strategy

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 09:27
 */
@Serializable
@SerialName("S3")
@Name("S3Strategy")
data class S3Strategy(
    val endpoint: String,
    val bucketName: String,
    val region: String,
    val accessKey: String,
    val secretKey: String,
    val uploadFolder: String,
    val thumbnailFolder: String,
    val publicUrl: String
) : StrategyConfig(StrategyType.S3)