package com.tallerwebi.dominio;

public class JugadorInexistenteExeption extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public JugadorInexistenteExeption(String mensaje) {
    super(mensaje);
  }
}
