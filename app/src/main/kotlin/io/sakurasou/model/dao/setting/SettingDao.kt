package io.sakurasou.model.dao.setting

import io.sakurasou.model.dto.SettingInsertDTO
import io.sakurasou.model.dto.SettingUpdateDTO
import io.sakurasou.model.entity.Setting

/**
 * @author ShiinaKin
 * 2024/9/7 14:07
 */
interface SettingDao {
    fun saveSetting(settingInsertDTO: SettingInsertDTO)
    fun updateSettingByName(settingUpdateDTO: SettingUpdateDTO)
    fun getSettingByName(name: String): Setting?
}