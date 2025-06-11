package com.yash.mfg.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "equipment_reading")
data class EquipmentReading(
    @Id
    val equipmentId: String = System.nanoTime().toString(),
    val equipmentName: String,
    val processId: String,
    val energyConsumedKWh: Double,
    val currentAverage: Double,
    val voltageAverage: Int,
    val temperatureAverage: Double,
    val humidityPercentAverage: Double,
    val date: LocalDate,
    val co2EmissionsKgAverage: Double
)
