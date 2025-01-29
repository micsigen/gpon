package io.comunda.demo.gpon

import io.camunda.zeebe.spring.client.annotation.Deployment
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@Deployment(resources = ["classpath:gpon_process_1.bpmn"])
@RestController
class GponApplication {

    @GetMapping("/hello")
    fun helloWorld(): String {
        return "Hello World!"
    }
}

fun main(args: Array<String>) {
    runApplication<GponApplication>(*args)
}
