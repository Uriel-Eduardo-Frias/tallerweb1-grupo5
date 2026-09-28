package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pregunta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long identificador;

  private String textoDeLaPregunta;

  @ManyToOne(fetch = FetchType.EAGER)
  private Categoria categoria;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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

  public void agregarOpcion(Opcion opcion) {
    this.opciones.add(opcion);
    opcion.setPregunta(this);
  }
}
