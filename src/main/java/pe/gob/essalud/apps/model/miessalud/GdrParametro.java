package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "gdr_parametro")
public class GdrParametro {

    @Id
    @Column(name = "id_parametro")
    private Integer idParametro;

    @Column(name = "fecha_limite_planificacion")
    private LocalDateTime fechaLimitePlanificacion;

    @Column(name = "fecha_limite_seguimiento")
    private LocalDateTime fechaLimiteSeguimiento;

    @Column(name = "fecha_limite_evaluacion")
    private LocalDateTime fechaLimiteEvaluacion;

}