package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.sakurasou.constant.STRATEGY_DELETE
import io.sakurasou.constant.STRATEGY_READ_ALL
import io.sakurasou.constant.STRATEGY_READ_SINGLE
import io.sakurasou.constant.STRATEGY_WRITE
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.request.StrategyInsertRequest
import io.sakurasou.controller.request.StrategyPatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.StrategyPageVO
import io.sakurasou.controller.vo.StrategyVO
import io.sakurasou.extension.id
import io.sakurasou.extension.pageRequest
import io.sakurasou.extension.pageRequestSpec
import io.sakurasou.extension.success
import io.sakurasou.model.strategy.LocalStrategy
import io.sakurasou.model.strategy.S3Strategy
import io.sakurasou.model.strategy.StrategyConfig
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.strategy.StrategyService
import io.swagger.v3.oas.models.media.Schema

/**
 * @author Shiina Kin
 * 2024/9/9 08:59
 */
fun Route.strategyRoute(strategyService: StrategyService) {
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
        post({
            protected = true
            request {
                body(Schema<StrategyInsertRequest>().apply {
                    title = "StrategyInsertRequest"
                    addProperty("config", Schema<StrategyConfig>().apply {
                        description = "io/sakurasou/model/strategy/StrategyConfig.kt"
                        oneOf(
                            listOf(
                                Schema<LocalStrategy>().apply {
                                    title = "StrategyRequestLocalStrategyConfig"
                                    description = "Local strategy configuration"
                                    addProperty("uploadFolder", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("thumbnailFolder", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("strategyType", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("type", Schema<Any>().apply {
                                        type = "string"
                                        description = "same to strategyType"
                                    })
                                },
                                Schema<S3Strategy>().apply {
                                    title = "StrategyRequestS3StrategyConfig"
                                    description = "S3 strategy configuration"
                                    addProperty("endpoint", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("bucketName", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("region", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("accessKey", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("secretKey", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("uploadFolder", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("thumbnailFolder", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("publicUrl", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("strategyType", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("type", Schema<Any>().apply {
                                        type = "string"
                                        description = "same to strategyType"
                                    })
                                }
                            )
                        )
                    })
                    addProperty("name", Schema<Any>().apply {
                        type = "string"
                    })
                }) {
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
        patch({
            request {
                body(Schema<StrategyPatchRequest>().apply {
                    title = "StrategyPatchRequest"
                    addProperty("config", Schema<StrategyConfig>().apply {
                        description = "io/sakurasou/model/strategy/StrategyConfig.kt"
                        anyOf(
                            listOf(
                                Schema<LocalStrategy>().apply {
                                    title = "StrategyRequestLocalStrategyConfig"
                                    description = "Local strategy configuration"
                                    addProperty("uploadFolder", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("thumbnailFolder", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("strategyType", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("type", Schema<Any>().apply {
                                        type = "string"
                                        description = "same to strategyType"
                                    })
                                },
                                Schema<S3Strategy>().apply {
                                    title = "StrategyRequestS3StrategyConfig"
                                    description = "S3 strategy configuration"
                                    addProperty("endpoint", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("bucketName", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("region", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("accessKey", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("secretKey", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("uploadFolder", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("thumbnailFolder", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("publicUrl", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("strategyType", Schema<Any>().apply {
                                        type = "string"
                                    })
                                    addProperty("type", Schema<Any>().apply {
                                        type = "string"
                                        description = "same to strategyType"
                                    })
                                }
                            )
                        )
                        required = listOf("false")
                    })
                    addProperty("name", Schema<Any>().apply {
                        type = "string"
                        required = listOf("false")
                    })
                }) {
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
    private val strategyService: StrategyService
) {
    suspend fun handleInsertStrategy(request: StrategyInsertRequest) {
        strategyService.saveStrategy(request)
    }

    suspend fun handleDeleteStrategy(id: Long) {
        strategyService.deleteStrategy(id)
    }

    suspend fun handlePatchStrategy(id: Long, request: StrategyPatchRequest) {
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