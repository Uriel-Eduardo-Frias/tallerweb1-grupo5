package com.tallerwebi.presentacion.dto;

import java.util.List;

public class EventoSala {

  private String tipo;
  private String codigoSala;
  private List<String> jugadores;
  private Long partidaId;

  public EventoSala(String tipo, String codigoSala, List<String> jugadores) {
    this.tipo = tipo;
    this.codigoSala = codigoSala;
    this.jugadores = jugadores;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public String getCodigoSala() {
    return codigoSala;
  }

  public void setCodigoSala(String codigoSala) {
    this.codigoSala = codigoSala;
  }

  public List<String> getJugadores() {
    return jugadores;
  }

  public void setJugadores(List<String> jugadores) {
    this.jugadores = jugadores;
  }

  public Long getPartidaId() {
    return partidaId;
  }

  public void setPartidaId(Long partidaId) {
    this.partidaId = partidaId;
  }
}
