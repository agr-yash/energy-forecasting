package com.yash.mfg.service

import com.yash.mfg.model.EquipmentReading
import com.yash.mfg.repository.EquipmentReadingRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class EquipmentReadingService(private val equipmentReadingRepository: EquipmentReadingRepository) {
    fun getAllEquipmentReadings(): List<EquipmentReading> = equipmentReadingRepository.findAll()

    fun getEquipmentReadingsByProcessIdAndDateRange(
        processId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<EquipmentReading> {
        return equipmentReadingRepository.findByProcessId(processId).filter { reading ->
            reading.date in startDate..endDate
        }
    }
}
