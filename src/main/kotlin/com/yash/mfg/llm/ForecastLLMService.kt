package com.yash.mfg.llm

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class ForecastLLMService(
    private val chatClientBuilder: ChatClient.Builder
) {

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

        val chatClient = chatClientBuilder.build()
        val rawResponse = chatClient.prompt(prompt).call().content()

        val jsonRegex = Regex("""\{[\s\S]*?}""")
        val jsonMatch = jsonRegex.find(rawResponse ?: "")

        val json = jsonMatch?.value ?: throw RuntimeException("No JSON found in LLM response: $rawResponse")

        return try {
            val mapper = ObjectMapper()
            mapper.readValue(json, Map::class.java) as Map<String, Double>
        } catch (e: Exception) {
            throw RuntimeException("Failed to parse LLM JSON: $json", e)
        }
    }
}
