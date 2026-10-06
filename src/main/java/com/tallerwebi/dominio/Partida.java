package com.tallerwebi.dominio;

public class Partida {

  private Sala sala;
  private EstadoPartida estado;

  public Partida(Sala sala, EstadoPartida estado) {
    this.sala = sala;
    this.estado = estado;
  }

  public Sala getSala() {
    return sala;
  }

  public EstadoPartida getEstado() {
    return estado;
  }

  public void setSala(Sala sala) {
    this.sala = sala;
  }

  public void setEstado(EstadoPartida estado) {
    this.estado = estado;
  }
}
