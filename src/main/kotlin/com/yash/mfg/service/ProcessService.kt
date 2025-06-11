package com.yash.mfg.service

import com.yash.mfg.model.Process
import com.yash.mfg.repository.EquipmentReadingRepository
import com.yash.mfg.repository.ProcessRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ProcessService(private val processRepository: ProcessRepository, private val equipmentReadingRepository: EquipmentReadingRepository) {

    fun getAllProcesses(): List<Process> = processRepository.findAll()

    fun getProcessesByPlantId(plantId: String): List<Process> = processRepository.findByPlantId(plantId)

    fun getTotalEnergyConsumedByPlantIdAndDateRange(
        plantId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Double {
        return processRepository.findByPlantId(plantId)
            .flatMap { process ->
                equipmentReadingRepository.findByProcessId(process.processId)
            }
            .filter { reading ->
                reading.date in startDate..endDate
            }
            .sumOf { it.energyConsumedKWh }
    }
    fun getTotalEnergyConsumedByProcessIdAndDateRange(
        processId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Double {
        return equipmentReadingRepository.findByProcessId(processId)
            .filter { reading ->
                reading.date in startDate..endDate
            }
            .sumOf { it.energyConsumedKWh }
    }
}
