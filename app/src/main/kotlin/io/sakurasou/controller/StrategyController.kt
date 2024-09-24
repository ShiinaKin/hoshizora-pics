package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
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
import io.sakurasou.extension.success
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.strategy.StrategyService

/**
 * @author Shiina Kin
 * 2024/9/9 08:59
 */
fun Route.strategyRoute(strategyService: StrategyService) {
    val controller = StrategyController(strategyService)
    route("strategy") {
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
                body<StrategyInsertRequest> {
                    description = "strategy request"
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
                body<StrategyPatchRequest> {
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
            pageRequest()
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<PageResult<StrategyPageVO>> {
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