package io.sakurasou.extension

import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.sakurasou.config.InstanceCenter
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.exception.ServiceThrowable
import io.sakurasou.exception.WrongParameterException

/**
 * @author Shiina Kin
 * 2024/9/12 10:39
 */
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

suspend fun ApplicationCall.success() {
    respond(CommonResponse.success(Unit))
}

suspend inline fun <reified T> ApplicationCall.success(data: T) {
    respond(CommonResponse.success(data))
}

suspend fun ApplicationCall.failure(exception: ServiceThrowable) {
    respond(CommonResponse.error<Unit>(exception.code, exception.message))
}

fun isSiteNotInitialized() = !InstanceCenter.systemStatus.isInit
