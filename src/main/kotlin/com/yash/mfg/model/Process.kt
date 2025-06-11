package com.yash.mfg.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "process")
data class Process(
    @Id
    val processId: String = System.nanoTime().toString(),
    val processName: String,
    val plantId: String
)

