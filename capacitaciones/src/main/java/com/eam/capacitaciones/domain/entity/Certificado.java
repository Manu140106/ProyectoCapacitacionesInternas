package com.eam.capacitaciones.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Certificado",
       indexes = {
           @Index(name = "idx_usuario", columnList = "usuarioId"),
           @Index(name = "idx_curso", columnList = "cursoId"),
           @Index(name = "idx_hash", columnList = "hash")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_usuario_curso_certificado", columnNames = {"usuarioId", "cursoId"}),
           @UniqueConstraint(name = "uk_hash", columnNames = {"hash"})
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCertificado;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long cursoId;

    @Column(nullable = false)
    private LocalDate fechaEmision;

    @NotBlank(message = "El hash es obligatorio")
    @Size(max = 200)
    @Column(nullable = false, unique = true, length = 200)
    private String hash;

    @Size(max = 255)
    @Column(length = 255)
    private String urlPdf;

    @Builder.Default
    @Column(nullable = false)
    private Boolean revocado = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cursoId", insertable = false, updatable = false)
    private Curso curso;

    public void revocar() { this.revocado = true; }
    public boolean esValido() { return !revocado; }

    @PrePersist
    protected void onCreate() {
        if (fechaEmision == null) fechaEmision = LocalDate.now();
        if (revocado == null) revocado = false;
    }
}
