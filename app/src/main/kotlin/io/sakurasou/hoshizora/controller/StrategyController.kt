@file:OptIn(ExperimentalKtorApi::class)

package io.sakurasou.hoshizora.controller

import io.ktor.http.HttpStatusCode
import io.ktor.openapi.jsonSchema
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi
import io.sakurasou.hoshizora.constant.REGEX_URL
import io.sakurasou.hoshizora.constant.STRATEGY_DELETE
import io.sakurasou.hoshizora.constant.STRATEGY_READ_ALL
import io.sakurasou.hoshizora.constant.STRATEGY_READ_SINGLE
import io.sakurasou.hoshizora.constant.STRATEGY_WRITE
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.request.StrategyInsertRequest
import io.sakurasou.hoshizora.controller.request.StrategyPatchRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.StrategyPageVO
import io.sakurasou.hoshizora.controller.vo.StrategyVO
import io.sakurasou.hoshizora.extension.commonResponse
import io.sakurasou.hoshizora.extension.id
import io.sakurasou.hoshizora.extension.pageRequest
import io.sakurasou.hoshizora.extension.pageRequestSpec
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.extension.successResponse
import io.sakurasou.hoshizora.extension.transparentRoute
import io.sakurasou.hoshizora.model.strategy.S3Strategy
import io.sakurasou.hoshizora.model.strategy.WebDavStrategy
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin

/**
 * @author Shiina Kin
 * 2024/9/9 08:59
 */
fun Route.strategyRoute(strategyService: io.sakurasou.hoshizora.service.strategy.StrategyService) {
    val controller = StrategyController(strategyService)
    route("strategy") {
        insertStrategy(controller)
        route("{id}") {
            deleteStrategy(controller)
            patchStrategy(controller)
            fetchStrategy(controller)
        }.describe {
            parameters {
                path("id") {
                    description = "strategy id"
                    required = true
                    schema = jsonSchema<Long>()
                }
            }
            responses {
                commonResponse(HttpStatusCode.NotFound, "strategy not found")
            }
        }
        pageStrategies(controller)
    }.describe {
        tag("Strategy")
    }
}

private fun Route.insertStrategy(controller: StrategyController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = STRATEGY_WRITE
        }
        install(RequestValidation) {
            validate<StrategyInsertRequest> { insertRequest ->
                if (insertRequest.name.isBlank()) {
                    ValidationResult.Invalid("name is invalid")
                } else if (insertRequest.config is S3Strategy && !insertRequest.config.endpoint.matches(Regex(REGEX_URL))) {
                    ValidationResult.Invalid("endpoint is invalid")
                } else if (insertRequest.config is S3Strategy && insertRequest.config.bucketName.isBlank()) {
                    ValidationResult.Invalid("bucketName is invalid")
                } else if (insertRequest.config is S3Strategy && insertRequest.config.region.isBlank()) {
                    ValidationResult.Invalid("region is invalid")
                } else if (insertRequest.config is S3Strategy && !insertRequest.config.publicUrl.matches(Regex(REGEX_URL))) {
                    ValidationResult.Invalid("publicUrl is invalid")
                } else if (insertRequest.config is WebDavStrategy &&
                    !insertRequest.config.serverUrl.matches(
                        Regex(
                            REGEX_URL,
                        ),
                    )
                ) {
                    ValidationResult.Invalid("serverUrl is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        post {
            val insertRequest = call.receive<StrategyInsertRequest>()
            controller.handleInsertStrategy(insertRequest)
            call.success()
        }.describe {
            requestBody {
                description = "`config` need a extra field `type`, same to strategyType"
                required = true
                schema = jsonSchema<StrategyInsertRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.deleteStrategy(controller: StrategyController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = STRATEGY_DELETE
        }
        delete {
            val id = call.id()
            controller.handleDeleteStrategy(id)
            call.success()
        }.describe {
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.patchStrategy(controller: StrategyController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = STRATEGY_WRITE
        }
        install(RequestValidation) {
            validate<StrategyPatchRequest> { patchRequest ->
                if (patchRequest.name == null && patchRequest.config == null) {
                    ValidationResult.Invalid("at least one field is required")
                } else if (patchRequest.name != null && patchRequest.name.isBlank()) {
                    ValidationResult.Invalid("name is invalid")
                } else if (patchRequest.config is S3Strategy && !patchRequest.config.endpoint.matches(Regex(REGEX_URL))) {
                    ValidationResult.Invalid("endpoint is invalid")
                } else if (patchRequest.config is S3Strategy && patchRequest.config.bucketName.isBlank()) {
                    ValidationResult.Invalid("bucketName is invalid")
                } else if (patchRequest.config is S3Strategy && patchRequest.config.region.isBlank()) {
                    ValidationResult.Invalid("region is invalid")
                } else if (patchRequest.config is S3Strategy && !patchRequest.config.publicUrl.matches(Regex(REGEX_URL))) {
                    ValidationResult.Invalid("publicUrl is invalid")
                } else if (patchRequest.config is WebDavStrategy &&
                    !patchRequest.config.serverUrl.matches(
                        Regex(
                            REGEX_URL,
                        ),
                    )
                ) {
                    ValidationResult.Invalid("serverUrl is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch {
            val id = call.id()
            val patchRequest = call.receive<StrategyPatchRequest>()
            controller.handlePatchStrategy(id, patchRequest)
            call.success()
        }.describe {
            requestBody {
                description = "`config` need a extra field `type`, same to strategyType"
                required = true
                schema = jsonSchema<StrategyPatchRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.fetchStrategy(controller: StrategyController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = STRATEGY_READ_SINGLE
        }
        get {
            val id = call.id()
            val strategyVO = controller.handleFetchStrategy(id)
            call.success(strategyVO)
        }.describe {
            responses {
                successResponse<StrategyVO>()
            }
        }
    }
}

private fun Route.pageStrategies(controller: StrategyController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = STRATEGY_READ_ALL
        }
        get("page") {
            val pageRequest = call.pageRequest()
            val pageResult = controller.handlePageStrategies(pageRequest)
            call.success(pageResult)
        }.describe {
            pageRequestSpec()
            responses {
                successResponse<PageResult<StrategyPageVO>>("page result")
                HttpStatusCode.BadRequest {
                    description = "page or pageSize wrong"
                }
            }
        }
    }
}

class StrategyController(
    private val strategyService: io.sakurasou.hoshizora.service.strategy.StrategyService,
) {
    suspend fun handleInsertStrategy(request: StrategyInsertRequest) {
        strategyService.saveStrategy(request)
    }

    suspend fun handleDeleteStrategy(id: Long) {
        strategyService.deleteStrategy(id)
    }

    suspend fun handlePatchStrategy(
        id: Long,
        request: StrategyPatchRequest,
    ) {
        strategyService.updateStrategy(id, request)
    }

    suspend fun handleFetchStrategy(id: Long): StrategyVO {
        val strategy = strategyService.fetchStrategy(id)
        return strategy
    }

    suspend fun handlePageStrategies(pageRequest: PageRequest): PageResult<StrategyPageVO> {
        val pageResult = strategyService.pageStrategies(pageRequest)
        return pageResult
    }
}
