package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;
  private String password;
  private String rol;
  private Boolean activo = false;

  private String username;
  private String nombreCompleto;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRol() {
    return rol;
  }

  public void setRol(String rol) {
    this.rol = rol;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public void activar() {
    activo = true;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getNombreCompleto() {
    return nombreCompleto;
  }

  public void setNombreCompleto(String nombreCompleto) {
    this.nombreCompleto = nombreCompleto;
  }

  // Relacion con la clase PerfilUsuario:
  @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private PerfilUsuario perfil;

  public PerfilUsuario getPerfil() {
    return perfil;
  }

  public void setPerfil(PerfilUsuario perfil) {
    this.perfil = perfil;
  }

  @Override
  public boolean equals(Object otroObjeto) {
    if (this == otroObjeto) {
      return true;
    }
    if (!(otroObjeto instanceof Usuario)) {
      return false;
    }
    Usuario otro = (Usuario) otroObjeto;
    return Objects.equals(username, otro.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }
}
