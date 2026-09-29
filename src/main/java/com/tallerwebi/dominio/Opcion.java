package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Opcion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String texto;
  private boolean esCorrecta;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pregunta_id")
  private Pregunta pregunta;

  public Opcion() {}

  public Opcion(String texto, boolean esCorrecta) {
    this.texto = texto;
    this.esCorrecta = esCorrecta;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTexto() {
    return texto;
  }

  public void setTexto(String texto) {
    this.texto = texto;
  }

  public void setEsCorrecta(boolean esCorrecta) {
    this.esCorrecta = esCorrecta;
  }

  public Boolean getEsCorrecta() {
    return esCorrecta;
  }

  public Pregunta getPregunta() {
    return pregunta;
  }

  public void setPregunta(Pregunta pregunta) {
    this.pregunta = pregunta;
  }
}
