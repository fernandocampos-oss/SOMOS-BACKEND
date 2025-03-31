package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.base.BaseService;
import pe.gob.essalud.apps.client.EmailServiceClient;
import pe.gob.essalud.apps.dto.emailservice.SaludoOnomasticobRequestDto;
import pe.gob.essalud.apps.dto.onomastico.response.IOnomasticoResponseDto;
import pe.gob.essalud.apps.model.miessalud.Onomastico;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.repository.miessalud.OnomasticoRepository;
import pe.gob.essalud.apps.service.OnomasticoService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnomasticoServiceImpl extends BaseService implements OnomasticoService {

    private final OnomasticoRepository onomasticoRepository;
    private final EmailServiceClient _emailServiceClient;

    @Override
    public List<Onomastico> findAllOnomasticos() {
        return onomasticoRepository.findAll();
    }

    @Override
    public List<Onomastico> findAllOnomasticosByMes(String mes) {
        return onomasticoRepository.findByMes(mes);
    }

//    @Override
//    public List<Onomastico> findAllOnomasticosByMesAndDia(String mes, String dia) {
//        return onomasticoRepository.findByMesAndDia(mes, dia);
//    }
    @Override
    public List<IOnomasticoResponseDto> obtenerOnomasticosInterfazPorDiaAndEstado(String mes, String dia) {
        List<IOnomasticoResponseDto> listOnomastico;
        listOnomastico = onomasticoRepository.obtenerOnomasticosInterfazPorDiaAndEstado(mes, dia);
        log.info("cant. cumpleaños: [{}]", listOnomastico.size());
        return listOnomastico;
    }

    @Override
    public Usuario findUsuarioByNumDocAndEstado(String numeroDocumento) {
        return onomasticoRepository.findUsuarioByNumDocAndEstado(numeroDocumento);
    }

    @Override
    public List<IOnomasticoResponseDto> obtenerOnomasticosCorreoPorDiaAndEstado(String mes, String dia) {
        List<IOnomasticoResponseDto> listOnomastico;
        listOnomastico = onomasticoRepository.obtenerOnomasticosInterfazPorDiaAndEstado(mes, dia);
        for (IOnomasticoResponseDto row : listOnomastico) {
            log.info("Nombre: [{}], Apellido: [{}], Correo: [{}]", row.getNombres(), row.getApellidos(), row.getCorreo());
            String nombreCompleto = row.getNombres() + " " + row.getApellidos();
            _sendMailSaludoOnomastico(row.getCorreo(), nombreCompleto);
        }
        return listOnomastico;
    }

    @Async
    protected void _sendMailSaludoOnomastico(String correo, String nombreCompleto) {
        log.info("correo a notificar: [{}-{}]", correo, nombreCompleto);
        SaludoOnomasticobRequestDto requestSaludoOnomastico = new SaludoOnomasticobRequestDto();
        requestSaludoOnomastico.setEmail(correo);
        requestSaludoOnomastico.setNombre(nombreCompleto);
        _emailServiceClient.saludoOnomastico(requestSaludoOnomastico);
    }

}
