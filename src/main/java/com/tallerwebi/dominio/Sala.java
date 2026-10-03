package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class Sala {

  private String codigo;
  private String nombre;
  private Usuario host;
  private List<SalaJugador> jugadores;
  private Integer maxJugadores;
  private EstadoSala estado;

  public Sala(String codigo, String nombre, Usuario host) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.host = host;
    this.jugadores = new ArrayList<>();
    this.maxJugadores = 4;
    this.estado = EstadoSala.EN_ESPERA;
  }

  public boolean agregarJugador(SalaJugador salaJugador) {
    if (salaJugador == null) {
      throw new JugadorInexistenteExeption("No se encontro el jugador");
    }

    return this.jugadores.add(salaJugador);
  }

  public void quitarJugador(Usuario usuario) {
    for (int i = 0; i < jugadores.size(); i++) {
      if (jugadores.get(i).getUsuario().getId().equals(usuario.getId())) {
        jugadores.remove(i);
        break;
      }
    }
  }

  public String getCodigo() {
    return this.codigo;
  }

  public String getNombre() {
    return this.nombre;
  }

  public Usuario getHost() {
    return this.host;
  }

  public List<SalaJugador> getJugadores() {
    return this.jugadores;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void setHost(Usuario host) {
    this.host = host;
  }

  public void setJugadores(List<SalaJugador> jugadores) {
    this.jugadores = jugadores;
  }

  public Integer getMaxJugadores() {
    return maxJugadores;
  }

  public void setMaxJugadores(Integer maxJugadores) {
    this.maxJugadores = maxJugadores;
  }

  public EstadoSala getEstado() {
    return estado;
  }

  public void iniciar() {
    if (estado != EstadoSala.EN_ESPERA) {
      throw new IllegalStateException("La sala no está en espera");
    }

    estado = EstadoSala.EN_CURSO;
  }

  public void setEstado(EstadoSala estadoSala) {
    this.estado = estadoSala;
  }
}
