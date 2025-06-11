package com.yash.mfg.service

import com.yash.mfg.model.Plant
import com.yash.mfg.repository.PlantRepository
import org.springframework.stereotype.Service

@Service
class PlantService(private val plantRepository: PlantRepository) {
    fun getAllPlants(): List<Plant> = plantRepository.findAll()
}
