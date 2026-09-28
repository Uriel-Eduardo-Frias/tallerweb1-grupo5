package com.tallerwebi.dominio;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preguntas")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identificador;

    private String textoDeLaPregunta;

    // Muchas preguntas pertenecen a una categoría
    @ManyToOne(fetch = FetchType.EAGER)
    private Categoria categoria;

    // Una pregunta contiene una lista de opciones
    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Opcion> opciones = new ArrayList<>();

    public Pregunta() {}

    public Pregunta(String textoDeLaPregunta, Categoria categoria) {
        this.textoDeLaPregunta = textoDeLaPregunta;
        this.categoria = categoria;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Long identificador) {
        this.identificador = identificador;
    }

    public String getTextoDeLaPregunta() {
        return textoDeLaPregunta;
    }

    public void setTextoDeLaPregunta(String textoDeLaPregunta) {
        this.textoDeLaPregunta = textoDeLaPregunta;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Opcion> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<Opcion> opciones) {
        this.opciones = opciones;
    }

    // Método de ayuda para asociar una opción a la pregunta agregándola a la lista
    public void agregarOpcion(Opcion opcion) {
        this.opciones.add(opcion);
        opcion.setPregunta(this);
    }
}