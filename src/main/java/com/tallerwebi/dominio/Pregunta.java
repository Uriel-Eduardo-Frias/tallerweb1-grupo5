package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pregunta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long identificador;

  private String descripcion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoria_id")
  private Categoria categoria;

  @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Opcion> opciones = new ArrayList<>();

  public Pregunta() {}

  public Pregunta(String textoDeLaPregunta, Categoria categoria) {
    this.descripcion = textoDeLaPregunta;
    this.categoria = categoria;
  }

  public Long getIdentificador() {
    return identificador;
  }

  public void setIdentificador(Long identificador) {
    this.identificador = identificador;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String textoDeLaPregunta) {
    this.descripcion = textoDeLaPregunta;
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
}
