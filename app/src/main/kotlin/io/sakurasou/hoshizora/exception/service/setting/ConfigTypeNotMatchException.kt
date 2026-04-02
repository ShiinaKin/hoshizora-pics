package io.sakurasou.hoshizora.exception.service.setting

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/13 15:13
 */
class ConfigTypeNotMatchException : ServiceException() {
    override val code: Int
        get() = 5003
    override val message: String
        get() = "Config Type Not Match"
}
