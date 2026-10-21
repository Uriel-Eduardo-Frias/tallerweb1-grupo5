package com.tallerwebi.dominio.modelo;

public class EstadisticaUsuario {

  private String username;
  private int partidasJugadas;
  private int partidasGanadas;
  private int preguntasAcertadas;
  private int preguntasErradas;
  private int rachaActualRespuestas;
  private int mejorRachaRespuestas;

  public void sumarPartida(PartidaJugador jugador) {
    partidasJugadas++;

    Integer posicion = jugador.getPosicionFinal();
    if (posicion != null && posicion == 1) {
      partidasGanadas++;
    }

    preguntasAcertadas += jugador.getAciertos();
    preguntasErradas += jugador.getErrores();

    if (jugador.getMejorRachaPartida() > mejorRachaRespuestas) {
      mejorRachaRespuestas = jugador.getMejorRachaPartida();
    }
  }

  public int getPartidasPerdidas() {
    return partidasJugadas - partidasGanadas;
  }

  public double getPromedioAciertos() {
    int total = preguntasAcertadas + preguntasErradas;

    if (total == 0) {
      return 0.0;
    }

    return Math.round((preguntasAcertadas * 1000.0) / total) / 10.0;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getPartidasJugadas() {
    return partidasJugadas;
  }

  public int getPartidasGanadas() {
    return partidasGanadas;
  }

  public int getPreguntasAcertadas() {
    return preguntasAcertadas;
  }

  public int getPreguntasErradas() {
    return preguntasErradas;
  }

  public int getRachaActualRespuestas() {
    return rachaActualRespuestas;
  }

  public void setRachaActualRespuestas(int rachaActualRespuestas) {
    this.rachaActualRespuestas = rachaActualRespuestas;
  }

  public int getMejorRachaRespuestas() {
    return mejorRachaRespuestas;
  }
}
