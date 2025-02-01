package io.comunda.demo.gpon.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@Component
class GponApiWorker(
    private val webClient: WebClient
) {

    @JobWorker(type = "Activity_gpon_call_1")
    fun handleGponCall(job: ActivatedJob): Map<String, Any> {
        println("Executing GPON API call for job ${job.key}")
        val number = job.variablesAsMap.getOrDefault("number", 10) as Int

        val result = webClient.get()
            .uri("http://localhost:8080/fibonacci/$number")
            .retrieve()
            .bodyToMono(Long::class.java)
            .block() ?: 0

        return mapOf(
            "fibonacciResult" to result,
            "inputNumber" to number,
            "timestamp" to LocalDateTime.now().toString()
        )
    }
}