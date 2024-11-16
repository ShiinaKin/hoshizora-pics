package io.sakurasou.exception.service.personalAccessToken

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/10/9 13:58
 */
class PersonalAccessTokenAccessDeniedException : ServiceThrowable() {
    override val code: Int
        get() = 4003
    override val message: String
        get() = "PersonalAccessToken Access Denied."
}