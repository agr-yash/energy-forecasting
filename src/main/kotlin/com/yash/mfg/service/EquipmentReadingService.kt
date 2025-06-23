package com.yash.mfg.service

import com.yash.mfg.model.EquipmentReading
import com.yash.mfg.repository.EquipmentReadingRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class EquipmentReadingService(private val equipmentReadingRepository: EquipmentReadingRepository) {

    fun getAllEquipmentReadingsByDateRange(startDate: LocalDate, endDate: LocalDate): List<EquipmentReading> {
        return equipmentReadingRepository.findByDateBetweenAsString(startDate.toString(), endDate.toString())
    }

    fun getEquipmentReadingsByProcessIdAndDateRange(
        processId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<EquipmentReading> {
        return equipmentReadingRepository.findByProcessId(processId).filter { reading ->
            reading.date in startDate..endDate
        }
    }

    //FORECASTED
    fun getEquipmentReadingsByProcessIdAndDateRangeForecasted(
        processId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<EquipmentReading> {
        val totalDays = endDate.toEpochDay() - startDate.toEpochDay()

        val baseEndDate = LocalDate.of(2025, 6, 9)
        val baseStartDate = baseEndDate.minusDays(totalDays)

        // Fetch base readings from the past window
        val baseReadings = equipmentReadingRepository
            .findByDateBetweenAsString(baseStartDate.toString(), baseEndDate.toString())
            .filter { it.processId == processId }

        // Compute date shift
        val diff = startDate.toEpochDay() - baseStartDate.toEpochDay()

        return baseReadings.map { reading ->
            reading.copy(
                date = reading.date.plusDays(diff),
                energyConsumedKWh = reading.energyConsumedKWh * 1.1,
                co2EmissionsKgAverage = reading.co2EmissionsKgAverage * 1.1,
                temperatureAverage = reading.temperatureAverage * 1.05,
                humidityPercentAverage = reading.humidityPercentAverage * 1.05
            )
        }
    }

    fun getAllEquipmentReadingsByDateRangeForecasted(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<EquipmentReading> {
        val totalDays = endDate.toEpochDay() - startDate.toEpochDay()

        // Base data reference window
        val baseEndDate = LocalDate.of(2025, 6, 9)
        val baseStartDate = baseEndDate.minusDays(totalDays)

        // Fetch base readings from the past window
        val baseReadings = equipmentReadingRepository
            .findByDateBetweenAsString(baseStartDate.toString(), baseEndDate.toString())

        // Compute date shift
        val diff = startDate.toEpochDay() - baseStartDate.toEpochDay()

        return baseReadings.map { reading ->
            reading.copy(
                date = reading.date.plusDays(diff),
                energyConsumedKWh = reading.energyConsumedKWh * 1.1,
                co2EmissionsKgAverage = reading.co2EmissionsKgAverage * 1.1,
                temperatureAverage = reading.temperatureAverage * 1.05,
                humidityPercentAverage = reading.humidityPercentAverage * 1.05
            )
        }
    }
}
