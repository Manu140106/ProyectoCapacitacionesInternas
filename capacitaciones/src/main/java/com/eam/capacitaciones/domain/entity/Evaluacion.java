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

@Entity
@Table(name = "Evaluacion",
       indexes = {@Index(name = "idx_modulo", columnList = "moduloId")})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvaluacion;

    @Column(nullable = false)
    private Long moduloId;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEnum tipo;

    @Min(value = 1, message = "El puntaje máximo debe ser mayor a 0")
    @Column(nullable = false)
    @Builder.Default
    private Integer puntajeMax = 100;

    @Column
    private Integer duracionMinutos;

    @Column
    @Builder.Default
    private Integer intentosPermitidos = 3;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moduloId", insertable = false, updatable = false)
    private Modulo modulo;

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Respuesta> respuestas = new HashSet<>();

    public enum TipoEnum {
        MCQ("Opción Múltiple"),
        ABIERTA("Respuesta Abierta");

        private final String displayName;
        TipoEnum(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public boolean esAbierta() { return this.tipo == TipoEnum.ABIERTA; }
    public boolean tieneLimiteIntentos() { return intentosPermitidos != null && intentosPermitidos > 0; }

    @PrePersist
    protected void onCreate() {
        if (puntajeMax == null) puntajeMax = 100;
        if (intentosPermitidos == null) intentosPermitidos = 3;
    }
}
