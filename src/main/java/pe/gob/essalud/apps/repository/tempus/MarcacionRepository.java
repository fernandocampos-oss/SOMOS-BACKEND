package pe.gob.essalud.apps.repository.tempus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.essalud.apps.model.tempus.Ambito;
import pe.gob.essalud.apps.model.tempus.projection.PersonalProjection;

public interface MarcacionRepository extends JpaRepository<Ambito, Integer> {
	
	@Query(nativeQuery = true, value= "select ma.CODIGO as codplanilla,\r\n"
			+ "	(pe.apellido_paterno+ ' '  + pe.apellido_materno+ ',' + pe.nombres) as nombrepersonal,\r\n"
			+ "	pe.nro_documento as dni,\r\n"
			+ "	convert(varchar(19),ma.fecha,103) as fecha,\r\n"
			+ "	convert(varchar(19),ma.horatxt,8) as hora,\r\n"
			+ "	te.descripcion as terminalmarca,\r\n"
			+ "	tl.descripcion,\r\n"
			+ "	case ma.FLG_ACTIVIDAD when '001' then 'ENTRADA' when '002' then 'SALIDA' END AS lectora,\r\n"
			+ "	lt.nombre_lugartrabajo as sede\r\n"
			+ "	from tempus.marcaciones ma \r\n"
			+ "		inner join tempus.personal pe on (ma.codigo= pe.codigo)\r\n"
			+ "		inner join tempus.terminal te on (ma.idterminal = te.idterminal)\r\n"
			+ "		inner join tempus.lugar_trabajo lt on (pe.lugar_trabajo = lt.id_lugartrabajo)\r\n"
			+ "		inner join tempus.tipo_lectora tl on (ma.IDLECTORA=tl.ID_TIPO_LECT)\r\n"
			+ "	where \r\n"
			+ "		ma.fechahora between convert(datetime,?1,103) and convert(datetime,?2,103)\r\n"
			+ "		and ma.codigo =?3")
	List<PersonalProjection> findAllMarcas(String desde, String hasta, String codigo); 
		
}
