package io.sakurasou.hoshizora.extension

import io.ktor.http.HttpStatusCode
import io.ktor.openapi.Operation
import io.ktor.openapi.Responses
import io.ktor.openapi.jsonSchema
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.CommonResponse
import io.sakurasou.hoshizora.di.InstanceCenter
import io.sakurasou.hoshizora.exception.ServiceThrowable
import io.sakurasou.hoshizora.exception.controller.param.WrongParameterException

/**
 * @author Shiina Kin
 * 2024/9/12 10:39
 */
fun ApplicationCall.pageRequest(): PageRequest {
    val page = parameters["page"]?.toLong()
    val pageSize = parameters["pageSize"]?.toInt()
    val order = parameters["order"]
    val orderBy = parameters["orderBy"]

    if (page == null || pageSize == null) {
        throw WrongParameterException()
    }
    return PageRequest(page, pageSize, order, orderBy)
}

fun ApplicationCall.id(): Long = parameters["id"]?.toLong() ?: throw WrongParameterException()

private class TransparentRouteSelector : RouteSelector() {
    override suspend fun evaluate(
        context: RoutingResolveContext,
        segmentIndex: Int,
    ): RouteSelectorEvaluation = RouteSelectorEvaluation.Transparent

    override fun toString(): String = "(transparent)"
}

fun Route.transparentRoute(build: Route.() -> Unit): Route = createChild(TransparentRouteSelector()).apply(build)

fun Operation.Builder.pageRequestSpec() {
    parameters {
        query("page") {
            description = "page"
            required = true
            schema = jsonSchema<Long>()
        }
        query("pageSize") {
            description = "pageSize"
            required = true
            schema = jsonSchema<Int>()
        }
        query("order") {
            description = "order"
            required = false
            schema = jsonSchema<String>()
        }
        query("orderBy") {
            description = "orderBy"
            required = false
            schema = jsonSchema<String>()
        }
    }
}

inline fun <reified T> Responses.Builder.successResponse(description: String = "success") {
    HttpStatusCode.OK {
        this.description = description
        schema = jsonSchema<CommonResponse<T>>()
    }
}

fun Responses.Builder.commonResponse(
    statusCode: HttpStatusCode,
    description: String,
) {
    statusCode {
        this.description = description
        schema = jsonSchema<CommonResponse<Unit>>()
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
