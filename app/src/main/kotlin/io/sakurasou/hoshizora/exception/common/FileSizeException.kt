package io.sakurasou.hoshizora.exception.common

import io.sakurasou.hoshizora.exception.ServiceThrowable

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
