package com.tallerwebi.dominio.excepcion;

public class JugadorBloqueadoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final long segundosRestantes;

  public JugadorBloqueadoException(long segundosRestantes) {
    super("El jugador se encuentra bloqueado por un comodín.");
    this.segundosRestantes = segundosRestantes;
  }

  public long getSegundosRestantes() {
    return segundosRestantes;
  }
}
