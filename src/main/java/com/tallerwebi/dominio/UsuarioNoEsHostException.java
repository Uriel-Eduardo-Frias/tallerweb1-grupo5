package com.tallerwebi.dominio;

public class UsuarioNoEsHostException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public UsuarioNoEsHostException(String mensaje) {
    super(mensaje);
  }
}
