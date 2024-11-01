package io.sakurasou.controller.request

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 10:31
 */
@Serializable
@Name("PageRequest")
data class PageRequest(
    val page: Long,
    val pageSize: Int,
    val order: String? = null,
    val orderBy: String? = null,
    var additionalCondition: Map<String, String>? = null
)

@Serializable
@Name("SiteInitRequest")
data class SiteInitRequest(
    val username: String,
    val password: String,
    val email: String,
    val siteExternalUrl: String,
    val siteTitle: String,
    val siteSubtitle: String,
    val siteDescription: String
)