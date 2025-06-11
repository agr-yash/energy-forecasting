package com.yash.mfg.repository

import com.yash.mfg.model.Plant
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PlantRepository : MongoRepository<Plant, String> {
}
