package com.yash.mfg.controller

import com.yash.mfg.dto.EnergyAndEmissionsResponseDTO
import com.yash.mfg.dto.ProcessEnergyConsumptionByProcessIdAndDateRangeRequestDTO
import com.yash.mfg.dto.TotalEnergyConsumedByDateRangeRequestDTO
import com.yash.mfg.model.Process
import com.yash.mfg.service.ProcessService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/processes")
class ProcessController(private val processService: ProcessService) {

    @GetMapping
    fun getAllProcesses(): List<Process> = processService.getAllProcesses()

    @PostMapping
    fun getEnergyAndEmissionsByProcessIdAndDateRange(
        @RequestBody request: ProcessEnergyConsumptionByProcessIdAndDateRangeRequestDTO
    ): ResponseEntity<EnergyAndEmissionsResponseDTO> {
        val result = processService.getEnergyAndEmissionsByProcessIdAndDateRange(
            request.processId,
            request.startDate,
            request.endDate
        )
        return ResponseEntity.ok(result)
    }
}
