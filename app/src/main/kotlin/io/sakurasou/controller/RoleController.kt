package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.ktor.http.*
import io.ktor.server.routing.*
import io.sakurasou.controller.vo.RoleVO

/**
 * @author Shiina Kin
 * 2024/9/9 08:53
 */

fun Route.roleRoute() {
    route("role") {
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