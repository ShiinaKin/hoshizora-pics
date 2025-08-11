package io.sakurasou.plugins

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.sakurasou.exception.controller.status.SiteNotInitializationException
import io.sakurasou.extension.isSiteNotInitialized

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
