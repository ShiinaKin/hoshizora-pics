package io.sakurasou.hoshizora.model.group

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/10/12 11:10
 */
@Serializable
@Name("GroupConfig")
data class GroupConfig(
    val groupStrategyConfig: GroupStrategyConfig,
)

@Serializable
@Name("ImageTypeEnum")
enum class ImageType {
    JPEG,
    JPG,
    PNG,
    GIF,
    TIF,
    BMP,
    ICO,
    PSD,
    WEBP,
}
