package io.sakurasou.exception.controller.status

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/13 16:13
 */
class SiteNotInitializationException: ServiceThrowable() {
    override val code: Int
        get() = 5000
    override val message: String
        get() = "Site has not been initialized"
}