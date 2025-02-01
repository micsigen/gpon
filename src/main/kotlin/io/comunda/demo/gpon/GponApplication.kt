package io.comunda.demo.gpon

import io.camunda.zeebe.spring.client.annotation.Deployment
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@Deployment(resources = ["classpath:gpon_process_1.bpmn"])
class GponApplication() {

}

fun main(args: Array<String>) {
    runApplication<GponApplication>(*args)
}

