package com.yash.mfg.dto

import java.time.LocalDate

data class DateRangeRequestDTO(
    val startDate: LocalDate,
    val endDate: LocalDate
)
