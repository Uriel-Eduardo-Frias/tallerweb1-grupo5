package com.tallerwebi.dominio;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Sala {

  private String codigo;

  private String nombre;

  private Usuario usuario;

  public Sala(String codigo, String nombre, Usuario usuario) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.usuario = usuario;
  }

  public String getCodigo() {
    return this.codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setId(String codigo) {
    this.codigo = codigo;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }
}
