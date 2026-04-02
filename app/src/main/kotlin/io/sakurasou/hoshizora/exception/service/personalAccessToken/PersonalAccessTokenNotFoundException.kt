package io.sakurasou.hoshizora.exception.service.personalAccessToken

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author ShiinaKin
 * 2024/11/16 04:41
 */
class PersonalAccessTokenNotFoundException : ServiceException() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "PersonalAccessToken Not Found"
}
