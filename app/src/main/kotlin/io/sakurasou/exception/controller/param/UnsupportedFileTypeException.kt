package io.sakurasou.exception.controller.param

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/10/11 17:44
 */
class UnsupportedFileTypeException : ServiceThrowable() {
    override val code: Int
        get() = 4000
    override val message: String
        get() = "File type is not supported"
}