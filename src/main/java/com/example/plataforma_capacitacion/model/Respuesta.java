package com.example.plataforma_capacitacion.model;

import jakarta.persistence.*;

@Entity
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenido;
    private boolean correcta;

    @ManyToOne
    @JoinColumn(name = "evaluacion_id")
    private Evaluacion evaluacion;

    public Respuesta() {}

    public Respuesta(Long id, String contenido, boolean correcta, Evaluacion evaluacion) {
        this.id = id;
        this.contenido = contenido;
        this.correcta = correcta;
        this.evaluacion = evaluacion;
    }

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public String getContenido() { 
        return contenido; 
    }
    public void setContenido(String contenido) { 
        this.contenido = contenido; 
    }
    public boolean isCorrecta() { 
        return correcta; 
    }
    public void setCorrecta(boolean correcta) { 
        this.correcta = correcta; 
    }
    public Evaluacion getEvaluacion() { 
        return evaluacion; 
    }
    public void setEvaluacion(Evaluacion evaluacion) { 
        this.evaluacion = evaluacion; 
    }
}

