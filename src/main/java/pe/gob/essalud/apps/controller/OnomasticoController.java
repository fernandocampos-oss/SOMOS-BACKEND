package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.essalud.apps.base.BaseController;
import pe.gob.essalud.apps.dto.onomastico.response.OnomasticoResponseDto;
import pe.gob.essalud.apps.model.miessalud.Onomastico;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.service.OnomasticoService;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(OnomasticoController.ONOMASTICO)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
@Slf4j
public class OnomasticoController extends BaseController {

    static final String ONOMASTICO = "onomasticos";
    private final OnomasticoService onomasticoService;

    @GetMapping
    public List<Onomastico> findAllOnomasticos() {
        return onomasticoService.findAllOnomasticos();
    }

    @GetMapping("mes/{mes}")
    public List<Onomastico> findAllOnomasticosByMes(@PathVariable String mes) {
        return onomasticoService.findAllOnomasticosByMes(mes);
    }

    @GetMapping("mes/{mes}/dia/{dia}")
    public List<Onomastico> findAllOnomasticosByMesAndDia(@PathVariable String mes, @PathVariable String dia) {
        return onomasticoService.findAllOnomasticosByMesAndDia(mes, dia);
    }

    @GetMapping("/find/usuario/num-doc/{numeroDocumento}")
    public Usuario findUsuarioByNumDocAndEstado(@PathVariable String numeroDocumento) {
        return onomasticoService.findUsuarioByNumDocAndEstado(numeroDocumento);
    }

    @PostConstruct
    public void sendEmailSaludoOnomasticoMasivo() {
        System.out.println("Iniciado envio programado de correos masivos por onomastico diario a las 7AM");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            log.info("Tarea ejecutada a las [{}]", LocalDateTime.now());
            LocalDate fechaActual = LocalDate.now();
            String mesFormato = String.format("%02d", fechaActual.getMonthValue());
            String diaFormato = String.format("%02d", fechaActual.getDayOfMonth());
            log.info("Mes actual: [{}], Día actual: [{}]", mesFormato, diaFormato);

            List<OnomasticoResponseDto> listUser=  onomasticoService.obtenerOnomasticosPorDiaAndEstado(mesFormato, diaFormato);
            log.info("list: [{}]", listUser.size());
            for (OnomasticoResponseDto t : listUser) {
                log.info("enviado: [{}]", t);
            }
        };
<<<<<<< Updated upstream
//		long initialDelay = calculateInitialDelay(7, 0); // Hora: 7:00 AM formato 24h
        long initialDelay = calculateInitialDelay(15, 10);
        long period = TimeUnit.DAYS.toMillis(1); // Repeticion por cada día
=======
		long initialDelay = calculateInitialDelay(12, 0); // Hora: 7:00 AM formato 24h
        long period = TimeUnit.DAYS.toMillis(1); // Repeticion por día
>>>>>>> Stashed changes
        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    // Calculo de la demora inicial para la próxima ejecución a las 7:00AM
    private static long calculateInitialDelay(int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);
        // Si la hora actual ya pasó, se programa para el siguiente día
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }
        return Duration.between(now, nextRun).toMillis();
    }

}
