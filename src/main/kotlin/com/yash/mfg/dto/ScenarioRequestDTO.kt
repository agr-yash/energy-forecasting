package com.yash.mfg.dto

import java.time.LocalDate

data class ScenarioRequestDTO(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val volChangePercent: Double,
    val renewableEnergyChangePercent: Double,
    val plantId: String
)
