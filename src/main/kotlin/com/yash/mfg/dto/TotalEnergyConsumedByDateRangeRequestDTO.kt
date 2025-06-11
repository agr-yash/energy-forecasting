package com.yash.mfg.dto

import java.time.LocalDate

data class TotalEnergyConsumedByDateRangeRequestDTO(
    val plantId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
