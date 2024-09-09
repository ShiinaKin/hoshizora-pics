package io.sakurasou.controller.request

import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiRoute
import io.ktor.server.application.*
import io.sakurasou.exception.WrongParameterException
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

fun ApplicationCall.pageRequest(): PageRequest {
    val page = parameters["page"]?.toInt()
    val pageSize = parameters["pageSize"]?.toInt()
    val order = parameters["order"]
    val orderBy = parameters["orderBy"]

    if (page == null || pageSize == null) {
        throw WrongParameterException()
    }
    return PageRequest(page, pageSize, order, orderBy)
}

fun OpenApiRoute.pageRequest() {
    request {
        queryParameter<Int>("page") {
            description = "page"
            required = true
        }
        queryParameter<Int>("pageSize") {
            description = "pageSize"
            required = true
        }
        queryParameter<String>("order") {
            description = "order"
            required = false
        }
        queryParameter<String>("orderBy") {
            description = "orderBy"
            required = false
        }
    }
}

@Serializable
data class SiteInitRequest(
    val username: String,
    val password: String,
    val email: String,
    val siteTitle: String,
    val siteSubtitle: String,
)