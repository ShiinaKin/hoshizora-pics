package io.sakurasou.model.dao.setting

import io.sakurasou.model.dto.SettingInsertDTO
import io.sakurasou.model.dto.SettingUpdateDTO
import io.sakurasou.model.entity.Setting
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

/**
 * @author ShiinaKin
 * 2024/9/7 14:07
 */
class SettingDaoImpl : SettingDao {
    override fun saveSetting(settingInsertDTO: SettingInsertDTO) {
        Settings.insert {
            it[name] = settingInsertDTO.name
            it[config] = settingInsertDTO.config
            it[createTime] = settingInsertDTO.createTime
            it[updateTime] = settingInsertDTO.updateTime
        }
    }

    override fun updateSettingByName(settingUpdateDTO: SettingUpdateDTO): Int {
        return Settings.update({ Settings.name eq settingUpdateDTO.name }) {
            it[config] = settingUpdateDTO.config
            it[updateTime] = settingUpdateDTO.updateTime
        }
    }

    override fun getSettingByName(name: String): Setting? {
        return Settings.selectAll()
            .where { Settings.name eq name }
            .map {
                Setting(
                    it[Settings.name],
                    it[Settings.config],
                    it[Settings.createTime],
                    it[Settings.updateTime]
                )
            }
            .firstOrNull()
    }
}