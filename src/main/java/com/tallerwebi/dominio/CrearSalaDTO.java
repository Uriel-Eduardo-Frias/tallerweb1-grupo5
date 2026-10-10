package com.tallerwebi.dominio;

import com.tallerwebi.dominio.enums.ModoJuego;

public class CrearSalaDTO {

  private String nombre;

  private int maxJugadores = 4;

  private boolean esPrivada = false;

  private ModoJuego modoJuego = ModoJuego.TURNO_TODOS;

  private int totalRondas = 5;

  public CrearSalaDTO() {}

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public int getMaxJugadores() {
    return maxJugadores;
  }

  public void setMaxJugadores(int maxJugadores) {
    this.maxJugadores = maxJugadores;
  }

  public boolean isEsPrivada() {
    return esPrivada;
  }

  public void setEsPrivada(boolean esPrivada) {
    this.esPrivada = esPrivada;
  }

  public ModoJuego getModoJuego() {
    return modoJuego;
  }

  public void setModoJuego(ModoJuego modoJuego) {
    this.modoJuego = modoJuego;
  }

  public int getTotalRondas() {
    return totalRondas;
  }

  public void setTotalRondas(int totalRondas) {
    this.totalRondas = totalRondas;
  }
}
