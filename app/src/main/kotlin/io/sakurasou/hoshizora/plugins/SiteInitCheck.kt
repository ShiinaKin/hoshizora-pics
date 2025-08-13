package io.sakurasou.hoshizora.plugins

import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.hooks.CallSetup
import io.sakurasou.hoshizora.exception.controller.status.SiteNotInitializationException
import io.sakurasou.hoshizora.extension.isSiteNotInitialized

/**
 * @author Shiina Kin
 * 2024/10/27 17:58
 */

val SiteInitCheckPlugin =
    createRouteScopedPlugin("SiteInitCheckPlugin") {
        on(CallSetup) {
            if (isSiteNotInitialized()) throw SiteNotInitializationException()
        }
    }
