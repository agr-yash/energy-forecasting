package com.yash.mfg.controller

import com.yash.mfg.model.EquipmentReading
import com.yash.mfg.service.EquipmentReadingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/equipment-readings")
class EquipmentReadingController(private val equipmentReadingService: EquipmentReadingService) {

    @GetMapping
    fun getAllEquipmentReadings(): List<EquipmentReading> = equipmentReadingService.getAllEquipmentReadings()
}
