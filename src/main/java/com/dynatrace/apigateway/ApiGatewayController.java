package com.dynatrace.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/controller")
public class ApiGatewayController {

	private final Logger log = LoggerFactory.getLogger(ApiGatewayController.class);
	
	private final WebClient.Builder webClientBuilder;
	private final AtomicInteger pojoId = new AtomicInteger();

	public ApiGatewayController(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	private Mono<Pojo> requestPojo() {
		log.info("ENTER requestPojo");
		Mono<Pojo> res = webClientBuilder.build().get()
				.uri("http://localhost:8080/controller/pojo")
				.retrieve()
				.bodyToMono(Pojo.class);
		log.info("EXIT requestPojo");
		return res;
	}

	private Mono<Pojo> requestMojo() {
		log.info("ENTER requestMojo");
		Mono<Pojo> res = webClientBuilder.build().get()
				.uri("http://localhost:8080/controller/mojo")
				.retrieve()
				.bodyToMono(Pojo.class);
		log.info("EXIT requestMojo");
		return res;
	}

	@GetMapping(value = "backend")
	public Mono<Pojo> backend() {
		log.info("ENTER backend");
		Mono<Pojo> res = requestPojo();
		log.info("EXIT backend");
		return res;
	}

	@GetMapping(value = "backendmap")
	public Mono<Pojo> backendmap() {
		log.info("ENTER backendmap");
		Mono<Pojo> res = requestPojo().flatMap(it -> requestMojo());
		log.info("EXIT backendmap");
		return res;
	}

	@GetMapping(value = "pojo")
	public Mono<Pojo> getPojo() {
		log.info("ENTER getPojo");
		Mono<Pojo> res = Mono.just(new Pojo(pojoId.incrementAndGet()));
		log.info("EXIT getPojo");
		return res;
	}

	@GetMapping(value = "mojo")
	public Mono<Pojo> getMojo() {
		log.info("ENTER getMojo");
		Mono<Pojo> res = Mono.just(new Pojo(pojoId.incrementAndGet()));
		log.info("EXIT getMojo");
		return res;
	}

}