package com.tallerwebi.dominio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Revancha {

  @Column(name = "sala_revancha_codigo")
  private String salaCodigo;

  public Revancha() {}

  public String getSalaCodigo() {
    return salaCodigo;
  }

  public void setSalaCodigo(String salaCodigo) {
    this.salaCodigo = salaCodigo;
  }
}
