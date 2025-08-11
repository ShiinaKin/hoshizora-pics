package io.sakurasou.service.personalAccessToken

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.request.PersonalAccessTokenInsertRequest
import io.sakurasou.controller.request.PersonalAccessTokenPatchRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.PersonalAccessTokenPageVO

/**
 * @author ShiinaKin
 * 2024/11/16 02:08
 */
interface PersonalAccessTokenService {
    suspend fun savePAT(
        userId: Long,
        insertRequest: PersonalAccessTokenInsertRequest,
    ): String

    suspend fun deletePAT(
        userId: Long,
        patId: Long,
    )

    suspend fun patchPAT(
        userId: Long,
        patId: Long,
        updateRequest: PersonalAccessTokenPatchRequest,
    )

    suspend fun pagePAT(
        userId: Long,
        pageRequest: PageRequest,
    ): PageResult<PersonalAccessTokenPageVO>
}
