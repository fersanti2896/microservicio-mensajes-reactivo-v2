package com.aleph.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MicroservicioMensajesReactivoV2Application {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioMensajesReactivoV2Application.class, args);
	}

}
