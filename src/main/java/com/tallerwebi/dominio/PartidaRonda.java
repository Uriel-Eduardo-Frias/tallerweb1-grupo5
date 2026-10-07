package com.tallerwebi.dominio;

public class PartidaRonda {

  private Partida partida;
  private int numero;
  private Pregunta pregunta;
  private EstadoRonda estado;

  public PartidaRonda(Partida partida, int numero) {
    this.partida = partida;
    this.numero = numero;
    this.estado = EstadoRonda.PENDIENTE;
  }

  public Partida getPartida() {
    return partida;
  }

  public int getNumero() {
    return numero;
  }

  public Pregunta getPregunta() {
    return pregunta;
  }

  public void setPregunta(Pregunta pregunta) {
    this.pregunta = pregunta;
  }

  public EstadoRonda getEstado() {
    return estado;
  }

  public void setEstado(EstadoRonda estado) {
    this.estado = estado;
  }
}
