package com.example.springresillencerjdemo


import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.function.Function


@SpringBootApplication
class SpringResilienceDemoApplication {

    @Bean
    fun defaultCustomizer(): Customizer<ReactiveResilience4JCircuitBreakerFactory> {
        return Customizer {
            it.configureDefault {
                Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration()
                        .apply {
                            id = "default"
                            circuitBreakerConfig = CircuitBreakerConfig.ofDefaults()
                            timeLimiterConfig = TimeLimiterConfig.ofDefaults()

                        }
            }
        }
    }
}

@RestController
class SomeController {
    @Autowired
    private lateinit var someService: SomeService
    @Autowired
    private lateinit var reactiveCircuitBreakerFactory: ReactiveCircuitBreakerFactory<*, *>

    @GetMapping("/process", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun somRequest(@RequestParam id: Int): Mono<String> {
        return reactiveCircuitBreakerFactory
                .create("someService")
                .run(someService.process(id), Function {
                    Mono.just("just recovered from error! => ${it.localizedMessage}")
                })

    }
}

@Service
class SomeService {
    fun process(id: Int): Mono<String> {
        return if (id < 1) Mono.error{ IllegalArgumentException("oh!! invalid id") }
        else Mono.just("Id Found!")
    }
}

fun main(args: Array<String>) {
    runApplication<SpringResilienceDemoApplication>(*args)
}
