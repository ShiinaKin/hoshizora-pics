package io.sakurasou.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 10:31
 */
@Serializable
data class PageRequest(
    val page: Int,
    val pageSize: Int,
    val order: String? = null,
    val orderBy: String? = null
)

@Serializable
data class SiteInitRequest(
    val username: String,
    val password: String,
    val email: String,
    val siteTitle: String,
    val siteSubtitle: String,
    val siteDescription: String
)