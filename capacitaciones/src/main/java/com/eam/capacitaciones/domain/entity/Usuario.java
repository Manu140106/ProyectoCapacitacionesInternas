package com.eam.capacitaciones.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Usuario - Representa a los usuarios del sistema
 * (Administradores, Instructores y Usuarios regulares)
 */
@Entity
@Table(name = "Usuario", 
       indexes = {
           @Index(name = "idx_email", columnList = "email"),
           @Index(name = "idx_rol", columnList = "rol")
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Column(nullable = false, length = 200)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolEnum rol;

    @Size(max = 100, message = "El departamento no puede exceder 100 caracteres")
    @Column(length = 100)
    private String departamento;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Inscripcion> inscripciones = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Respuesta> respuestas = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Certificado> certificados = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Usuario_Badge",
        joinColumns = @JoinColumn(name = "usuarioId"),
        inverseJoinColumns = @JoinColumn(name = "badgeId")
    )
    @Builder.Default
    private Set<Badge> badges = new HashSet<>();

    public enum RolEnum {
        ADMIN("Administrador"),
        INSTRUCTOR("Instructor"),
        USER("Usuario");

        private final String displayName;

        RolEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public boolean isAdmin() {
        return this.rol == RolEnum.ADMIN;
    }

    public boolean isInstructor() {
        return this.rol == RolEnum.INSTRUCTOR;
    }

    public boolean isUser() {
        return this.rol == RolEnum.USER;
    }

    public void addInscripcion(Inscripcion inscripcion) {
        inscripciones.add(inscripcion);
        inscripcion.setUsuario(this);
    }

    public void addBadge(Badge badge) {
        badges.add(badge);
        badge.getUsuarios().add(this);
    }

    @PrePersist
    protected void onCreate() {
        if (activo == null) {
            activo = true;
        }
        if (rol == null) {
            rol = RolEnum.USER;
        }
    }
}
