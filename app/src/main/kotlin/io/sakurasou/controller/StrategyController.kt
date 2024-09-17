package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.sakurasou.constant.STRATEGY_DELETE
import io.sakurasou.constant.STRATEGY_READ_ALL
import io.sakurasou.constant.STRATEGY_READ_SINGLE
import io.sakurasou.constant.STRATEGY_WRITE
import io.sakurasou.controller.request.StrategyInsertRequest
import io.sakurasou.controller.request.StrategyPatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.StrategyPageVO
import io.sakurasou.controller.vo.StrategyVO
import io.sakurasou.extension.pageRequest
import io.sakurasou.plugins.AuthorizationPlugin

/**
 * @author Shiina Kin
 * 2024/9/9 08:59
 */
fun Route.strategyRoute() {
    route("strategy") {
        insertStrategy()
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
            deleteStrategy()
            updateStrategy()
            fetchStrategy()
        }
        pageStrategies()
    }
}

private fun Route.insertStrategy() {
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
            TODO()
        }
    }
}

private fun Route.deleteStrategy() {
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
            TODO()
        }
    }
}

private fun Route.updateStrategy() {
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
            TODO()
        }
    }
}

private fun Route.fetchStrategy() {
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
            TODO()
        }
    }
}

private fun Route.pageStrategies() {
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
            val pageVO = call.pageRequest()

            TODO()
        }
    }
}

class StrategyController {
}