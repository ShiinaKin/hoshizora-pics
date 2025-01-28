package io.sakurasou.model.strategy

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 09:27
 */
@Serializable
@SerialName("WebDav")
@Name("WebDavStrategy")
data class WebDavStrategy(
    val serverUrl: String,
    val username: String,
    val password: String,
    val uploadFolder: String,
    val thumbnailFolder: String,
) : StrategyConfig(StrategyType.WEBDAV)