package com.yash.mfg.dto

import java.time.LocalDate

data class ProcessEnergyConsumptionByProcessIdAndDateRangeRequestDTO(
    val processId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
