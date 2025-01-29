package io.comunda.demo.gpon.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.springframework.stereotype.Component

@Component
class GponApiWorker {

    @JobWorker(type = "Activity_gpon_call_1")
    fun handleGponCall(job: ActivatedJob) {
        println("Executing GPON API call for job ${job.key}")
    }
}