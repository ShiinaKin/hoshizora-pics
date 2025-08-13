package io.sakurasou.hoshizora.exception.controller.access

import io.sakurasou.hoshizora.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/14 08:30
 */
class RandomFetchAllowedException : ServiceThrowable() {
    override val code: Int
        get() = 4003
    override val message: String
        get() = "Random fetch image is not allowed"
}
