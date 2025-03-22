package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.common.constants.gestionrendimiento.EstadoEvidenciaConstant;
import pe.gob.essalud.apps.common.util.DateUtil;
import pe.gob.essalud.apps.common.util.ExcelUtil;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.*;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.GdrParametroRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.IndicadorService;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndicadorServiceImpl implements IndicadorService {

    private final IndicadorRepository indicadorRepository;
    private final AuthService authService;
    private final TipoValorMetaRepository tipoValorMetaRepository;
    private final PrioridadRepository prioridadRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final EquipoRepository equipoRepository;
    private final GdrParametroRepository gdrParametroRepository;

    @Override
    @Transactional
    public void registrarIndicador(IndicadorRequestDto requestDto) {
        Prioridad prioridad = new Prioridad();
        prioridad.setAnio(DateUtil.getYearCurrent());
        prioridad.setActividad(requestDto.getActividad());
        System.out.println("Flack: " + requestDto.getIndicador().getFlDesPrioridad());
        if(requestDto.getIndicador().getFlDesPrioridad().equalsIgnoreCase("1")) {
        	System.out.println("Reemplaza valor.");
        	prioridad.setDescripcion(requestDto.getIndicador().getDesPrioridad());
        }
        Prioridad prioridadGuardado = prioridadRepository.save(prioridad);

        Indicador model = requestDto.getIndicador();
        model.setAnio(DateUtil.getYearCurrent());
        model.setEstado(true);
        model.setUsuarioCreacion(authService.getIdUserSession());
        model.setVotante(requestDto.getVotante());
        model.setPrioridad(prioridadGuardado);
        model.setCodRed(authService.getCodRedSession());
        model.setCodUnidad(authService.getCodUnidadSession());
        
        Indicador indicadorGuardado = indicadorRepository.save(model);

        if (!requestDto.getListEvidencia().isEmpty()) {
            for (Evidencia i : requestDto.getListEvidencia()) {
                i.setIndicador(indicadorGuardado);
                i.setUsuarioCreacion(authService.getIdUserSession());

                EstadoEvidencia estadoEvidencia = new EstadoEvidencia();
                estadoEvidencia.setIdEstadoEvidencia(EstadoEvidenciaConstant.REGISTRADO);
                i.setEstadoEvidencia(estadoEvidencia);

                i.setEstado(true);
                evidenciaRepository.save(i);
            }
        }
    }

    @Override
    public List<PendienteDto> listPendientesTrabajadorByUser() {

        Votante votante = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());

        List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), votante.getIdVotante());
        String aux = "";
        List<PendienteDto> listPrioridadDto = new ArrayList<>();
        for (Prioridad p : prioridades) {
            PendienteDto modelPrioridadDto = new PendienteDto();
            modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
            if(p.getDescripcion() == null) {
            	modelPrioridadDto.setPrioridadNombre(p.getActividad().getDescripcion());
            }else {
            	modelPrioridadDto.setPrioridadNombre(p.getDescripcion());
            }

            List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(votante.getIdVotante(), p.getIdPrioridad());

            List<PendienteIndicadorDto> listIndicadorDto = new ArrayList<>();
            for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                PendienteIndicadorDto modelIndicadorDto = new PendienteIndicadorDto();
                modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                modelIndicadorDto.setNombreIndicador(i.getDescripcion());
                modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                modelIndicadorDto.setValorMeta(i.getValorMeta());
                modelIndicadorDto.setPeso(i.getPeso());

                List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());

                List<PendienteEvidenciaDto> listEvidenciaDto = new ArrayList<>();
                for (Evidencia t : listEvidencia) {
                    PendienteEvidenciaDto modelEvidenciaDto = new PendienteEvidenciaDto();
                    modelEvidenciaDto.setIdEvidencia(t.getIdEvidencia());
                    modelEvidenciaDto.setDescripcion(t.getDescripcion());
                    modelEvidenciaDto.setPlazo(t.getPlazo());

                    modelEvidenciaDto.setFechaCreacion(t.getFechaCreacion());
                    modelEvidenciaDto.setSustentoDescripcion(t.getSustentoDescripcion());
                    modelEvidenciaDto.setSustentoFechaRegistro(t.getSustentoFechaRegistro());
                    modelEvidenciaDto.setSustentoExtensionFile(t.getSustentoExtensionFile());

                    listEvidenciaDto.add(modelEvidenciaDto);
                }
                modelIndicadorDto.setListEvidencia(listEvidenciaDto);
                listIndicadorDto.add(modelIndicadorDto);
            }
            modelPrioridadDto.setListIndicador(listIndicadorDto);
            listPrioridadDto.add(modelPrioridadDto);
        }
        return listPrioridadDto;
    }

    @Override
    public List<PendienteDto> listPendientesTrabajadorByVotanteAdmin(int idVotante) {
        List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), idVotante);

        List<PendienteDto> listPrioridadDto = new ArrayList<>();
        for (Prioridad p : prioridades) {
            PendienteDto modelPrioridadDto = new PendienteDto();
            modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
            modelPrioridadDto.setPrioridadNombre(p.getDescripcion());
            modelPrioridadDto.setIdActividad(p.getActividad().getIdActividad());
            modelPrioridadDto.setFechaAsignacionPrioridad(p.getFechaAsignacion());
            int porcentajeTotal = 0;

            List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(idVotante, p.getIdPrioridad());

            List<PendienteIndicadorDto> listIndicadorDto = new ArrayList<>();
            for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                PendienteIndicadorDto modelIndicadorDto = new PendienteIndicadorDto();
                modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                modelIndicadorDto.setNombreIndicador(i.getDescripcion());
                modelIndicadorDto.setIdTipoValorMeta(i.getTipoValorMeta().getIdTipoValorMeta());
                modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                modelIndicadorDto.setValorMeta(i.getValorMeta());
                modelIndicadorDto.setPeso(i.getPeso());
                int numero = 0;
                numero = i.getPeso();
                porcentajeTotal += numero;

                List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());

                List<PendienteEvidenciaDto> listEvidenciaDto = new ArrayList<>();
                for (Evidencia t : listEvidencia) {
                    log.info("[{}]", t.getDescripcion());
                    PendienteEvidenciaDto modelEvidenciaDto = new PendienteEvidenciaDto();
                    modelEvidenciaDto.setIdEvidencia(t.getIdEvidencia());
                    modelEvidenciaDto.setDescripcion(t.getDescripcion());
                    modelEvidenciaDto.setPlazo(t.getPlazo());

                    modelEvidenciaDto.setFechaCreacion(t.getFechaCreacion());
                    modelEvidenciaDto.setSustentoDescripcion(t.getSustentoDescripcion());
                    modelEvidenciaDto.setSustentoFechaRegistro(t.getSustentoFechaRegistro());
                    modelEvidenciaDto.setSustentoExtensionFile(t.getSustentoExtensionFile());

                    listEvidenciaDto.add(modelEvidenciaDto);
                }
                modelIndicadorDto.setListEvidencia(listEvidenciaDto);
                modelPrioridadDto.setPeso(porcentajeTotal);
                listIndicadorDto.add(modelIndicadorDto);
            }
            modelPrioridadDto.setListIndicador(listIndicadorDto);
            listPrioridadDto.add(modelPrioridadDto);
        }
        return listPrioridadDto;
    }

    @Override
    public List<TipoValorMeta> getAllTipoValorMeta() {
        return tipoValorMetaRepository.findAllByEstado(true);
    }

    @Override
    public ExcelTrabajadorDto generarExcelTrabajador() {
        ExcelTrabajadorDto mainDto = new ExcelTrabajadorDto();

        Map<Integer, String> calificacionMap = new HashMap<>();
        calificacionMap.put(0, "NO presenta evidencia de logro final");
        calificacionMap.put(1, "En proceso de logro");
        calificacionMap.put(2, "SI presenta evidencia final");
        calificacionMap.put(3, "Logrado");
        calificacionMap.put(4, "No presenta evidencia");

        EvaluadorResponseDto trabajadorUsuario = prioridadRepository.findUsuarioById(authService.getIdUserSession());
        Votante votanteTrabajador = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());
        mainDto.setEvaluadoNombreCompleto(votanteTrabajador.getApellidos() + " " + votanteTrabajador.getNombres());
        mainDto.setEvaluadoPuesto(trabajadorUsuario.getPuesto());
        mainDto.setEvaluadoNumeroDocumento(trabajadorUsuario.getNumeroDocumento());
        UnidadOrganizativa unidadtrabajador = prioridadRepository.getUnidadByCod(trabajadorUsuario.getUnidad());
        mainDto.setEvaluadoCodUnidad(unidadtrabajador.getDescripcion());
        if (votanteTrabajador.getIdSegmento() == 1) {
            mainDto.setEvaluadoSegmento("DIRECTIVO");
        }
        if (votanteTrabajador.getIdSegmento() == 3) {
            mainDto.setEvaluadoSegmento("EJECUTOR");
        }
        Equipo JefeEquipo = equipoRepository.getJefeByIdIntegrante(votanteTrabajador.getIdVotante());
        EvaluadorResponseDto jefe = prioridadRepository.findUsuarioById(JefeEquipo.getJefe().getIdUsuario());
        UnidadOrganizativa unidadJefe = prioridadRepository.getUnidadByCod(jefe.getUnidad());
        mainDto.setEvaluadorCodUnidad(unidadJefe.getDescripcion());
        mainDto.setEvaluadorNombreCompleto(JefeEquipo.getJefe().getApellidos() + " " + JefeEquipo.getJefe().getNombres());
        mainDto.setEvaluadorPuesto(jefe.getPuesto());
        if (JefeEquipo.getJefe().getIdSegmento() == 1) {
            mainDto.setEvaluadorSegmento("DIRECTIVO");
        }
        mainDto.setEvaluadorNumeroDocumento(jefe.getNumeroDocumento());

        List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), votanteTrabajador.getIdVotante());

        List<PendienteDto> listPrioridadDto = new ArrayList<>();
        for (Prioridad p : prioridades) {
            PendienteDto modelPrioridadDto = new PendienteDto();
            modelPrioridadDto.setFechaAsignacionPrioridad(p.getFechaAsignacion());
            modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
            modelPrioridadDto.setPrioridadNombre(p.getDescripcion());

            List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(votanteTrabajador.getIdVotante(), p.getIdPrioridad());

            List<PendienteIndicadorDto> listIndicadorDto = new ArrayList<>();
            for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                PendienteIndicadorDto modelIndicadorDto = new PendienteIndicadorDto();
                modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                modelIndicadorDto.setNombreIndicador(i.getDescripcion());
                modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                modelIndicadorDto.setValorMeta(i.getValorMeta());
                modelIndicadorDto.setPeso(i.getPeso());

                List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());

                List<PendienteEvidenciaDto> listEvidenciaDto = new ArrayList<>();
                for (Evidencia t : listEvidencia) {
                    PendienteEvidenciaDto modelEvidenciaDto = new PendienteEvidenciaDto();
                    modelEvidenciaDto.setIdEvidencia(t.getIdEvidencia());
                    modelEvidenciaDto.setDescripcion(t.getDescripcion());
                    modelEvidenciaDto.setPlazo(t.getPlazo());
                    modelEvidenciaDto.setComentario(t.getComentario());
                    modelEvidenciaDto.setFechaCreacion(t.getFechaCreacion());
                    modelEvidenciaDto.setSustentoDescripcion(t.getSustentoDescripcion());
                    modelEvidenciaDto.setSustentoFechaRegistro(t.getSustentoFechaRegistro());
                    modelEvidenciaDto.setSustentoExtensionFile(t.getSustentoExtensionFile());
                    modelEvidenciaDto.setCalificacionDescripcion(calificacionMap.get(t.getCalificacion()));
                    listEvidenciaDto.add(modelEvidenciaDto);
                }
                modelIndicadorDto.setListEvidencia(listEvidenciaDto);
                listIndicadorDto.add(modelIndicadorDto);
            }
            modelPrioridadDto.setListIndicador(listIndicadorDto);
            listPrioridadDto.add(modelPrioridadDto);
        }
        mainDto.setListPrioridad(listPrioridadDto);
        return mainDto;
    }

    @Override
    public ExcelTrabajadorDto generarExcelTrabajadorByVotanteAdmin(int idVotante) {
        ExcelTrabajadorDto mainDto = new ExcelTrabajadorDto();

        Map<Integer, String> calificacionMap = new HashMap<>();
        calificacionMap.put(0, "NO presenta evidencia de logro final");
        calificacionMap.put(1, "En proceso de logro");
        calificacionMap.put(2, "SI presenta evidencia final");
        calificacionMap.put(3, "Logrado");
        calificacionMap.put(4, "No presenta evidencia");

        Votante votanteTrabajador = equipoRepository.getVotanteByIdVotante(idVotante);
        EvaluadorResponseDto trabajadorUsuario = prioridadRepository.findUsuarioById(votanteTrabajador.getIdUsuario());
        mainDto.setEvaluadoNombreCompleto(votanteTrabajador.getApellidos() + " " + votanteTrabajador.getNombres());
        mainDto.setEvaluadoPuesto(trabajadorUsuario.getPuesto());
        mainDto.setEvaluadoNumeroDocumento(trabajadorUsuario.getNumeroDocumento());
        UnidadOrganizativa unidadtrabajador = prioridadRepository.getUnidadByCod(trabajadorUsuario.getUnidad());
        mainDto.setEvaluadoCodUnidad(unidadtrabajador.getDescripcion());
        if (votanteTrabajador.getIdSegmento() == 1) {
            mainDto.setEvaluadoSegmento("DIRECTIVO");
        }
        if (votanteTrabajador.getIdSegmento() == 3) {
            mainDto.setEvaluadoSegmento("EJECUTOR");
        }
        Equipo JefeEquipo = equipoRepository.getJefeByIdIntegrante(votanteTrabajador.getIdVotante());
        EvaluadorResponseDto jefe = prioridadRepository.findUsuarioById(JefeEquipo.getJefe().getIdUsuario());
        UnidadOrganizativa unidadJefe = prioridadRepository.getUnidadByCod(jefe.getUnidad());
        mainDto.setEvaluadorCodUnidad(unidadJefe.getDescripcion());
        mainDto.setEvaluadorNombreCompleto(JefeEquipo.getJefe().getApellidos() + " " + JefeEquipo.getJefe().getNombres());
        mainDto.setEvaluadorPuesto(jefe.getPuesto());
        if (JefeEquipo.getJefe().getIdSegmento() == 1) {
            mainDto.setEvaluadorSegmento("DIRECTIVO");
        }
        mainDto.setEvaluadorNumeroDocumento(jefe.getNumeroDocumento());

        List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), votanteTrabajador.getIdVotante());

        List<PendienteDto> listPrioridadDto = new ArrayList<>();
        for (Prioridad p : prioridades) {
            PendienteDto modelPrioridadDto = new PendienteDto();
            modelPrioridadDto.setFechaAsignacionPrioridad(p.getFechaAsignacion());
            modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
            modelPrioridadDto.setPrioridadNombre(p.getDescripcion());

            List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(votanteTrabajador.getIdVotante(), p.getIdPrioridad());

            List<PendienteIndicadorDto> listIndicadorDto = new ArrayList<>();
            for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                PendienteIndicadorDto modelIndicadorDto = new PendienteIndicadorDto();
                modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                modelIndicadorDto.setNombreIndicador(i.getDescripcion());
                modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                modelIndicadorDto.setValorMeta(i.getValorMeta());
                modelIndicadorDto.setPeso(i.getPeso());

                List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());

                List<PendienteEvidenciaDto> listEvidenciaDto = new ArrayList<>();
                for (Evidencia t : listEvidencia) {
                    PendienteEvidenciaDto modelEvidenciaDto = new PendienteEvidenciaDto();
                    modelEvidenciaDto.setIdEvidencia(t.getIdEvidencia());
                    modelEvidenciaDto.setDescripcion(t.getDescripcion());
                    modelEvidenciaDto.setPlazo(t.getPlazo());
                    modelEvidenciaDto.setComentario(t.getComentario());
                    modelEvidenciaDto.setFechaCreacion(t.getFechaCreacion());
                    modelEvidenciaDto.setSustentoDescripcion(t.getSustentoDescripcion());
                    modelEvidenciaDto.setSustentoFechaRegistro(t.getSustentoFechaRegistro());
                    modelEvidenciaDto.setSustentoExtensionFile(t.getSustentoExtensionFile());
                    modelEvidenciaDto.setCalificacionDescripcion(calificacionMap.get(t.getCalificacion()));
                    listEvidenciaDto.add(modelEvidenciaDto);
                }
                modelIndicadorDto.setListEvidencia(listEvidenciaDto);
                listIndicadorDto.add(modelIndicadorDto);
            }
            modelPrioridadDto.setListIndicador(listIndicadorDto);
            listPrioridadDto.add(modelPrioridadDto);
        }
        mainDto.setListPrioridad(listPrioridadDto);
        return mainDto;
    }

    @Override
    public List<ExcelTrabajadorDto> generarExcelDirectivo() {
        List<ExcelTrabajadorDto> mainDtoList = new ArrayList<>();

        List<Equipo> trabajadoresPorJefe = equipoRepository.getListTrabajadoresByIdUsuarioJefe(authService.getIdUserSession());
        for (Equipo e : trabajadoresPorJefe) {
            ExcelTrabajadorDto excelTrabajadorDto = generarExcelTrabajadorByVotanteAdmin(e.getIntegrante().getIdVotante());
            mainDtoList.add(excelTrabajadorDto);
        }

        return mainDtoList;
    }

    @Override
    public ByteArrayResource generateFormatoExcel(ExcelTrabajadorDto excelTrabajadorDto) throws IOException {
        FileInputStream fileInputStream = new FileInputStream("src/main/resources/templates/formato-gdr.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFCellStyle centeredStyle = ExcelUtil.createCenteredStyle(workbook);

        //*********************HEADER**********
        // ENTIDAD
        ExcelUtil.updateCellValue(sheet.getRow(3), 3, "SEGURO SOCIAL DE SALUD - ESSALUD", false);
        //EVALUADO
        ExcelUtil.updateCellValue(sheet.getRow(7), 3, excelTrabajadorDto.getEvaluadoNumeroDocumento(), false);
        ExcelUtil.updateCellValue(sheet.getRow(8), 3, excelTrabajadorDto.getEvaluadoNombreCompleto(), false);
        ExcelUtil.updateCellValue(sheet.getRow(9), 3, excelTrabajadorDto.getEvaluadoPuesto(), false);
        ExcelUtil.updateCellValue(sheet.getRow(10), 3, excelTrabajadorDto.getEvaluadoSegmento(), false);
        ExcelUtil.updateCellValue(sheet.getRow(11), 3, excelTrabajadorDto.getEvaluadoCodUnidad(), false);
        //EVALUADOR
        ExcelUtil.updateCellValue(sheet.getRow(7), 10, excelTrabajadorDto.getEvaluadorNumeroDocumento(), false);
        ExcelUtil.updateCellValue(sheet.getRow(8), 10, excelTrabajadorDto.getEvaluadorNombreCompleto(), false);
        ExcelUtil.updateCellValue(sheet.getRow(9), 10, excelTrabajadorDto.getEvaluadorPuesto(), false);
        ExcelUtil.updateCellValue(sheet.getRow(10), 10, excelTrabajadorDto.getEvaluadorSegmento(), false);
        ExcelUtil.updateCellValue(sheet.getRow(11), 10, excelTrabajadorDto.getEvaluadorCodUnidad(), false);
        //*********************HEADER**********

        int startRow = 20; // Fila 21 en Excel (0-based index)

        // Calcular el número total de filas que serán usadas
        int totalRowsNeeded = excelTrabajadorDto.getListPrioridad().stream()
                .flatMap(prioridad -> prioridad.getListIndicador().stream())
                .mapToInt(indicador -> indicador.getListEvidencia().size())
                .sum();

        //*********************FOOTER**********
        ExcelUtil.moveFooterDown(sheet, startRow, totalRowsNeeded);
        //*********************FOOTER**********

        //********************LISTA**********
        String[] opcionesSentidoIndicador = {"[Seleccione]", "Ascendente", "Descendente"};
        String[] opcionesEvidenciaAvance = {"[Seleccione]", "NO presenta evidencia de logro final", "En proceso de logro",
                "SI presenta evidencia final", "Logrado", "No presenta evidencia"};

        // Crear la validación de datos (DropDown)
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraintSentidoIndicador = validationHelper.createExplicitListConstraint(opcionesSentidoIndicador);
        DataValidationConstraint constraintEvidenciaAvance = validationHelper.createExplicitListConstraint(opcionesEvidenciaAvance);
        //********************LISTA**********

        // Ahora llenamos la data sin afectar el footer
        int currentRow = startRow;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int contPrioridades = 0;
        for (PendienteDto prioridad : excelTrabajadorDto.getListPrioridad()) {
            int prioridadStartRow = currentRow;
            int prioridadRowSpan = 0;

            for (PendienteIndicadorDto indicador : prioridad.getListIndicador()) {
                int indicadorStartRow = currentRow;
                int indicadorRowSpan = indicador.getListEvidencia().size();

                for (PendienteEvidenciaDto evidencia : indicador.getListEvidencia()) {
                    Row row = sheet.createRow(currentRow++);
                    row.setHeightInPoints(60);
                    ExcelUtil.createCell(row, 7, evidencia.getDescripcion(), centeredStyle, false);
                    ExcelUtil.createCell(row, 8, evidencia.getPlazo().format(formatter), centeredStyle, false);
                    //*************LISTA*******
                    ExcelUtil.createCell(row, 9, Objects.requireNonNullElse(evidencia.getCalificacionDescripcion(), "[Seleccione]"), centeredStyle, false);
                    CellRangeAddressList addressList = new CellRangeAddressList(row.getRowNum(), row.getRowNum(), 9, 9);
                    DataValidation validation = validationHelper.createValidation(constraintEvidenciaAvance, addressList);
                    validation.setShowErrorBox(true);
                    sheet.addValidationData(validation);
                    //*************LISTA*******
                    ExcelUtil.mergeCellsInRow(sheet, row.getRowNum(), 10, 11, centeredStyle);
                    ExcelUtil.createCell(row, 10, evidencia.getComentario(), centeredStyle, false);
                }

                // Fusionar celdas del indicador
                ExcelUtil.mergeCellsInColumn(sheet, indicadorStartRow, indicadorStartRow + indicadorRowSpan - 1, 3, centeredStyle);
                ExcelUtil.mergeCellsInColumn(sheet, indicadorStartRow, indicadorStartRow + indicadorRowSpan - 1, 4, centeredStyle);
                ExcelUtil.mergeCellsInColumn(sheet, indicadorStartRow, indicadorStartRow + indicadorRowSpan - 1, 5, centeredStyle);
                ExcelUtil.mergeCellsInColumn(sheet, indicadorStartRow, indicadorStartRow + indicadorRowSpan - 1, 6, centeredStyle);
                ExcelUtil.mergeCellsInColumn(sheet, indicadorStartRow, indicadorStartRow + indicadorRowSpan - 1, 12, centeredStyle);
                ExcelUtil.mergeCellsInColumn(sheet, indicadorStartRow, indicadorStartRow + indicadorRowSpan - 1, 13, centeredStyle);
                ExcelUtil.createCell(sheet.getRow(indicadorStartRow), 3, indicador.getNombreIndicador(), centeredStyle, false);

                //*************LISTA*******
                ExcelUtil.createCell(sheet.getRow(indicadorStartRow), 4, "[Seleccione]", centeredStyle, false);
                CellRangeAddressList addressList = new CellRangeAddressList(indicadorStartRow, indicadorStartRow, 4, 4);
                DataValidation validation = validationHelper.createValidation(constraintSentidoIndicador, addressList);
                validation.setShowErrorBox(true);
                sheet.addValidationData(validation);
                //*************LISTA*******

                ExcelUtil.createCell(sheet.getRow(indicadorStartRow), 5, indicador.getValorMeta(), centeredStyle, false);
                ExcelUtil.createCell(sheet.getRow(indicadorStartRow), 6, indicador.getPeso() + "%", centeredStyle, false);
                ExcelUtil.createCell(sheet.getRow(indicadorStartRow), 12, "", centeredStyle, false);

                String formula = "IFERROR(IF($L$18=\"No\",\"No corresponde\",IF(OR(M{row}=\"\",E{row}=\"[Seleccione]\"),\"-\",IF(IF(E{row}=\"Ascendente\",(M{row}/F{row})*100*G{row},(((1-(M{row}/F{row}))+1)*100*G{row}))>(G{row}*100),(G{row}*100),IF(E{row}=\"Ascendente\",(M{row}/F{row})*100*G{row},IF(AND(E{row}=\"Descendente\",(M{row}>F{row}*2)),0,((1-(M{row}/F{row}))+1)*100*G{row}))))),\"-\")";
                formula = formula.replace("{row}", String.valueOf(indicadorStartRow + 1));
                ExcelUtil.createCell(sheet.getRow(indicadorStartRow), 13, formula, centeredStyle, true);

                prioridadRowSpan += indicadorRowSpan;
            }

            // Fusionar celdas de la prioridad
            contPrioridades++;
            ExcelUtil.mergeCellsInColumn(sheet, prioridadStartRow, prioridadStartRow + prioridadRowSpan - 1, 1, centeredStyle);
            ExcelUtil.mergeCellsInColumn(sheet, prioridadStartRow, prioridadStartRow + prioridadRowSpan - 1, 2, centeredStyle);
            ExcelUtil.createCell(sheet.getRow(prioridadStartRow), 1, contPrioridades, centeredStyle, false);
            ExcelUtil.createCell(sheet.getRow(prioridadStartRow), 2, prioridad.getPrioridadNombre(), centeredStyle, false);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

    @Override
    public Optional<Integer> sumaTotalPesoAllIndicadorByTrabajador(int idVotante) {
        log.info("[{}-{}]", DateUtil.getYearCurrent(), idVotante);
        return indicadorRepository.sumaTotalPesoAllIndicadorByTrabajador(DateUtil.getYearCurrent(), idVotante);
    }

    @Override
    public void modificarIndicador(int id, Indicador request) {
        Indicador indicador = indicadorRepository.findById(id)
                .orElseThrow(() -> new ValidationException("El indicador no se encuentra"));

        indicador.setDescripcion(request.getDescripcion());
        indicador.setTipoValorMeta(request.getTipoValorMeta());
        indicador.setValorMeta(request.getValorMeta());
        indicador.setPeso(request.getPeso());
        indicador.setUsuarioModificacion(authService.getIdUserSession());
        indicadorRepository.save(indicador);
    }

    @Override
    public void eliminarIndicador(int id) {
        Indicador indicador = indicadorRepository.findById(id)
                .orElseThrow(() -> new ValidationException("El indicador no se encuentra"));
        List<Evidencia> evidencias = evidenciaRepository.listEvidenciaByIdIndicador(indicador.getIdIndicador());
        if (!evidencias.isEmpty()) {
            throw new ValidationException("El indicador tiene evidencia registrada");
        }
        indicador.setEstado(false);
        indicadorRepository.save(indicador);
    }

}
