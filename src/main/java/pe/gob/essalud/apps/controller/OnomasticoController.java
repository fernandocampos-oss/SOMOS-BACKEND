package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.essalud.apps.base.BaseController;
import pe.gob.essalud.apps.dto.onomastico.response.IOnomasticoResponseDto;
import pe.gob.essalud.apps.model.miessalud.Onomastico;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.service.OnomasticoService;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    @Value("${email.hour}")
    private int hora;
    @Value("${email.minute}")
    private int minuto;
    @Value("${email.enabled}")
    private String estado;

    @GetMapping
    public List<Onomastico> findAllOnomasticos() {
        return onomasticoService.findAllOnomasticos();
    }

    @GetMapping("mes/{mes}")
    public List<Onomastico> findAllOnomasticosByMes(@PathVariable String mes) {
        return onomasticoService.findAllOnomasticosByMes(mes);
    }

//    @GetMapping("mes/{mes}/dia/{dia}")
//    public List<Onomastico> findAllOnomasticosByMesAndDia(@PathVariable String mes, @PathVariable String dia) {
//        return onomasticoService.findAllOnomasticosByMesAndDia(mes, dia);
//    }
    @GetMapping("mes/{mes}/dia/{dia}")
    public List<IOnomasticoResponseDto> obtenerOnomasticosInterfazPorDiaAndEstado(@PathVariable String mes, @PathVariable String dia) {
        return onomasticoService.obtenerOnomasticosInterfazPorDiaAndEstado(mes, dia);
    }

    @GetMapping("/find/usuario/num-doc/{numeroDocumento}")
    public Usuario findUsuarioByNumDocAndEstado(@PathVariable String numeroDocumento) {
        return onomasticoService.findUsuarioByNumDocAndEstado(numeroDocumento);
    }

    @PostConstruct
    public void sendEmailSaludoOnomasticoMasivo() {
        LocalDateTime fechaInicioLocal = LocalDateTime.now(ZoneId.of("America/Lima"));
        log.info("Inicio programado envio de correos masivos por onomastico [{}-{}]", LocalDateTime.now(), fechaInicioLocal);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            LocalDate fechaActual = LocalDate.now();
            String mesFormato = String.format("%02d", fechaActual.getMonthValue());
            String diaFormato = String.format("%02d", fechaActual.getDayOfMonth());
            if (estado.equals("true")) {
                log.info("Envio de correo onomastico [Activo] [{}-{}-{}]", hora, minuto, estado);
                List<IOnomasticoResponseDto> listUser=  onomasticoService.obtenerOnomasticosCorreoPorDiaAndEstado(mesFormato, diaFormato);
                log.info("Cant. envios: [{}]", listUser.size());
            }
            if (estado.equals("false")) {
                log.info("Envio de correo onomastico [Inactivo] [{}-{}-{}]", hora, minuto, estado);
            }
        };
		long initialDelay = calculateInitialDelay(hora, minuto); //formato 24h
        long period = TimeUnit.DAYS.toMillis(1);
        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }
    private static long calculateInitialDelay(int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }
        return Duration.between(now, nextRun).toMillis();
    }

}
