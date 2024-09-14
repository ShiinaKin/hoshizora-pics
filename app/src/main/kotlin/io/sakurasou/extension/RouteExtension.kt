package io.sakurasou.extension

import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.routing.documentation
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import io.sakurasou.config.InstanceCenter
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.exception.ServiceThrowable
import io.sakurasou.exception.SiteNotInitializationException
import io.sakurasou.exception.UnauthorizedAccessException
import io.sakurasou.exception.WrongParameterException
import org.jetbrains.exposed.sql.exposedLogger

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

suspend fun <T> ApplicationCall.success(data: T) {
    respond(CommonResponse.success(data))
}

suspend fun ApplicationCall.failure(exception: ServiceThrowable) {
    respond(CommonResponse.error<Unit>(exception.code, exception.message))
}

fun isSiteNotInitialized() = !InstanceCenter.systemStatus.isInit

/**
 * Behavior:
 * 1. Check if the user has the required permission, if not throw an UnauthorizedAccessException
 *
 * @throws UnauthorizedAccessException
 */
fun Route.post(
    builder: OpenApiRoute.() -> Unit = { },
    permission: String,
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    intercept(ApplicationCallPipeline.Call) {
        if (lackPermission(call.attributes, permission)) {
            throw UnauthorizedAccessException()
        }
        exposedLogger.debug("====== access permission check ======")
    }
    return documentation(builder) { post(body) }
}

/**
 * Behavior:
 * 1. Check if the user has the required permission, if not throw an UnauthorizedAccessException
 *
 * @throws UnauthorizedAccessException
 */
fun Route.delete(
    builder: OpenApiRoute.() -> Unit = { },
    permission: String,
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    intercept(ApplicationCallPipeline.Call) {
        if (lackPermission(call.attributes, permission)) {
            throw UnauthorizedAccessException()
        }
        exposedLogger.debug("====== access permission check ======")
    }
    return documentation(builder) { delete(body) }
}

/**
 * Behavior:
 * 1. Check if the user has the required permission, if not throw an UnauthorizedAccessException
 *
 * @throws UnauthorizedAccessException
 */
fun Route.patch(
    builder: OpenApiRoute.() -> Unit = { },
    permission: String,
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    intercept(ApplicationCallPipeline.Call) {
        if (lackPermission(call.attributes, permission)) {
            throw UnauthorizedAccessException()
        }
        exposedLogger.debug("====== access permission check ======")
    }
    return documentation(builder) { patch(body) }
}

/**
 * Behavior:
 * 1. Check if the user has the required permission, if not throw an UnauthorizedAccessException
 *
 * @throws UnauthorizedAccessException
 */
fun Route.get(
    builder: OpenApiRoute.() -> Unit = { },
    permission: String,
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    intercept(ApplicationCallPipeline.Call) {
        if (lackPermission(call.attributes, permission)) {
            throw UnauthorizedAccessException()
        }
        exposedLogger.debug("====== access permission check ======")
    }
    return documentation(builder) { get(body) }
}

/**
 * Behavior:
 * 1. Check if the user has the required permission, if not throw an UnauthorizedAccessException
 *
 * @throws UnauthorizedAccessException
 */
fun Route.post(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    permission: String,
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    intercept(ApplicationCallPipeline.Call) {
        if (lackPermission(call.attributes, permission)) {
            throw UnauthorizedAccessException()
        }
        exposedLogger.debug("====== access permission check ======")
    }
    return documentation(builder) { post(path, body) }
}

/**
 * Behavior:
 * 1. Check if the user has the required permission, if not throw an UnauthorizedAccessException
 *
 * @throws UnauthorizedAccessException
 */
fun Route.delete(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    permission: String,
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    intercept(ApplicationCallPipeline.Call) {
        if (lackPermission(call.attributes, permission)) {
            throw UnauthorizedAccessException()
        }
        exposedLogger.debug("====== access permission check ======")
    }
    return documentation(builder) { delete(path, body) }
}

/**
 * Behavior:
 * 1. Check if the user has the required permission, if not throw an UnauthorizedAccessException
 *
 * @throws UnauthorizedAccessException
 */
fun Route.patch(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    permission: String,
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    intercept(ApplicationCallPipeline.Call) {
        if (lackPermission(call.attributes, permission)) {
            throw UnauthorizedAccessException()
        }
        exposedLogger.debug("====== access permission check ======")
    }
    return documentation(builder) { patch(path, body) }
}

/**
 * Behavior:
 * 1. Check if the user has the required permission, if not throw an UnauthorizedAccessException
 *
 * @throws UnauthorizedAccessException
 */
fun Route.get(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    permission: String,
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    intercept(ApplicationCallPipeline.Call) {
        if (lackPermission(call.attributes, permission)) {
            throw UnauthorizedAccessException()
        }
        exposedLogger.debug("====== access permission check ======")
    }
    return documentation(builder) { get(path, body) }
}