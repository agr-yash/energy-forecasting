package com.yash.mfg.controller

import com.yash.mfg.dto.MonthlyPlantEnergyResponseDTO
import com.yash.mfg.dto.TotalEnergyConsumedByDateRangeRequestDTO
import com.yash.mfg.model.Plant
import com.yash.mfg.model.Process
import com.yash.mfg.service.PlantService
import com.yash.mfg.service.ProcessService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/plants")
class PlantController(private val plantService: PlantService, private val processService: ProcessService) {

    @GetMapping
    fun getAllPlants(): List<Plant> = plantService.getAllPlants()

    @PostMapping
    fun getEnergyByPlantIdAndDateRange(
        @RequestBody totalEnergyConsumedByDateRangeRequestDTO: TotalEnergyConsumedByDateRangeRequestDTO
    ): Double {

        return processService.getTotalEnergyConsumedByPlantIdAndDateRange(
            totalEnergyConsumedByDateRangeRequestDTO.plantId,
            totalEnergyConsumedByDateRangeRequestDTO.startDate,
            totalEnergyConsumedByDateRangeRequestDTO.endDate
        )
    }

    @GetMapping("/{id}")
    fun getProcessesByPlantId(@PathVariable id: String): List<Process> =
        processService.getProcessesByPlantId(id)

    @GetMapping("/monthly/{id}")
    fun getMonthlyEnergyConsumptionByPlantId(@PathVariable id: String): ResponseEntity<MonthlyPlantEnergyResponseDTO> {
        val response = processService.getMonthlyEnergyConsumptionGroupedByProcess(id)
        return ResponseEntity.ok(response)
    }


    //Forecasting APIs
    @PostMapping("/forecasted")
    fun getEnergyByPlantIdAndDateRangeForecasted(
        @RequestBody totalEnergyConsumedByDateRangeRequestDTO: TotalEnergyConsumedByDateRangeRequestDTO
    ): Double {
        return processService.getTotalEnergyConsumedByPlantIdAndDateRangeForecasted(
            totalEnergyConsumedByDateRangeRequestDTO.plantId,
            totalEnergyConsumedByDateRangeRequestDTO.startDate,
            totalEnergyConsumedByDateRangeRequestDTO.endDate
        )
    }
}
