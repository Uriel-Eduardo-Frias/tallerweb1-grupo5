package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class Sala {

  private String codigo;
  private String nombre;
  private Usuario host;
  private List<Usuario> jugadores;

  public Sala(String codigo, String nombre, Usuario host) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.host = host;
    this.jugadores = new ArrayList<>();
  }

  public boolean agregarJugador(Usuario usuario) {
    if (usuario == null) {
      throw new JugadorInexistenteExeption("No se encontro el jugador");
    }

    return this.jugadores.add(usuario);
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

  public List<Usuario> getJugadores() {
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

  public void setJugadores(List<Usuario> jugadores) {
    this.jugadores = jugadores;
  }
}
