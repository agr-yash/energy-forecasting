package com.yash.mfg.repository

import com.yash.mfg.model.EquipmentReading
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface EquipmentReadingRepository : MongoRepository<EquipmentReading, String> {

    fun findByProcessId(processId: String): List<EquipmentReading>

    @Query("{ 'date': { \$gte: ?0, \$lte: ?1 } }")
    fun findByDateBetweenAsString(startDate: String, endDate: String): List<EquipmentReading>

    @Query("{ 'processId': { \$in: ?0 }, 'date': { \$gte: ?1, \$lte: ?2 } }")
    fun findByProcessIdInAndDateBetween(
        processIds: List<String>,
        start: String,
        end: String
    ): List<EquipmentReading>

}
