package com.yash.mfg.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "plant")
data class Plant(
    @Id
    val plantId: String = System.nanoTime().toString(),
    val plantName: String,
    val city: String,
    val country: String
)
