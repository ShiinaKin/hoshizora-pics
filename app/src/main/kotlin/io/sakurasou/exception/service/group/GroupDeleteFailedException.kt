package io.sakurasou.exception.service.group

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/25 12:44
 */
class GroupDeleteFailedException(
    cause: ServiceThrowable? = null
) : ServiceThrowable() {
    override val code: Int
        get() = 4000
    override var message: String = "Group Delete Failed"

    init {
        message = (message + (cause?.let { ", " + it.message } ?: ""))
    }
}