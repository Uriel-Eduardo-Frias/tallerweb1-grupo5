package com.tallerwebi.dominio;

public class SalaJugador {

  private Sala sala;
  private boolean esAnfitrion;
  private EstadoJugador estadoJugador;
  private Usuario usuario;

  public SalaJugador() {}

  public SalaJugador(Sala sala, EstadoJugador estadoJugador, boolean esAnfitrion, Usuario usuario) {
    this.sala = sala;
    this.estadoJugador = estadoJugador;
    this.esAnfitrion = esAnfitrion;
    this.usuario = usuario;
  }

  public Sala getSala() {
    return sala;
  }

  public void setSala(Sala sala) {
    this.sala = sala;
  }

  public boolean isEsAnfitrion() {
    return esAnfitrion;
  }

  public void setEsAnfitrion(boolean esAnfitrion) {
    this.esAnfitrion = esAnfitrion;
  }

  public EstadoJugador getEstadoJugador() {
    return estadoJugador;
  }

  public void setEstadoJugador(EstadoJugador estadoJugador) {
    this.estadoJugador = estadoJugador;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }
}
