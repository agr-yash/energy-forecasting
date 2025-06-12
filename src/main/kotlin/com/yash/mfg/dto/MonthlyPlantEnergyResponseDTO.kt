package com.yash.mfg.dto

data class MonthlyPlantEnergyResponseDTO(
    val processWiseMonthly: Map<String, Map<String, Double>>,
    val plantMonthlyTotal: Map<String, Double>
)

