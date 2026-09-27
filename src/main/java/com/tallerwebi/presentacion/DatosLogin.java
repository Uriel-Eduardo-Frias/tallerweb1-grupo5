package com.tallerwebi.presentacion;

public class DatosLogin {

  private String username;
  private String password;

  private String email;
  private String nombreCompleto;

  public DatosLogin() {}

  public DatosLogin(String username, String password, String email, String nombreCompleto) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.nombreCompleto = nombreCompleto;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNombreCompleto() {
    return nombreCompleto;
  }

  public void setNombreCompleto(String nombreCompleto) {
    this.nombreCompleto = nombreCompleto;
  }
}
