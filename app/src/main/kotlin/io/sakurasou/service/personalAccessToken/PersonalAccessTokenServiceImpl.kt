package io.sakurasou.service.personalAccessToken

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.request.PersonalAccessTokenInsertRequest
import io.sakurasou.controller.request.PersonalAccessTokenPatchRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.PersonalAccessTokenPageVO
import io.sakurasou.exception.service.group.GroupNotFoundException
import io.sakurasou.exception.service.personalAccessToken.PersonalAccessTokenAccessDeniedException
import io.sakurasou.exception.service.personalAccessToken.PersonalAccessTokenInsertFailedException
import io.sakurasou.exception.service.personalAccessToken.PersonalAccessTokenNotFoundException
import io.sakurasou.exception.service.user.UserNotFoundException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.group.GroupDao
import io.sakurasou.model.dao.personalAccessToken.PersonalAccessTokenDao
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.PersonalAccessTokenInsertDTO
import io.sakurasou.model.dto.PersonalAccessTokenUpdateDTO
import io.sakurasou.util.JwtUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author ShiinaKin
 * 2024/11/16 02:08
 */
class PersonalAccessTokenServiceImpl(
    private val personalAccessTokenDao: PersonalAccessTokenDao,
    private val userDao: UserDao,
    private val groupDao: GroupDao,
    private val relationDao: RelationDao
) : PersonalAccessTokenService {
    override suspend fun savePAT(userId: Long, insertRequest: PersonalAccessTokenInsertRequest): String {
        return dbQuery {
            val user = userDao.findUserById(userId) ?: throw UserNotFoundException()

            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            if (insertRequest.expireTime < now)
                throw PersonalAccessTokenInsertFailedException(reason = "Expire time must be later than now")

            val group = groupDao.findGroupById(user.groupId) ?: throw GroupNotFoundException()
            val roleOfGroup = relationDao.listRoleByGroupId(group.id)
            val permissionsOfUser = roleOfGroup.map { relationDao.listPermissionByRole(it) }.flatten().toSet()

            if (insertRequest.permissions.any { !permissionsOfUser.contains(it) })
                throw PersonalAccessTokenInsertFailedException(reason = "Invalid permissions")

            val insertDTO = PersonalAccessTokenInsertDTO(
                userId = userId,
                name = insertRequest.name,
                description = insertRequest.description,
                createTime = now,
                expireTime = insertRequest.expireTime,
            )
            val patId = personalAccessTokenDao.savePAT(insertDTO)
            relationDao.batchInsertPATToPermissions(patId, insertRequest.permissions)

            val token = JwtUtils.generateJwtToken(
                user = user,
                patId = patId,
                expireTime = insertRequest.expireTime,
                permissions = insertRequest.permissions,
            )

            token
        }
    }

    override suspend fun deletePAT(userId: Long, patId: Long) {
        dbQuery {
            val personalAccessToken =
                personalAccessTokenDao.findPATById(patId) ?: throw PersonalAccessTokenNotFoundException()
            if (personalAccessToken.userId != userId) throw PersonalAccessTokenAccessDeniedException()
            relationDao.deletePATToPermissionsByPATId(personalAccessToken.id)
            personalAccessTokenDao.deletePATById(personalAccessToken.id)
        }
    }

    override suspend fun patchPAT(userId: Long, patId: Long, updateRequest: PersonalAccessTokenPatchRequest) {
        dbQuery {
            val personalAccessToken =
                personalAccessTokenDao.findPATById(patId) ?: throw PersonalAccessTokenNotFoundException()
            if (personalAccessToken.userId != userId) throw PersonalAccessTokenAccessDeniedException()
            val updateDTO = PersonalAccessTokenUpdateDTO(
                id = userId,
                name = updateRequest.name ?: personalAccessToken.name,
                description = updateRequest.description ?: personalAccessToken.description,
            )
            personalAccessTokenDao.updatePATById(updateDTO)
        }
    }

    override suspend fun pagePAT(userId: Long, pageRequest: PageRequest): PageResult<PersonalAccessTokenPageVO> {
        return dbQuery { personalAccessTokenDao.pagination(userId, pageRequest) }
    }
}