package com.yash.mfg.controller

import com.yash.mfg.dto.EnergyAndEmissionsResponseDTO
import com.yash.mfg.dto.ScenarioRequestDTO
import com.yash.mfg.service.ScenarioService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/api/scenario")
class ScenarioController(private val scenarioService: ScenarioService) {

    @PostMapping
    fun getNewScenario(@RequestBody scenarioRequestDTO: ScenarioRequestDTO): ResponseEntity<EnergyAndEmissionsResponseDTO> {
        val result = scenarioService.getNewScenario(
            scenarioRequestDTO.plantId,
            scenarioRequestDTO.startDate,
            scenarioRequestDTO.endDate,
            scenarioRequestDTO.volChangePercent,
            scenarioRequestDTO.renewableEnergyChangePercent
        )
        return ResponseEntity.ok(result)
    }
}