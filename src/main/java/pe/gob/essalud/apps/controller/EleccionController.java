package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.eleccion.request.VotoRequestDto;
import pe.gob.essalud.apps.dto.eleccion.response.EleccionResponseDto;
import pe.gob.essalud.apps.dto.onomastico.response.OnomasticoResponseDto;
import pe.gob.essalud.apps.service.EleccionService;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping(EleccionController.ELECCION)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class EleccionController {

    static final String ELECCION = "elecciones";
    private final EleccionService eleccionService;

    @GetMapping("/eleccion-activa")
    public EleccionResponseDto obtenerEleccionActiva() {
        return eleccionService.buscarEleccionActiva();
    }

    @PostMapping("/registrar-voto")
    public void registrarVoto(@RequestBody VotoRequestDto votoRequestDto) {
        eleccionService.guardarVoto(votoRequestDto);
    }
    @GetMapping("/horario-voto")
    public boolean getDiaVotacion() {
        return eleccionService.getDiaVotacion();
    }

}
