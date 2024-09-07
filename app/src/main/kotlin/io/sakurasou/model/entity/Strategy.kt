package io.sakurasou.model.entity

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * @author ShiinaKin
 * 2024/9/7 13:32
 */
@Serializable
data class Strategy(
    val id: Long,
    val name: String,
    val type: String = "local", // 默认值为 "local"
    val config: JsonElement,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
)

@Serializable
data class LocalConfig(
    val uploadFolder: String = "/uploads"
)

@Serializable
data class S3Config(
    val endpoint: String,
    val bucket: String,
    val region: String,
    val accessKey: String,
    val secretKey: String
)