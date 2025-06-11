package com.yash.mfg.service

import com.yash.mfg.model.EquipmentReading
import com.yash.mfg.repository.EquipmentReadingRepository
import org.springframework.stereotype.Service

@Service
class EquipmentReadingService(private val equipmentReadingRepository: EquipmentReadingRepository) {
    fun getAllEquipmentReadings(): List<EquipmentReading> = equipmentReadingRepository.findAll()
}
