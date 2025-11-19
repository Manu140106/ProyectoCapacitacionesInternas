package com.eam.capacitaciones.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Respuesta",
       indexes = {
           @Index(name = "idx_evaluacion", columnList = "evaluacionId"),
           @Index(name = "idx_usuario", columnList = "usuarioId"),
           @Index(name = "idx_fecha", columnList = "fecha")
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRespuesta;

    @Column(nullable = false)
    private Long evaluacionId;

    @Column(nullable = false)
    private Long usuarioId;

    @DecimalMin(value = "0.00", message = "La puntuación no puede ser negativa")
    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal puntuacion = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    @Builder.Default
    private Integer intentoNumero = 1;

    @Column(columnDefinition = "TEXT")
    private String respuestaTexto;

    @Column(nullable = false)
    @Builder.Default
    private Boolean calificada = false;

    @Column(columnDefinition = "TEXT")
    private String comentarioInstructor;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacionId", insertable = false, updatable = false)
    private Evaluacion evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuario;

    // Métodos de utilidad
    public void calificar(BigDecimal puntaje, String comentario) {
        this.puntuacion = puntaje;
        this.comentarioInstructor = comentario;
        this.calificada = true;
    }

    public boolean aprobada(Integer puntajeMinimo) {
        return this.puntuacion.compareTo(new BigDecimal(puntajeMinimo)) >= 0;
    }

    @PrePersist
    protected void onCreate() {
        if (fecha == null) fecha = LocalDate.now();
        if (puntuacion == null) puntuacion = BigDecimal.ZERO;
        if (calificada == null) calificada = false;
        if (intentoNumero == null) intentoNumero = 1;
    }
}
