package com.tallerwebi.dominio;

public class SalaSinJugadoresException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SalaSinJugadoresException(String mensaje) {
    super(mensaje);
  }
}
