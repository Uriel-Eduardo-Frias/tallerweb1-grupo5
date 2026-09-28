package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Opcion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String texto;
  private boolean esCorrecta;

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

  public boolean isEsCorrecta() {
    return esCorrecta;
  }

  public void setEsCorrecta(boolean esCorrecta) {
    this.esCorrecta = esCorrecta;
  }

  public void setPregunta(Pregunta pregunta) {}
}
