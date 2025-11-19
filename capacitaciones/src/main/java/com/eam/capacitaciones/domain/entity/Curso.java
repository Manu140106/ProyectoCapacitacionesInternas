package com.eam.capacitaciones.domain.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entidad Curso - Representa un curso de capacitación
 */
@Entity
@Table(name = "Curso",
       indexes = {
           @Index(name = "idx_titulo", columnList = "titulo"),
           @Index(name = "idx_nivel", columnList = "nivel"),
           @Index(name = "idx_instructor", columnList = "instructorId")
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCurso;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 150, message = "El título debe tener entre 5 y 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Size(max = 50, message = "La duración estimada no puede exceder 50 caracteres")
    @Column(length = 50)
    private String duracionEstim;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private NivelEnum nivel;

    @Column(nullable = false)
    private Long instructorId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(length = 255)
    private String imagenUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructorId", insertable = false, updatable = false)
    private Usuario instructor;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    @Builder.Default
    private List<Modulo> modulos = new ArrayList<>();

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Inscripcion> inscripciones = new HashSet<>();

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Certificado> certificados = new HashSet<>();

    public enum NivelEnum {
        BASICO("Básico"),
        INTERMEDIO("Intermedio"),
        AVANZADO("Avanzado");

        private final String displayName;

        NivelEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public void addModulo(Modulo modulo) {
        modulos.add(modulo);
        modulo.setCurso(this);
    }

    public void removeModulo(Modulo modulo) {
        modulos.remove(modulo);
        modulo.setCurso(null);
    }

    public int getTotalModulos() {
        return modulos.size();
    }

    public int getTotalInscritos() {
        return inscripciones.size();
    }

    public boolean puedePublicarse() {
        return modulos.size() > 0 && activo;
    }

    @PrePersist
    protected void onCreate() {
        if (activo == null) {
            activo = true;
        }
        if (nivel == null) {
            nivel = NivelEnum.BASICO;
        }
    }
}
