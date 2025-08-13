package io.sakurasou.hoshizora.exception.dao

import io.sakurasou.hoshizora.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/13 15:15
 */
class MissingNecessaryColumnException : ServiceThrowable() {
    override val code: Int
        get() = 5002
    override val message: String
        get() = "Missing Necessary Column"
}
