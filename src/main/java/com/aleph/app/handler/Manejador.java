/**
 * Este es como un controlador en Spring convencional,
 * tiene diferencias notorias y utiliza más programación reactiva
 * y funcional.
 * 
 **/

package com.aleph.app.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aleph.app.entities.Hacha;
import com.aleph.app.entities.Mensaje;
import com.aleph.app.entities.Fragmentacion;

import reactor.core.publisher.Mono;

@Component
public class Manejador {

	@Value("${ruta.archivos}")
	private String pathOrigen;

	@Value("${ruta.archivos.destino}")
	private String pathDestino;

	public Mono<ServerResponse> obtenerMensaje(ServerRequest request) {
		Mensaje mensaje = new Mensaje();
		mensaje.setMensaje("Hola Mundo Reactivo 1");
		Mono<Mensaje> mensajeMono = Mono.just(mensaje);
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mensajeMono, Mensaje.class);
	}

	public Mono<ServerResponse> almacenarArchivo(ServerRequest request) {
		String clave = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MTUxOTA0NTksInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJqdGkiOiJhOGQ5ZDIwNy1mODBhLTQ0YzEtYWE3YS1iMjMxNjVkYThlYzciLCJjbGllbnRfaWQiOiJmcm9udGVuZGFwcCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.z054R8ljP1SBv1MR0MmUS3S47JkUAlm3Lm-QrJAj-FY";

		Mono<Mensaje> mensajeMono = request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
				.cast(FilePart.class).flatMap(file -> {
					file.transferTo(new File(pathOrigen + file.filename()));

					try {
						/* 128*1024*1024 */
						Hacha hacha = new Hacha(pathOrigen, pathDestino, 35 * 1024 * 1024, file.filename(), clave);
						hacha.crearCortes();
						//Fragmentacion compress = new Fragmentacion();
					} catch (IOException e) {
						Logger.getLogger(Hacha.class.getName()).log(Level.SEVERE, null, e);
						//Logger.getLogger(.class.getName()).log(Level.SEVERE, null, e);
					}
					Mensaje mensaje = new Mensaje();
					mensaje.setMensaje("El nombre del archivo recibido en el server 1 fue: " + file.filename());

					return Mono.just(mensaje);
				});

		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mensajeMono, Mensaje.class);
	}

	public Mono<ServerResponse> recuperarArchivo(ServerRequest request) {
		Optional<String> nombreArchivo = request.queryParam("nombreArchivo");

		File file = new File(pathOrigen + nombreArchivo.get());
		Resource resource = new FileSystemResource(file);

		/*
		 * Mensaje mensaje = new Mensaje();
		 * mensaje.setMensaje("el nombre del archivo recibido en el server fue: "
		 * +nombreArchivo);
		 */

		return ServerResponse.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo.get() + "\"")
				.bodyValue(resource);
	}
}

/**
 * Esta es otra forma en la que devuelvo un mensaje con el nombre del archivo
 * que se recibió en el servidor. (también funciona esta versión)
 *
 * 
 * Con el Mono.just se resolvió el error java.lang.IllegalArgumentException:
 * 'producer' type is unknown to ReactiveAdapterRegistry
 * 
 * Mensaje mensaje = new Mensaje(); mensaje.setMensaje("archivo recibido en el
 * server exitosamente"); //Este mensaje no se despliega en el cliente. return
 * request.multipartData().map(multipart ->
 * multipart.toSingleValueMap().get("file")) .cast(FilePart.class) .flatMap(file
 * -> file.transferTo(new File(path + file.filename()))) .flatMap(v ->
 * ServerResponse.ok() .contentType(MediaType.APPLICATION_JSON)
 * .body(Mono.just(mensaje), Mensaje.class));
 * 
 * Nota: Es importante tener en cuenta que para que funcione el upload de un
 * archivo, hay que seguir los ejemplos de código que se presentan aquí, ya que
 * si lo que devuelve el request no se asigna a nada o no va precedido de un
 * return, no se lleva a cabo el upload del archivo.
 */
