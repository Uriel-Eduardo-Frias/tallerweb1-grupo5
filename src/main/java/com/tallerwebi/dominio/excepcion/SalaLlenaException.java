package com.tallerwebi.dominio.excepcion;

public class SalaLlenaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SalaLlenaException(String mensaje) {
    super(mensaje);
  }
}
