package com.dynatrace.apigateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@SpringBootApplication
public class SpringCloudGatewaySample {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudGatewaySample.class, new String[0]);
	}

	public void configure(String config) {
	}

	@Value("classpath:/static/index.html")
	private Resource indexHtml;

	@Bean
	RouterFunction<?> routerFunction() {
		RouterFunction router = RouterFunctions.resources("/**", new ClassPathResource("static/"))
				.andRoute(RequestPredicates.GET("/"),
						request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml));
		return router;
	}

}

