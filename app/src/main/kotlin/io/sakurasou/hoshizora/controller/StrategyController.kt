package io.sakurasou.hoshizora.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.sakurasou.hoshizora.constant.REGEX_URL
import io.sakurasou.hoshizora.constant.STRATEGY_DELETE
import io.sakurasou.hoshizora.constant.STRATEGY_READ_ALL
import io.sakurasou.hoshizora.constant.STRATEGY_READ_SINGLE
import io.sakurasou.hoshizora.constant.STRATEGY_WRITE
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.request.StrategyInsertRequest
import io.sakurasou.hoshizora.controller.request.StrategyPatchRequest
import io.sakurasou.hoshizora.controller.vo.CommonResponse
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.StrategyPageVO
import io.sakurasou.hoshizora.controller.vo.StrategyVO
import io.sakurasou.hoshizora.extension.id
import io.sakurasou.hoshizora.extension.pageRequest
import io.sakurasou.hoshizora.extension.pageRequestSpec
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.model.strategy.LocalStrategy
import io.sakurasou.hoshizora.model.strategy.S3Strategy
import io.sakurasou.hoshizora.model.strategy.StrategyConfig
import io.sakurasou.hoshizora.model.strategy.WebDavStrategy
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin
import io.swagger.v3.oas.models.media.Schema

/**
 * @author Shiina Kin
 * 2024/9/9 08:59
 */
fun Route.strategyRoute(strategyService: io.sakurasou.hoshizora.service.strategy.StrategyService) {
    val controller = StrategyController(strategyService)
    route("strategy", {
        protected = true
        tags("Strategy")
    }) {
        insertStrategy(controller)
        route("{id}", {
            protected = true
            request {
                pathParameter<Long>("id") {
                    description = "strategy id"
                    required = true
                }
            }
            response {
                HttpStatusCode.NotFound to {
                    description = "strategy not found"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            deleteStrategy(controller)
            patchStrategy(controller)
            fetchStrategy(controller)
        }
        pageStrategies(controller)
    }
}

private fun Route.insertStrategy(controller: StrategyController) {
    route {
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
        post({
            protected = true
            request {
                body(
                    Schema<StrategyInsertRequest>().apply {
                        title = "StrategyInsertRequest"
                        addProperty(
                            "config",
                            Schema<StrategyConfig>().apply {
                                description = "io/sakurasou/hoshizora/model/strategy/StrategyConfig.kt"
                                oneOf(
                                    listOf(
                                        Schema<LocalStrategy>().apply {
                                            title = "StrategyRequestLocalStrategyConfig"
                                            description = "Local strategy configuration"
                                            addProperty(
                                                "uploadFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "thumbnailFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "strategyType",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "type",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                    description = "same to strategyType"
                                                },
                                            )
                                        },
                                        Schema<S3Strategy>().apply {
                                            title = "StrategyRequestS3StrategyConfig"
                                            description = "S3 strategy configuration"
                                            addProperty(
                                                "endpoint",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "bucketName",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "region",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "accessKey",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "secretKey",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "uploadFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "thumbnailFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "publicUrl",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "strategyType",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "type",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                    description = "same to strategyType"
                                                },
                                            )
                                        },
                                        Schema<WebDavStrategy>().apply {
                                            title = "StrategyRequestWebDavStrategyConfig"
                                            description = "WebDav strategy configuration"
                                            addProperty(
                                                "serverUrl",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "username",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "password",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "uploadFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "thumbnailFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "strategyType",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "type",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                    description = "same to strategyType"
                                                },
                                            )
                                        },
                                    ),
                                )
                            },
                        )
                        addProperty(
                            "name",
                            Schema<Any>().apply {
                                type = "string"
                            },
                        )
                    },
                ) {
                    description = "`config` need a extra field `type`, same to strategyType"
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val insertRequest = call.receive<StrategyInsertRequest>()
            controller.handleInsertStrategy(insertRequest)
            call.success()
        }
    }
}

private fun Route.deleteStrategy(controller: StrategyController) {
    route {
        install(AuthorizationPlugin) {
            permission = STRATEGY_DELETE
        }
        delete({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val id = call.id()
            controller.handleDeleteStrategy(id)
            call.success()
        }
    }
}

private fun Route.patchStrategy(controller: StrategyController) {
    route {
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
        patch({
            request {
                body(
                    Schema<StrategyPatchRequest>().apply {
                        title = "StrategyPatchRequest"
                        addProperty(
                            "config",
                            Schema<StrategyConfig>().apply {
                                description = "io/sakurasou/hoshizora/model/strategy/StrategyConfig.kt"
                                anyOf(
                                    listOf(
                                        Schema<LocalStrategy>().apply {
                                            title = "StrategyRequestLocalStrategyConfig"
                                            description = "Local strategy configuration"
                                            addProperty(
                                                "uploadFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "thumbnailFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "strategyType",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "type",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                    description = "same to strategyType"
                                                },
                                            )
                                        },
                                        Schema<S3Strategy>().apply {
                                            title = "StrategyRequestS3StrategyConfig"
                                            description = "S3 strategy configuration"
                                            addProperty(
                                                "endpoint",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "bucketName",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "region",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "accessKey",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "secretKey",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "uploadFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "thumbnailFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "publicUrl",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "strategyType",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "type",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                    description = "same to strategyType"
                                                },
                                            )
                                        },
                                        Schema<WebDavStrategy>().apply {
                                            title = "StrategyRequestWebDavStrategyConfig"
                                            description = "WebDav strategy configuration"
                                            addProperty(
                                                "serverUrl",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "username",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "password",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "uploadFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "thumbnailFolder",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "strategyType",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                },
                                            )
                                            addProperty(
                                                "type",
                                                Schema<Any>().apply {
                                                    type = "string"
                                                    description = "same to strategyType"
                                                },
                                            )
                                        },
                                    ),
                                )
                                required = listOf("false")
                            },
                        )
                        addProperty(
                            "name",
                            Schema<Any>().apply {
                                type = "string"
                                required = listOf("false")
                            },
                        )
                    },
                ) {
                    description = "`config` need a extra field `type`, same to strategyType"
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val id = call.id()
            val patchRequest = call.receive<StrategyPatchRequest>()
            controller.handlePatchStrategy(id, patchRequest)
            call.success()
        }
    }
}

private fun Route.fetchStrategy(controller: StrategyController) {
    route {
        install(AuthorizationPlugin) {
            permission = STRATEGY_READ_SINGLE
        }
        get({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<StrategyVO>> { }
                }
            }
        }) {
            val id = call.id()
            val strategyVO = controller.handleFetchStrategy(id)
            call.success(strategyVO)
        }
    }
}

private fun Route.pageStrategies(controller: StrategyController) {
    route {
        install(AuthorizationPlugin) {
            permission = STRATEGY_READ_ALL
        }
        get("page", {
            pageRequestSpec()
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<PageResult<StrategyPageVO>>> {
                        description = "page result"
                    }
                }
                HttpStatusCode.BadRequest to {
                    description = "page or pageSize wrong"
                }
            }
        }) {
            val pageRequest = call.pageRequest()
            val pageResult = controller.handlePageStrategies(pageRequest)
            call.success(pageResult)
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
