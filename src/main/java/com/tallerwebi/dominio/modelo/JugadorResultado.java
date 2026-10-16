package com.tallerwebi.dominio.modelo;

public class JugadorResultado {

  private final int posicion;
  private final String nombre;
  private final int puntaje;
  private final int aciertos;
  private final int errores;
  private final int mejorRacha;
  private final boolean esUsuarioActual;

  public JugadorResultado(
    int posicion,
    String nombre,
    int puntaje,
    int aciertos,
    int errores,
    int mejorRacha,
    boolean esUsuarioActual
  ) {
    this.posicion = posicion;
    this.nombre = nombre;
    this.puntaje = puntaje;
    this.aciertos = aciertos;
    this.errores = errores;
    this.mejorRacha = mejorRacha;
    this.esUsuarioActual = esUsuarioActual;
  }

  public int getPosicion() {
    return posicion;
  }

  public String getNombre() {
    return nombre;
  }

  public int getPuntaje() {
    return puntaje;
  }

  public int getAciertos() {
    return aciertos;
  }

  public int getErrores() {
    return errores;
  }

  public int getMejorRacha() {
    return mejorRacha;
  }

  public boolean isEsUsuarioActual() {
    return esUsuarioActual;
  }

  public boolean isGanador() {
    return posicion == 1;
  }
}
