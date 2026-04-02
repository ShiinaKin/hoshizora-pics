package io.sakurasou.hoshizora.exception.controller.param

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/10/11 17:44
 */
class UnsupportedFileTypeException : ServiceException() {
    override val code: Int
        get() = 4000
    override val message: String
        get() = "File type is not supported"
}
