package io.sakurasou.exception.controller.param

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/9 10:38
 */
class FileSizeException : ServiceThrowable() {
    override val code: Int
        get() = 4001
    override val message: String
        get() = "File size exceeds the limit"
}