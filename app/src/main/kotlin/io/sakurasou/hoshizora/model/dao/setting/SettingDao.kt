package io.sakurasou.hoshizora.model.dao.setting

import io.sakurasou.hoshizora.model.dto.SettingInsertDTO
import io.sakurasou.hoshizora.model.dto.SettingUpdateDTO
import io.sakurasou.hoshizora.model.entity.Setting

/**
 * @author ShiinaKin
 * 2024/9/7 14:07
 */
interface SettingDao {
    fun saveSetting(settingInsertDTO: SettingInsertDTO)

    fun updateSettingByName(settingUpdateDTO: SettingUpdateDTO): Int

    fun getSettingByName(name: String): Setting?
}
