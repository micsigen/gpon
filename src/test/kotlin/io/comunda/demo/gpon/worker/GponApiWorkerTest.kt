package io.comunda.demo.gpon.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.test.ZeebeSpringTest
import io.comunda.demo.gpon.config.AppConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import kotlin.text.get

@SpringBootTest
@Testcontainers
@ZeebeSpringTest
class GponApiWorkerTest {

    private lateinit var mockWebServer: MockWebServer

    @Autowired
    private lateinit var gponApiWorker: GponApiWorker

    companion object {
        @Container
        private val zeebe = GenericContainer("camunda/zeebe:8.2.4")
            .withExposedPorts(26500)

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("zeebe.client.broker.gateway-address")
                { "${zeebe.host}:${zeebe.getMappedPort(26500)}" }
        }
    }

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()

        val appConfig = AppConfig().apply {
            baseUrl = mockWebServer.url("/").toString()
        }

        gponApiWorker = GponApiWorker(webClient, appConfig)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should handle GPON API call successfully`() {
        // Given
        val expectedResult = "55"
        mockWebServer.enqueue(
            MockResponse()
                .setBody(expectedResult.toString())
                .setResponseCode(200)
        )

        val mockJob = Mockito.mock(ActivatedJob::class.java)
        val variables = mapOf("number" to 10)
        Mockito.`when`(mockJob.variablesAsMap).thenReturn(variables)
        Mockito.`when`(mockJob.key).thenReturn(1L)

        // When
        val result = gponApiWorker.handleGponCall(mockJob)

        // Then
        assertThat(result["fibonacciResult"]).isEqualTo(expectedResult)
        assertThat(result["inputNumber"]).isEqualTo(10)
        assertThat(result["timestamp"]).isNotNull()

        val recordedRequest = mockWebServer.takeRequest()
        assertThat(recordedRequest.path).isEqualTo("/fibonacci?n=10")
    }

    @Test
    fun `should handle GPON API call with default value when number is missing`() {
        // Given
        val expectedResult = "55"
        mockWebServer.enqueue(
            MockResponse()
                .setBody(expectedResult.toString())
                .setResponseCode(200)
        )

        val mockJob = Mockito.mock(ActivatedJob::class.java)
        Mockito.`when`(mockJob.variablesAsMap).thenReturn(emptyMap())
        Mockito.`when`(mockJob.key).thenReturn(1L)

        // When
        val result = gponApiWorker.handleGponCall(mockJob)

        // Then
        Assertions.assertThat(result["fibonacciResult"]).isEqualTo(expectedResult)
        Assertions.assertThat(result["inputNumber"]).isEqualTo(10) // default value
        Assertions.assertThat(result["timestamp"]).isNotNull()
    }
}