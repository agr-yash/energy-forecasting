package com.yash.mfg.controller

import com.yash.mfg.dto.DateRangeRequestDTO
import com.yash.mfg.dto.ProcessEnergyConsumptionByProcessIdAndDateRangeRequestDTO
import com.yash.mfg.model.EquipmentReading
import com.yash.mfg.service.EquipmentReadingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/equipment-readings")
class EquipmentReadingController(private val equipmentReadingService: EquipmentReadingService) {

    @PostMapping
    fun getAllEquipmentReadingsByDateRangeAndProcessId(
        @RequestBody processEnergyConsumptionByProcessIdAndDateRangeRequestDTO: ProcessEnergyConsumptionByProcessIdAndDateRangeRequestDTO
    ): ResponseEntity<List<EquipmentReading>> {
        val equipmentReadings = equipmentReadingService.getEquipmentReadingsByProcessIdAndDateRange(
            processEnergyConsumptionByProcessIdAndDateRangeRequestDTO.processId,
            processEnergyConsumptionByProcessIdAndDateRangeRequestDTO.startDate,
            processEnergyConsumptionByProcessIdAndDateRangeRequestDTO.endDate
        )
        return ResponseEntity.ok(equipmentReadings)
    }

    @PostMapping("/with-date")
    fun getAllEquipmentReadings(
        @RequestBody dateRangeRequestDTO: DateRangeRequestDTO
    ): List<EquipmentReading> = equipmentReadingService.getAllEquipmentReadingsByDateRange(
        dateRangeRequestDTO.startDate,
        dateRangeRequestDTO.endDate
    )

    //Forecasting APIs
    @PostMapping("/forecasted")
    fun getAllEquipmentReadingsByDateRangeAndProcessIdForecasted(
        @RequestBody processEnergyConsumptionByProcessIdAndDateRangeRequestDTO: ProcessEnergyConsumptionByProcessIdAndDateRangeRequestDTO
    ): ResponseEntity<List<EquipmentReading>> {
        val equipmentReadings = equipmentReadingService.getEquipmentReadingsByProcessIdAndDateRangeForecasted(
            processEnergyConsumptionByProcessIdAndDateRangeRequestDTO.processId,
            processEnergyConsumptionByProcessIdAndDateRangeRequestDTO.startDate,
            processEnergyConsumptionByProcessIdAndDateRangeRequestDTO.endDate
        )
        return ResponseEntity.ok(equipmentReadings)
    }

    @PostMapping("/with-date/forecasted")
    fun getAllEquipmentReadingsForecasted(
        @RequestBody dateRangeRequestDTO: DateRangeRequestDTO
    ): List<EquipmentReading> = equipmentReadingService.getAllEquipmentReadingsByDateRangeForecasted(
        dateRangeRequestDTO.startDate,
        dateRangeRequestDTO.endDate
    )
}
