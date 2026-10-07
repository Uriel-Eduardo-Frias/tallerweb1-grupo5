package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class Partida {

  private Long id;
  private Sala sala;
  private EstadoPartida estado;
  private List<PartidaRonda> rondas;
  private Integer rondaActual;
  private Integer totalRondas;
  private ModoJuego modoJuego;

  public Partida(Sala sala, EstadoPartida estado) {
    this.sala = sala;
    this.estado = estado;
    this.rondas = new ArrayList<>();
    this.rondaActual = 1;
    this.totalRondas = 5;
    this.modoJuego = ModoJuego.TURNO_TODOS;
  }

  public void agregarRonda(PartidaRonda ronda) {
    this.rondas.add(ronda);
  }

  public PartidaRonda obtenerRondaActual() {
    for (PartidaRonda ronda : rondas) {
      if (ronda.getNumero() == rondaActual) {
        return ronda;
      }
    }

    return null;
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

  public List<PartidaRonda> getRondas() {
    return rondas;
  }

  public void setRondas(List<PartidaRonda> rondas) {
    this.rondas = rondas;
  }

  public Integer getTotalRondas() {
    return totalRondas;
  }

  public void setTotalRondas(Integer totalRondas) {
    this.totalRondas = totalRondas;
  }

  public Integer getRondaActual() {
    return rondaActual;
  }

  public void setRondaActual(Integer rondaActual) {
    this.rondaActual = rondaActual;
  }

  public ModoJuego getModoJuego() {
    return modoJuego;
  }

  public void setModoJuego(ModoJuego modoJuego) {
    this.modoJuego = modoJuego;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
