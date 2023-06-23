package pe.gob.essalud.apps.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.gob.essalud.apps.dto.personalsaputilservice.PersonaSAP;
import pe.gob.essalud.apps.common.interfaces.BasicAuthForPersonalSapUtilService;

@BasicAuthForPersonalSapUtilService
@FeignClient(name = "personalsaputilserviceclient", url = "${feign-clients.personal-sap-util-service.url}")
public interface PersonalSapUtilServiceClient {

    @GetMapping("personal/getByNumDocAndFecNac")
    PersonaSAP getByNumDocAndFecNac(@RequestParam String numDoc, @RequestParam String fecNac);

}
