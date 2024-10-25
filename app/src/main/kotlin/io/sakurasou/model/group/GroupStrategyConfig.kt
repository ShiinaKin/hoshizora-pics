package io.sakurasou.model.group

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/10/12 13:53
 */
@Serializable
@Name("GroupStrategyConfig")
data class GroupStrategyConfig(
    val singleFileMaxSize: Long = 12 * 1024 * 1024L,
    val maxSize: Long = 16 * 1024 * 1024 * 1024L,
    val pathNamingRule: String = "{yyyy}/{MM}/{dd}",
    val fileNamingRule: String = "{uniq}",
    val imageQuality: Int = 100,
    val imageAutoTransformTarget: ImageType? = null,
    val allowedImageTypes: Set<ImageType> = setOf(ImageType.JPEG, ImageType.JPG, ImageType.PNG, ImageType.GIF, ImageType.WEBP)
)