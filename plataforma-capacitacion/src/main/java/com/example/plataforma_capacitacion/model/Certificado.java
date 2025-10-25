package com.example.plataforma_capacitacion.model;

import jakarta.persistence.*;

@Entity
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCurso;
    private String fechaEmision;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Certificado() {}

    public Certificado(Long id, String nombreCurso, String fechaEmision, Usuario usuario) {
        this.id = id;
        this.nombreCurso = nombreCurso;
        this.fechaEmision = fechaEmision;
        this.usuario = usuario;
    }

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public String getNombreCurso() { 
        return nombreCurso; 
    }
    public void setNombreCurso(String nombreCurso) { 
        this.nombreCurso = nombreCurso; 
    }
    public String getFechaEmision() { 
        return fechaEmision; 
    }
    public void setFechaEmision(String fechaEmision) { 
        this.fechaEmision = fechaEmision; 
    }
    public Usuario getUsuario() { 
        return usuario; 
    }
    public void setUsuario(Usuario usuario) { 
        this.usuario = usuario; 
    }
}

