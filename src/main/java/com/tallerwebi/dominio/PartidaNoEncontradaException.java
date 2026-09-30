package com.tallerwebi.dominio;

public class PartidaNoEncontradaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public PartidaNoEncontradaException(String mensaje) {
    super(mensaje);
  }
}
