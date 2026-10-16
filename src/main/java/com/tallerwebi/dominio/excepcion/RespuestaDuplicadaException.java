package com.tallerwebi.dominio.excepcion;

public class RespuestaDuplicadaException extends IllegalStateException {

  private static final long serialVersionUID = 1L;

  public RespuestaDuplicadaException() {
    super("Ya respondiste esta pregunta.");
  }
}
