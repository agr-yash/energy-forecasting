package com.yash.mfg.llm

import com.fasterxml.jackson.databind.ObjectMapper
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
}
