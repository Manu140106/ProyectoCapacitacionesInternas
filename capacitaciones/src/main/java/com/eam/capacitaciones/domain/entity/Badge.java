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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Badge")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idBadge;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100)
	@Column(nullable = false, unique = true, length = 100)
	private String nombre;

	@Size(max = 200)
	@Column(length = 200)
	private String criterio;

	@Size(max = 200)
	@Column(length = 200)
	private String icono;

	@Column
	@Builder.Default
	private Integer puntosRequeridos = 0;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@Builder.Default
	@ManyToMany(mappedBy = "badges", fetch = FetchType.LAZY)
	private Set<Usuario> usuarios = new HashSet<>();

	@PrePersist
	protected void onCreate() {
		if (puntosRequeridos == null) puntosRequeridos = 0;
	}
}