package com.yash.mfg.repository

import com.yash.mfg.model.Process
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProcessRepository : MongoRepository<Process, String> {
    fun findByPlantId(plantId: String): List<Process>
}
