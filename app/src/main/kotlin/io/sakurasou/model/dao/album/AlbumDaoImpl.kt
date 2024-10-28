package io.sakurasou.model.dao.album

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.AlbumPageVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.exception.dao.MissingUserDefaultAlbumException
import io.sakurasou.model.dao.image.Images
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

    override fun countAlbumByUserId(id: Long): Long {
        return Albums.select(Albums.id.count())
            .where { Albums.userId eq id }
            .map { it[Albums.id.count()] }
            .first()
    }

    override fun listAlbumByUserId(userId: Long): List<Album> {
        return Albums.selectAll()
            .where { Albums.userId eq userId }
            .map { toAlbum(it) }
    }

    override fun pagination(userId: Long?, pageRequest: PageRequest): PageResult<AlbumPageVO> {
        val customWhereCond: (Query) -> Query = {
            val query = it.adjustColumnSet { join(Images, JoinType.INNER) { Images.albumId eq Albums.id } }
                .adjustSelect { select(Albums.fields + Images.id.count()) }
                .groupBy(Albums.id, Albums.name, Albums.createTime)
            userId?.let { query.andWhere { Albums.userId eq userId } }
            query
        }
        return fetchPage(Albums, pageRequest, customWhereCond) { row ->
            AlbumPageVO(
                row[Albums.id].value,
                row[Albums.name],
                row[Images.id.count()],
                row[Albums.isUncategorized]
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