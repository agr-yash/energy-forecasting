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

    fun findByProcessIdInAndDateBetween(processIds: List<String>, startDate: LocalDate, endDate: LocalDate): List<EquipmentReading>

}
