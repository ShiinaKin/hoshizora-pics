package io.sakurasou.exception.service.setting

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/13 15:13
 */
class ConfigTypeNotMatchException : ServiceThrowable() {
    override val code: Int
        get() = 5003
    override val message: String
        get() = "Config Type Not Match"
}