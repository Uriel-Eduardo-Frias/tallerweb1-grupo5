package com.tallerwebi.dominio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EstadisticasJugador {

  @Column(name = "puntaje_acumulado", nullable = false)
  private int puntajeAcumulado;

  @Column(nullable = false)
  private int aciertos;

  @Column(nullable = false)
  private int errores;

  @Column(name = "racha_respuestas_partida", nullable = false)
  private int rachaRespuestasPartida;

  @Column(name = "mejor_racha_partida", nullable = false, columnDefinition = "integer default 0")
  private int mejorRachaPartida;

  public EstadisticasJugador() {}

  public void sumarPuntaje(int puntos) {
    puntajeAcumulado += puntos;
  }

  public void registrarAcierto() {
    aciertos++;
    rachaRespuestasPartida++;

    if (rachaRespuestasPartida > mejorRachaPartida) {
      mejorRachaPartida = rachaRespuestasPartida;
    }
  }

  public void registrarFallo() {
    errores++;
    rachaRespuestasPartida = 0;
  }

  public int getPuntajeAcumulado() {
    return puntajeAcumulado;
  }

  public void setPuntajeAcumulado(int puntajeAcumulado) {
    this.puntajeAcumulado = puntajeAcumulado;
  }

  public int getAciertos() {
    return aciertos;
  }

  public void setAciertos(int aciertos) {
    this.aciertos = aciertos;
  }

  public int getErrores() {
    return errores;
  }

  public void setErrores(int errores) {
    this.errores = errores;
  }

  public int getRachaRespuestasPartida() {
    return rachaRespuestasPartida;
  }

  public void setRachaRespuestasPartida(int rachaRespuestasPartida) {
    this.rachaRespuestasPartida = rachaRespuestasPartida;
  }

  public int getMejorRachaPartida() {
    return mejorRachaPartida;
  }

  public void setMejorRachaPartida(int mejorRachaPartida) {
    this.mejorRachaPartida = mejorRachaPartida;
  }
}
