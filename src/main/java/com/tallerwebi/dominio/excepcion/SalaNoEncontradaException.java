package com.tallerwebi.dominio.excepcion;

public class SalaNoEncontradaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SalaNoEncontradaException(String mensaje) {
    super(mensaje);
  }
}
