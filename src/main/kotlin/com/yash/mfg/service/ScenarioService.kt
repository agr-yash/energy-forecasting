package com.yash.mfg.service

import com.yash.mfg.dto.EnergyAndEmissionsResponseDTO
import com.yash.mfg.llm.ForecastLLMService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ScenarioService(
    private val processService: ProcessService,
    private val llmService: ForecastLLMService
) {

    fun getNewScenario(
        plantId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        volChangePercent: Double,
        renewableEnergyChangePercent: Double
    ): EnergyAndEmissionsResponseDTO {
        val res: EnergyAndEmissionsResponseDTO = processService.getEnergyAndCo2ByPlantIdAndDateRangeForecasted(
            plantId,
            startDate,
            endDate,
        )

        val answer = llmService.getForecastedEnergyAndEmissions(
            res,
            volChangePercent,
            renewableEnergyChangePercent
        )
        return answer
    }

}