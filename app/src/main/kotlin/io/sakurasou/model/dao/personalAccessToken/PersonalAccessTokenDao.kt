package io.sakurasou.model.dao.personalAccessToken

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.PersonalAccessTokenPageVO
import io.sakurasou.model.dao.common.PaginationDao
import io.sakurasou.model.dto.PersonalAccessTokenInsertDTO
import io.sakurasou.model.dto.PersonalAccessTokenUpdateDTO
import io.sakurasou.model.entity.PersonalAccessToken

/**
 * @author ShiinaKin
 * 2024/11/14 22:12
 */
interface PersonalAccessTokenDao : PaginationDao {
    fun savePAT(insertDTO: PersonalAccessTokenInsertDTO): Long

    fun deletePATById(patId: Long): Int

    fun updatePATById(updateDTO: PersonalAccessTokenUpdateDTO): Int

    fun findPATById(patId: Long): PersonalAccessToken?

    fun findPATByUserId(userId: Long): List<PersonalAccessToken>

    fun pagination(
        userId: Long,
        pageRequest: PageRequest,
    ): PageResult<PersonalAccessTokenPageVO>
}
