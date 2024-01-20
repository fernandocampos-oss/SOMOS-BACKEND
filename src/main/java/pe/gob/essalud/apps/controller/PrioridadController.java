package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.EmailNotificacionRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.MainDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.ExcelDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;
import pe.gob.essalud.apps.service.PrioridadService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(PrioridadController.PRIORIDAD)
@RequiredArgsConstructor
public class PrioridadController {

    static final String PRIORIDAD = "prioridades";
    private final PrioridadService prioridadService;

    @GetMapping("/listar-principal")
    public ResponseEntity<List<MainDto>> listGestionarIndicadoresPrincipalJefe() {
        List<MainDto> lista = prioridadService.listGestionarIndicadoresPrincipalJefe();
        return new ResponseEntity<List<MainDto>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/actividades")
    public ResponseEntity<List<Actividad>> getAllActividades() {
        List<Actividad> lista = prioridadService.getAllActividades();
        return new ResponseEntity<List<Actividad>>(lista, HttpStatus.OK);
    }

    @GetMapping("/excel/directivo")
    public ResponseEntity<List<ExcelDto>> generarExcelDirectivo() {
        List<ExcelDto> lista = prioridadService.generarExcelDirectivo();
        return new ResponseEntity<List<ExcelDto>>(lista, HttpStatus.OK);
    }

    @PostMapping("/correo-notificar")
    public void sendCorreoNotificacion(@Valid @RequestBody EmailNotificacionRequestDto requestDto) {
        prioridadService.sendCorreoNotificacion(requestDto);
    }

}
