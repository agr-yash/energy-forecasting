package com.yash.mfg.service

import com.yash.mfg.dto.EnergyAndEmissionsResponseDTO
import com.yash.mfg.dto.MonthlyPlantEnergyResponseDTO
import com.yash.mfg.llm.ForecastLLMService
import com.yash.mfg.model.Process
import com.yash.mfg.repository.EquipmentReadingRepository
import com.yash.mfg.repository.ProcessRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ProcessService(private val processRepository: ProcessRepository, private val equipmentReadingRepository: EquipmentReadingRepository) {

    @Autowired
    lateinit var forecastLLMService: ForecastLLMService

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
    // Average calculations
    fun getAverageCO2ByPlantIdAndDateRange(
        plantId: String,
        start: LocalDate,
        end: LocalDate
    ): Double {
        val processIds = processRepository.findByPlantId(plantId).map { it.processId }
        val readings = equipmentReadingRepository.findByProcessIdInAndDateBetween(processIds, start, end)
        return readings.map { it.co2EmissionsKgAverage }.average()
    }

    fun getAverageTemperatureByPlantIdAndDateRange(
        plantId: String,
        start: LocalDate,
        end: LocalDate
    ): Double {
        val processIds = processRepository.findByPlantId(plantId).map { it.processId }
        val readings = equipmentReadingRepository.findByProcessIdInAndDateBetween(processIds, start, end)
        return readings.map { it.temperatureAverage }.average()
    }

    fun getAverageHumidityByPlantIdAndDateRange(
        plantId: String,
        start: LocalDate,
        end: LocalDate
    ): Double {
        val processIds = processRepository.findByPlantId(plantId).map { it.processId }
        val readings = equipmentReadingRepository.findByProcessIdInAndDateBetween(processIds, start, end)
        return readings.map { it.humidityPercentAverage }.average()
    }


    //FORECASTING
    fun getTotalEnergyConsumedByPlantIdAndDateRangeForecasted(
        plantId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Double {
        // 1. Adjust date range to fixed forecast period
        val newEndDate = LocalDate.of(2025, 6, 18)
        val days = endDate.toEpochDay() - startDate.toEpochDay()
        val newStartDate = newEndDate.minusDays(days)

        // 2. Get current actual values
        val energy = getTotalEnergyConsumedByPlantIdAndDateRange(
            plantId = plantId,
            startDate = newStartDate,
            endDate = newEndDate
        )

        val avgCo2 = getAverageCO2ByPlantIdAndDateRange(plantId, newStartDate, newEndDate)
        val avgTemp = getAverageTemperatureByPlantIdAndDateRange(plantId, newStartDate, newEndDate)
        val avgHumidity = getAverageHumidityByPlantIdAndDateRange(plantId, newStartDate, newEndDate)

        // 3. Forecast values using LLM
        val forecast = forecastLLMService.getForecast(
            energy = energy,
            co2 = avgCo2,
            temp = avgTemp,
            humidity = avgHumidity
        )
        // 4. Return forecasted energy
        return forecast["energyConsumedKWh"]
            ?: throw RuntimeException("Forecast result missing 'energyConsumedKWh'")
    }

    //Process-specific forecasting
    fun getEnergyAndEmissionsByProcessIdAndDateRangeForecasted(
        processId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): EnergyAndEmissionsResponseDTO {
        // 1. Adjust date range to fixed forecast period
        val newEndDate = LocalDate.of(2025, 6, 18)
        val days = endDate.toEpochDay() - startDate.toEpochDay()
        val newStartDate = newEndDate.minusDays(days)

        // 2. Get current actual values
        val readings = equipmentReadingRepository.findByProcessId(processId)
            .filter { it.date in newStartDate..newEndDate }

        if (readings.isEmpty()) {
            throw RuntimeException("No readings found for process $processId in the given date range")
        }

        val energy = readings.sumOf { it.energyConsumedKWh }
        val avgCo2 = readings.sumOf { it.co2EmissionsKgAverage }
        val avgTemp = readings.map { it.temperatureAverage }.average()
        val avgHumidity = readings.map { it.humidityPercentAverage }.average()

        // 3. Forecast values using LLM
        val forecast = forecastLLMService.getForecast(
            energy = energy,
            co2 = avgCo2,
            temp = avgTemp,
            humidity = avgHumidity
        )

        // 4. Return forecasted energy and emissions
        return EnergyAndEmissionsResponseDTO(
            totalEnergyConsumedKWh = forecast["energyConsumedKWh"]
                ?: throw RuntimeException("Forecast result missing 'energyConsumedKWh'"),
            totalCo2EmissionsKg = forecast["co2EmissionsKgAverage"]
                ?: throw RuntimeException("Forecast result missing 'co2EmissionsKgAverage'")
        )
    }
}
