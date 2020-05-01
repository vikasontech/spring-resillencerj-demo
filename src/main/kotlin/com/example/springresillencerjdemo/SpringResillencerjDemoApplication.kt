package com.example.springresillencerjdemo


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.runApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.context.event.EventListener
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.awt.PageAttributes

@SpringBootApplication
class SpringResillencerjDemoApplication

@RestController
class SomeController {
    @Autowired
    private lateinit var someFacade: SomeFacade

    @GetMapping("/process", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun somRequest(@RequestParam id: Int): Mono<String> {
        return someFacade.processFacade(id)
    }
}

@Service
class SomeService {

    fun process(id: Int): Mono<String> {
        return if (id < 1) throw IllegalArgumentException("Illegal id")
        else Mono.just("Id Found!")
    }
}

@Service
class SomeFacade {
    @Autowired
    private lateinit var someService: SomeService

    fun processFacade(id: Int): Mono<String> {
        return someService.process(id)
    }
}

fun main(args: Array<String>) {
    runApplication<SpringResillencerjDemoApplication>(*args)
}
