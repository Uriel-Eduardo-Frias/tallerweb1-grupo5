package com.tallerwebi.presentacion.dto;

import java.util.List;

public class Comodin5050DTO {

  private List<Long> opcionesDescartadas;
  private int penalizacionSegundos;

  public Comodin5050DTO() {}

  public Comodin5050DTO(List<Long> opcionesDescartadas, int penalizacionSegundos) {
    this.opcionesDescartadas = opcionesDescartadas;
    this.penalizacionSegundos = penalizacionSegundos;
  }

  public List<Long> getOpcionesDescartadas() {
    return opcionesDescartadas;
  }

  public void setOpcionesDescartadas(List<Long> opcionesDescartadas) {
    this.opcionesDescartadas = opcionesDescartadas;
  }

  public int getPenalizacionSegundos() {
    return penalizacionSegundos;
  }

  public void setPenalizacionSegundos(int penalizacionSegundos) {
    this.penalizacionSegundos = penalizacionSegundos;
  }
}
