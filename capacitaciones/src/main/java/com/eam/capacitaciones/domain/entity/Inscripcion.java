package com.eam.capacitaciones.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Inscripcion - Representa la inscripción de un usuario a un curso
 */
@Entity
@Table(name = "Inscripcion",
       indexes = {
           @Index(name = "idx_usuario", columnList = "usuarioId"),
           @Index(name = "idx_curso", columnList = "cursoId"),
           @Index(name = "idx_estado", columnList = "estado")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_usuario_curso", columnNames = {"usuarioId", "cursoId"})
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInscripcion;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long cursoId;

    @DecimalMin(value = "0.00", message = "El progreso no puede ser negativo")
    @DecimalMax(value = "100.00", message = "El progreso no puede exceder 100")
    @Builder.Default
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal progreso = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate fechaInscripcion;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEnum estado = EstadoEnum.INSCRITO;

    @Column
    private LocalDate fechaCompletado;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cursoId", insertable = false, updatable = false)
    private Curso curso;

    public enum EstadoEnum {
        INSCRITO("Inscrito"),
        EN_PROGRESO("En Progreso"),
        COMPLETADO("Completado"),
        ABANDONADO("Abandonado");

        private final String displayName;

        EstadoEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public void actualizarProgreso(BigDecimal nuevoProgreso) {
        this.progreso = nuevoProgreso;

        if (nuevoProgreso.compareTo(BigDecimal.ZERO) > 0 && 
            nuevoProgreso.compareTo(new BigDecimal("100")) < 0) {
            this.estado = EstadoEnum.EN_PROGRESO;
        } else if (nuevoProgreso.compareTo(new BigDecimal("100")) >= 0) {
            this.estado = EstadoEnum.COMPLETADO;
            this.fechaCompletado = LocalDate.now();
        }
    }

    public boolean estaCompletado() {
        return this.estado == EstadoEnum.COMPLETADO;
    }

    public boolean estaEnProgreso() {
        return this.estado == EstadoEnum.EN_PROGRESO;
    }

    public void marcarComoAbandonado() {
        this.estado = EstadoEnum.ABANDONADO;
    }

    public double getProgresoDouble() {
        return this.progreso.doubleValue();
    }

    @PrePersist
    protected void onCreate() {
        if (fechaInscripcion == null) {
            fechaInscripcion = LocalDate.now();
        }
        if (progreso == null) {
            progreso = BigDecimal.ZERO;
        }
        if (estado == null) {
            estado = EstadoEnum.INSCRITO;
        }
    }
}
