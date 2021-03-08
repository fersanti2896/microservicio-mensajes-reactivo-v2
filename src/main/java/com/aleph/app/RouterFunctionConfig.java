package com.aleph.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

import com.aleph.app.handler.Manejador;


@Configuration
public class RouterFunctionConfig {

	@Bean
	public RouterFunction<ServerResponse> routes(Manejador handler){
		return route(GET("/mensaje"), handler::obtenerMensaje)
			   .andRoute(POST("/almacenarArchivo"), handler::almacenarArchivo)
			   .andRoute(GET("/recuperarArchivo"), handler::recuperarArchivo);
	}
}
