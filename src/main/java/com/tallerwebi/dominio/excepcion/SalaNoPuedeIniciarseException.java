package com.tallerwebi.dominio.excepcion;

public class SalaNoPuedeIniciarseException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SalaNoPuedeIniciarseException(String mensaje) {
    super(mensaje);
  }
}
