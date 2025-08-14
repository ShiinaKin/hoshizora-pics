package io.sakurasou.hoshizora.model.strategy

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:32
 */
@Serializable
@SerialName("StrategyConfig")
@Name("StrategyConfigSealed")
sealed class StrategyConfig(
    val strategyType: StrategyType,
) {
    abstract val uploadFolder: String
    abstract val thumbnailFolder: String

    abstract suspend fun upload(
        imageBytes: ByteArray,
        uploadPath: String,
    )

    abstract suspend fun delete(relativePath: String)

    abstract suspend fun fetch(relativePath: String): ByteArray
}

@Serializable
@Name("StrategyTypeEnum")
enum class StrategyType {
    LOCAL,
    S3,
    WEBDAV,
}
