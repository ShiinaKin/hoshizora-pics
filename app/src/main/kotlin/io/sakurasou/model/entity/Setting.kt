package io.sakurasou.model.entity

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * @author ShiinaKin
 * 2024/9/7 13:34
 */
@Serializable
data class Setting(
    val id: Long,
    val name: String,
    val config: JsonObject,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
)

@Serializable
data class StrategySetting(
    val allowedImageTypes: List<String>
)

@Serializable
data class SiteSetting(
    val siteTitle: String,
    val siteSubtitle: String,
    val siteKeyword: String,
    val siteDescription: String,
    val homePageRandomPicDisplay: Boolean
)