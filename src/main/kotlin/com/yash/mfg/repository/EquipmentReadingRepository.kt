package com.yash.mfg.repository

import com.yash.mfg.model.EquipmentReading
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EquipmentReadingRepository : MongoRepository<EquipmentReading, String> {
    fun findByProcessId(processId: String): List<EquipmentReading>
}
