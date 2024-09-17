package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.sakurasou.constant.ROLE_READ_ALL
import io.sakurasou.controller.vo.RoleVO
import io.sakurasou.plugins.AuthorizationPlugin

/**
 * @author Shiina Kin
 * 2024/9/9 08:53
 */

fun Route.roleRoute() {
    route("role") {
        install(AuthorizationPlugin) {
            permission = ROLE_READ_ALL
        }
        get("all", {
            protected = true
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<List<RoleVO>> {
                        description = "all roles with permissions"
                    }
                }
            }
        }) {
            TODO()
        }
    }
}

class RoleController {
}