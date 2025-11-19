package com.eam.capacitaciones.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Modulo - Representa los módulos que componen un curso
 */
@Entity
@Table(name = "Modulo",
       indexes = {
           @Index(name = "idx_curso", columnList = "cursoId"),
           @Index(name = "idx_orden", columnList = "orden")
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idModulo;

    @Column(nullable = false)
    private Long cursoId;

    @NotBlank(message = "El título del módulo es obligatorio")
    @Size(min = 3, max = 150, message = "El título debe tener entre 3 y 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEnum tipo;

    @Min(value = 1, message = "El orden debe ser mayor a 0")
    @Column(nullable = false)
    @Builder.Default
    private Integer orden = 1;

    @Column(columnDefinition = "TEXT")
    private String contenido; 

    @Column
    private Integer duracionMinutos;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cursoId", insertable = false, updatable = false)
    private Curso curso;

    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Evaluacion> evaluaciones = new HashSet<>();

    public enum TipoEnum {
        VIDEO("Video"),
        TEXTO("Lectura/Texto"),
        QUIZ("Quiz/Evaluación"),
        PRACTICA("Práctica");

        private final String displayName;

        TipoEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public void addEvaluacion(Evaluacion evaluacion) {
        evaluaciones.add(evaluacion);
        evaluacion.setModulo(this);
    }

    public void removeEvaluacion(Evaluacion evaluacion) {
        evaluaciones.remove(evaluacion);
        evaluacion.setModulo(null);
    }

    public boolean esEvaluacion() {
        return this.tipo == TipoEnum.QUIZ;
    }

    public boolean tieneEvaluaciones() {
        return !evaluaciones.isEmpty();
    }

    @PrePersist
    protected void onCreate() {
        if (orden == null) {
            orden = 1;
        }
    }
}
