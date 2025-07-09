package com.yash.mfg.llm

import com.fasterxml.jackson.databind.ObjectMapper
import com.yash.mfg.dto.EnergyAndEmissionsResponseDTO
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class ForecastLLMService(
    private val chatClientBuilder: ChatClient.Builder
) {

    private val logger = LoggerFactory.getLogger(ForecastLLMService::class.java)

    fun getForecast(
        energy: Double,
        co2: Double,
        temp: Double,
        humidity: Double
    ): Map<String, Double> {
        val prompt = """
            You are an energy forecasting AI. Based on current equipment readings, forecast the future values with realistic values.

            Here are the current readings:
            {
              "energyConsumedKWh": %.2f,
              "co2EmissionsKgAverage": %.2f,
              "temperatureAverage": %.2f,
              "humidityPercentAverage": %.2f
            }
            Instructions:
            - Keep your answer strictly in JSON format with keys:
              - energyConsumedKWh
              - co2EmissionsKgAverage
              - temperatureAverage
              - humidityPercentAverage

            Respond ONLY with a JSON object. No extra text.
        """.trimIndent().format(energy, co2, temp, humidity)

        logger.info("Generated prompt for LLM:\n{}", prompt)

        val chatClient = chatClientBuilder.build()

        val rawResponse = try {
            chatClient.prompt(prompt).call().content()
        } catch (e: Exception) {
            logger.error("Failed to get response from LLM", e)
            throw RuntimeException("LLM request failed", e)
        }

        logger.info("Raw response from LLM:\n{}", rawResponse)

        val jsonRegex = Regex("""\{[\s\S]*?}""")
        val jsonMatch = jsonRegex.find(rawResponse ?: "")

        val json = jsonMatch?.value ?: run {
            logger.error("No JSON found in LLM response: {}", rawResponse)
            throw RuntimeException("No JSON found in LLM response: $rawResponse")
        }

        logger.info("Extracted JSON from LLM response:\n{}", json)

        return try {
            val mapper = ObjectMapper()
            val result = mapper.readValue(json, Map::class.java) as Map<String, Double>
            logger.info("Parsed forecast result: {}", result)
            result
        } catch (e: Exception) {
            logger.error("Failed to parse JSON from LLM response", e)
            throw RuntimeException("Failed to parse LLM JSON: $json", e)
        }
    }

    fun getForecastedEnergyAndEmissions(
        energyAndEmissions: EnergyAndEmissionsResponseDTO,
        volChangePercent: Double,
        renewableEnergyChangePercent: Double
    ): EnergyAndEmissionsResponseDTO {
        val prompt = """
    You are an expert energy forecasting AI assisting a manufacturing plant.

    The plant has the following current metrics:
    {
      "totalEnergyConsumedKWh": ${"%.2f".format(energyAndEmissions.totalEnergyConsumedKWh)},
      "totalCo2EmissionsKg": ${"%.2f".format(energyAndEmissions.totalCo2EmissionsKg)},
      "volumeChangePercent": ${"%.2f".format(volChangePercent)},
      "renewableEnergyChangePercent": ${"%.2f".format(renewableEnergyChangePercent)}
    }

    Definitions:
    - volumeChangePercent represents the % change in number of production units. For example, -10 means production dropped by 10%.
    - renewableEnergyChangePercent indicates the % increase in renewable energy usage. For example, 20 means 20% more renewable energy is used, which should reduce CO2 emissions.

    Instructions:
    - Adjust **energyConsumedKWh** based on volumeChangePercent. A decrease in production volume should proportionally reduce energy.
    - Adjust **co2EmissionsKg** based on renewableEnergyChangePercent. More renewables lead to proportionally less CO2 emissions.
    - Maintain realistic values and round to two decimal places.
    - Return the forecast strictly as a JSON object with keys:
      - totalEnergyConsumedKWh
      - totalCo2EmissionsKg

    No extra text. Only JSON.
""".trimIndent()



        logger.info("Prompt for forecasted energy and emissions:\n{}", prompt)

        val chatClient = chatClientBuilder.build()
        val rawResponse = try {
            chatClient.prompt(prompt).call().content()
        } catch (e: Exception) {
            logger.error("Failed to get response from LLM", e)
            throw RuntimeException("LLM request failed", e)
        }

        logger.info("Raw LLM response:\n{}", rawResponse)

        val jsonRegex = Regex("""\{[\s\S]*?}""")
        val jsonMatch = jsonRegex.find(rawResponse ?: "")

        val json = jsonMatch?.value ?: run {
            logger.error("No JSON found in LLM response: {}", rawResponse)
            throw RuntimeException("No JSON found in LLM response: $rawResponse")
        }

        logger.info("Extracted JSON:\n{}", json)

        return try {
            val mapper = ObjectMapper()
            val resultMap = mapper.readValue(json, Map::class.java) as Map<String, Double>
            logger.info("Parsed forecasted values: {}", resultMap)

            EnergyAndEmissionsResponseDTO(
                totalEnergyConsumedKWh = resultMap["totalEnergyConsumedKWh"] ?: error("Missing energy value"),
                totalCo2EmissionsKg = resultMap["totalCo2EmissionsKg"] ?: error("Missing CO2 value")
            )
        } catch (e: Exception) {
            logger.error("Failed to parse JSON from LLM response", e)
            throw RuntimeException("Failed to parse LLM JSON: $json", e)
        }
    }

}
