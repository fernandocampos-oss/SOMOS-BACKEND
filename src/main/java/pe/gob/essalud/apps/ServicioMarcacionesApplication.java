package pe.gob.essalud.apps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients
@SpringBootApplication
public class ServicioMarcacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioMarcacionesApplication.class, args);
	}

}
