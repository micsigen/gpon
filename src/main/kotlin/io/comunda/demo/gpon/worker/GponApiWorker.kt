package io.comunda.demo.gpon.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import io.comunda.demo.gpon.config.AppConfig
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@Component
class GponApiWorker(
    private val webClient: WebClient,
    private val appConfig: AppConfig
) {
    private val baseUrl = appConfig.baseUrl

    @JobWorker(type = "Activity_gpon_call_1")
    fun handleGponCall(job: ActivatedJob): Map<String, Any> {
        println("Executing GPON API call for job ${job.key}")
        val number = job.variablesAsMap.getOrDefault("number", 10) as Int

        val result = webClient.get()
            .uri("$baseUrl/fibonacci?n=$number")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String::class.java)
            .block() ?: 0

        return mapOf(
            "fibonacciResult" to result,
            "inputNumber" to number,
            "timestamp" to LocalDateTime.now().toString()
        )
    }
}