package io.sakurasou.model.dao.album

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.AlbumManagePageVO
import io.sakurasou.controller.vo.AlbumPageVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.exception.dao.MissingUserDefaultAlbumException
import io.sakurasou.model.dao.image.Images
import io.sakurasou.model.dao.user.Users
import io.sakurasou.model.dto.AlbumInsertDTO
import io.sakurasou.model.dto.AlbumUpdateDTO
import io.sakurasou.model.entity.Album
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * @author ShiinaKin
 * 2024/9/7 14:09
 */
class AlbumDaoImpl : AlbumDao {
    override fun initAlbumForUser(userId: Long): Long {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val uncategorizedAlbum = AlbumInsertDTO(
            userId = userId,
            name = "uncategorized",
            description = "default, cannot delete",
            imageCount = 0,
            isUncategorized = true,
            createTime = now
        )
        return saveAlbum(uncategorizedAlbum)
    }

    override fun saveAlbum(insertDTO: AlbumInsertDTO): Long {
        val entityID = Albums.insertAndGetId {
            it[userId] = insertDTO.userId
            it[name] = insertDTO.name
            it[description] = insertDTO.description
            it[isUncategorized] = insertDTO.isUncategorized
            it[createTime] = insertDTO.createTime
        }
        return entityID.value
    }

    override fun deleteAlbumById(albumId: Long): Int {
        return Albums.deleteWhere { Albums.id eq albumId }
    }

    override fun updateAlbumById(updateDTO: AlbumUpdateDTO): Int {
        return Albums.update({ Albums.id eq updateDTO.id }) {
            it[userId] = updateDTO.userId
            it[name] = updateDTO.name
            it[description] = updateDTO.description
        }
    }

    override fun findAlbumById(albumId: Long): Album? {
        return Albums.selectAll()
            .where { Albums.id eq albumId }
            .map { toAlbum(it) }
            .firstOrNull()
    }

    override fun findDefaultAlbumByUserId(userId: Long): Album {
        return Albums.selectAll()
            .where { (Albums.userId eq userId) and (Albums.isUncategorized eq true) }
            .map { toAlbum(it) }
            .firstOrNull() ?: throw MissingUserDefaultAlbumException(userId)
    }

    override fun countAlbum(): Long {
        return Albums.select(Albums.id).count()
    }

    override fun countAlbumByUserId(id: Long): Long {
        return Albums.select(Albums.id)
            .where { Albums.userId eq id }
            .count()
    }

    override fun listAlbumByUserId(userId: Long): List<Album> {
        return Albums.selectAll()
            .where { Albums.userId eq userId }
            .map { toAlbum(it) }
    }

    override fun pagination(userId: Long, pageRequest: PageRequest): PageResult<AlbumPageVO> {
        val query = { query: Query ->
            query
                .adjustColumnSet {
                    leftJoin(Images) { Images.albumId eq Albums.id }
                        .innerJoin(Users) { Albums.userId eq Users.id }
                }
                .adjustSelect { select(Albums.fields + Users.defaultAlbumId + Images.id.count()) }
                .groupBy(Albums.id, Albums.name, Albums.createTime, Users.defaultAlbumId)
                .also { it.andWhere { Albums.userId eq userId } }
                .also {
                    pageRequest.additionalCondition?.let { map ->
                        map["albumName"]?.let { albumName ->
                            it.andWhere { Albums.name like "%$albumName%" }
                        }
                    }
                }
        }
        return fetchPage(Albums, pageRequest, query) { row ->
            AlbumPageVO(
                row[Albums.id].value,
                row[Albums.name],
                row[Images.id.count()],
                row[Albums.isUncategorized],
                row[Users.defaultAlbumId] == row[Albums.id].value,
                row[Albums.createTime]
            )
        }
    }

    override fun paginationForManage(pageRequest: PageRequest): PageResult<AlbumManagePageVO> {
        val query = { query: Query ->
            query
                .adjustColumnSet {
                    leftJoin(Images) { Images.albumId eq Albums.id }
                        .innerJoin(Users) { Albums.userId eq Users.id }
                }
                .adjustSelect { select(Albums.fields + Users.id + Users.name + Users.email + Users.defaultAlbumId + Images.id.count()) }
                .groupBy(Albums.id, Albums.name, Albums.createTime, Users.id, Users.name, Users.email, Users.defaultAlbumId)
                .also {
                    pageRequest.additionalCondition?.let { map ->
                        map["userId"]?.let { userId ->
                            it.andWhere { Albums.userId eq userId.toLong() }
                        }
                        map["albumName"]?.let { albumName ->
                            it.andWhere { Albums.name like "%$albumName%" }
                        }
                    }
                }
        }
        return fetchPage(Albums, pageRequest, query) { row ->
            AlbumManagePageVO(
                row[Albums.id].value,
                row[Albums.name],
                row[Users.id].value,
                row[Users.name],
                row[Users.email],
                row[Images.id.count()],
                row[Albums.createTime]
            )
        }
    }

    private fun toAlbum(it: ResultRow) = Album(
        it[Albums.id].value,
        it[Albums.userId],
        it[Albums.name],
        it[Albums.description],
        it[Albums.isUncategorized],
        it[Albums.createTime],
    )
}