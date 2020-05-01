package com.example.springresillencerjdemo


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@SpringBootApplication
class SpringResilienceDemoApplication

@RestController
class SomeController {
    @Autowired
    private lateinit var someService: SomeService

    @GetMapping("/process", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun somRequest(@RequestParam id: Int): Mono<String> {
        return someService.process(id)
    }
}

@Service
class SomeService {

    fun process(id: Int): Mono<String> {
        return if (id < 1) throw IllegalArgumentException("Illegal id")
        else Mono.just("Id Found!")
    }
}

fun main(args: Array<String>) {
    runApplication<SpringResilienceDemoApplication>(*args)
}
