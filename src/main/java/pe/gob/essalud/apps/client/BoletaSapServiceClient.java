package pe.gob.essalud.apps.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.gob.essalud.apps.common.interfaces.BasicAuthForBoletaSapUtilService;
import pe.gob.essalud.apps.dto.pago.response.PagoBoletaResponseDto;

import java.util.List;

@BasicAuthForBoletaSapUtilService
@FeignClient(name = "boletasaputilserviceclient", url = "${feign-clients.boleta-sap-service.url}")
public interface BoletaSapServiceClient {

    @GetMapping("boleta/getBoletaPago")
    List<PagoBoletaResponseDto> getBoletaPago(@RequestParam String codigoPlanilla, @RequestParam int anio, @RequestParam int mes);

    @GetMapping("boleta/getPdf")
    ResponseEntity<Resource> getPdf(@RequestParam int idBoleta);

}
