package com.tallerwebi.dominio.excepcion;

public class UsuarioNoPerteneceASalaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public UsuarioNoPerteneceASalaException(String mensaje) {
    super(mensaje);
  }
}
