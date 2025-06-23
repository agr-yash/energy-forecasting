package com.yash.mfg.service

import com.yash.mfg.dto.EnergyAndEmissionsResponseDTO
import com.yash.mfg.dto.MonthlyPlantEnergyResponseDTO
import com.yash.mfg.model.Process
import com.yash.mfg.repository.EquipmentReadingRepository
import com.yash.mfg.repository.ProcessRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

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

    fun getEnergyAndEmissionsByProcessIdAndDateRange(
        processId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): EnergyAndEmissionsResponseDTO {
        val filteredReadings = equipmentReadingRepository.findByProcessId(processId)
            .filter { it.date in startDate..endDate }

        val totalEnergy = filteredReadings.sumOf { it.energyConsumedKWh }
        val totalEmissions = filteredReadings.sumOf { it.co2EmissionsKgAverage }

        return EnergyAndEmissionsResponseDTO(
            totalEnergyConsumedKWh = totalEnergy,
            totalCo2EmissionsKg = totalEmissions
        )
    }



    fun getMonthlyEnergyConsumptionGroupedByProcess(plantId: String): MonthlyPlantEnergyResponseDTO {
        val processes = processRepository.findByPlantId(plantId)
        val formatter = DateTimeFormatter.ofPattern("MMMM-yyyy")

        val processWiseMonthly = mutableMapOf<String, Map<String, Double>>()
        val plantMonthlyTotal = mutableMapOf<String, Double>()

        for (process in processes) {
            val readings = equipmentReadingRepository.findByProcessId(process.processId)

            val monthly = readings
                .groupBy { it.date.format(formatter) }
                .mapValues { (_, readingsInMonth) -> readingsInMonth.sumOf { it.energyConsumedKWh } }

            processWiseMonthly[process.processName] = monthly

            // Accumulate into plant total
            for ((month, energy) in monthly) {
                plantMonthlyTotal[month] = plantMonthlyTotal.getOrDefault(month, 0.0) + energy
            }
        }

        return MonthlyPlantEnergyResponseDTO(
            processWiseMonthly = processWiseMonthly,
            plantMonthlyTotal = plantMonthlyTotal
        )
    }

    //FORECASTING
    fun getTotalEnergyConsumedByPlantIdAndDateRangeForecasted(
        plantId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Double {
        val newEndDate = LocalDate.of(2025, 6, 9)
        val newStartDate = newEndDate.minusDays(endDate.toEpochDay() - startDate.toEpochDay())
        return this.getTotalEnergyConsumedByPlantIdAndDateRange(
            plantId = plantId,
            startDate = newStartDate,
            endDate = newEndDate
        ) *1.047
    }
}
