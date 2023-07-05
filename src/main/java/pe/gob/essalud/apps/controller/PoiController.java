package pe.gob.essalud.apps.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Poi;
import pe.gob.essalud.apps.service.PoiService;

@RestController
@RequestMapping(PoiController.POIS)
@RequiredArgsConstructor
public class PoiController {

    static final String POIS = "pois";
    private final PoiService poiService;

    @GetMapping("/listar")
    public ResponseEntity<List<Poi>> listar() {
        List<Poi> lista = poiService.listar();
        return new ResponseEntity<List<Poi>>(lista, HttpStatus.OK);
    }

}
