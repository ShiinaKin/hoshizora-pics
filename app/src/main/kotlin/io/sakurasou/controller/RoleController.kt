package io.sakurasou.controller

import io.ktor.http.*
import io.ktor.server.routing.*
import io.sakurasou.constant.ROLE_READ_ALL
import io.sakurasou.controller.vo.RoleVO
import io.sakurasou.extension.get

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
        }, ROLE_READ_ALL) {
            TODO()
        }
    }
}

class RoleController {
}