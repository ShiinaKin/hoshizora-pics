package io.sakurasou.controller

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
import io.sakurasou.extension.*

/**
 * @author Shiina Kin
 * 2024/9/9 08:59
 */
fun Route.strategyRoute() {
    route("strategy") {
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
        }, STRATEGY_WRITE) {
            TODO()
        }
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
            delete({
                response {
                    HttpStatusCode.OK to {
                        description = "success"
                        body<CommonResponse<Unit>> { }
                    }
                }
            }, STRATEGY_DELETE) {
                TODO()
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
            }, STRATEGY_WRITE) {
                TODO()
            }
            get({
                response {
                    HttpStatusCode.OK to {
                        description = "success"
                        body<CommonResponse<StrategyVO>> { }
                    }
                }
            }, STRATEGY_READ_SINGLE) {
                TODO()
            }
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
        }, STRATEGY_READ_ALL) {
            val pageVO = call.pageRequest()

            TODO()
        }
    }
}

class StrategyController {
}