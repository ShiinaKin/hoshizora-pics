package io.sakurasou.model.group

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/10/12 11:10
 */
@Serializable
data class GroupConfig(
    val groupStrategyConfig: GroupStrategyConfig
)

@Serializable
enum class ImageType {
    JPEG, JPG, PNG, GIF, TIF, BMP, ICO, PSD, WEBP
}
