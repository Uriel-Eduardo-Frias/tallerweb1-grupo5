package com.tallerwebi.dominio.excepcion;

public class DesafioDiarioException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DesafioDiarioException(String mensaje) {
    super(mensaje);
  }
}
