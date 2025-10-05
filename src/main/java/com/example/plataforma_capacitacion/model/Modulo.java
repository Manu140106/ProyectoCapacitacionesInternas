package com.example.plataforma_capacitacion.model;

import jakarta.persistence.*;

@Entity
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String contenido; // puede ser texto, URL de video, link a PDF

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    public Modulo() {
    }

    public Modulo(Long id, String titulo, String contenido, Curso curso) {
        this.id = id;
        this.titulo = titulo;
        this.contenido = contenido;
        this.curso = curso;
    }

    // Getters y setters
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

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}
