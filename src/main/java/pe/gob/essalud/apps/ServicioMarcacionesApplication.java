package pe.gob.essalud.apps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ServicioMarcacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioMarcacionesApplication.class, args);
	}

}
