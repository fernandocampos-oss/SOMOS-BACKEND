package pe.gob.essalud.apps.model.miessalud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Entity
@Table(name = "token_activacion")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenActivacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token_activacion")
    private long idTokenActivacion;
    @Column(name = "token", nullable = false)
    private String token;
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;
    @Column(name = "correo", nullable = false)
    private String correo;
    @Column(name = "es_confirmado")
    private Boolean esConfirmado;
    @Column(name = "id_usuario")
    private long idUsuario;

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

}
