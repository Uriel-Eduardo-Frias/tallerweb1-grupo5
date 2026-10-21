package com.tallerwebi.dominio.excepcion;

public class PrimerIntentoFallidoDobleChanceException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final Long opcionIncorrectaId;

  public PrimerIntentoFallidoDobleChanceException(Long opcionIncorrectaId) {
    super("Primer intento fallido. Se activa el segundo intento por Doble Chance.");
    this.opcionIncorrectaId = opcionIncorrectaId;
  }

  public Long getOpcionIncorrectaId() {
    return opcionIncorrectaId;
  }
}
