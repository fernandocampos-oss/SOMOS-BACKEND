package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.PrioridadDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.MainDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.service.PrioridadService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(PrioridadController.PRIORIDAD)
@RequiredArgsConstructor
public class PrioridadController {

    static final String PRIORIDAD = "prioridades";
    private final PrioridadService prioridadService;

    @GetMapping("/listar-principal")
    public ResponseEntity<List<MainDto>> getPrioridadPorTrabajadorEnGestionJefe() {
        List<MainDto> lista = prioridadService.getPrioridadPorTrabajadorEnGestionJefe();
        return new ResponseEntity<List<MainDto>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/indicador")
    public ResponseEntity<List<Indicador>> getAllIndicadorOrganizar() {
        List<Indicador> lista = prioridadService.getAllIndicadorOrganizar();
        return new ResponseEntity<List<Indicador>>(lista, HttpStatus.OK);
    }

    //eliminar si no tiene integracion
//    @GetMapping("/asignar/actividad/{idActividad}/prioridad/{idPrioridad}")
//    public int actualizarPrioridad(@PathVariable Integer idActividad, @PathVariable Integer idPrioridad) {
//        return prioridadService.actualizarPrioridad(idActividad, idPrioridad);
//    }

    @PostMapping("/actualizar/indicadores")
    public void actualizarPrioridadEnListaIndicadores(@Valid @RequestBody PrioridadDto prioridadDto) {
         prioridadService.actualizarPrioridadEnListaIndicadores(prioridadDto);
    }

    @GetMapping("/asignar/peso/{peso}/prioridad/{idPrioridad}")
    public int asignarPesoPrioridad(@PathVariable int peso, @PathVariable int idPrioridad) {
        return prioridadService.asignarPesoPrioridad(peso, idPrioridad);
    }

    @GetMapping("/listar/actividades")
    public ResponseEntity<List<Actividad>> getAllActividades() {
        List<Actividad> lista = prioridadService.getAllActividades();
        return new ResponseEntity<List<Actividad>>(lista, HttpStatus.OK);
    }







////    @GetMapping("/aprobar")
////    public int aprobarIndicador(@RequestParam("estado") Number estado, @RequestParam("idIndicadorUsuario") Number idIndicadorUsuario) {
////        return indicadorUsuarioService.aprobarIndicador(estado, idIndicadorUsuario);
////    }
//
////    @GetMapping("/rechazar")
////    public int rechazarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("motivo") String motivo, @RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
////        return requerimientoUsuarioService.rechazarRequerimiento(estado, motivo, idRequerimientoUsuario);
////    }
//
////    @GetMapping("/listar/indicadores/{idUsuario}/trabajador")
////    public ResponseEntity<List<IndicadorUsuario>> getlistIndicadoresByIdUsuario(@PathVariable("idUsuario") Number idUsuario) {
////        List<IndicadorUsuario> lista = indicadorUsuarioService.getlistIndicadoresByIdUsuario(idUsuario);
////        return new ResponseEntity<List<IndicadorUsuario>>(lista, HttpStatus.OK);
////    }
//
//    @GetMapping("/finalizar/requerimiento")
//    public int finalizarTareaAdministrador(@RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
//        return indicadorUsuarioService.finalizarTareaAdministrador(idRequerimientoUsuario);
//    }
//
//    @GetMapping("/reporte/excel/anio/{anio}")
//    public ResponseEntity<List<IndicadorUsuario>> getAllRequerimientoUsuarioPorAnio(@PathVariable("anio") Number anio) {
//        List<IndicadorUsuario> lista = indicadorUsuarioService.getAllRequerimientoUsuarioPorAnio(anio);
//        return new ResponseEntity<List<IndicadorUsuario>>(lista, HttpStatus.OK);
//    }

}
