package com.example.plataforma_capacitacion.model;

import jakarta.persistence.*;

@Entity
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String tipo; // QUIZ, EXAMEN, PRACTICA

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    public Evaluacion() {}

    public Evaluacion(Long id, String titulo, String tipo, Curso curso) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.curso = curso;
    }

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public String getTitulo() { 
        return titulo; 
    }
    public void setTitulo(String titulo) { 
        this.titulo = titulo; 
    }
    public String getTipo() { 
        return tipo; 
    }
    public void setTipo(String tipo) { 
        this.tipo = tipo; 
    }
    public Curso getCurso() { 
        return curso; 
    }
    public void setCurso(Curso curso) { 
        this.curso = curso; 
    }
}

