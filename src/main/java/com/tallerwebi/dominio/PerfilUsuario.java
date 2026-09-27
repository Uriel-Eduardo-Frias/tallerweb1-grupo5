package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
public class PerfilUsuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  private String avatarUrl;
  private String biografia;
  private Integer nivel;
  private Integer experiencia;

  public PerfilUsuario() {
    this.nivel = 1;
    this.experiencia = 0;
    this.biografia = "¡Listo para competir en la Trivia!";
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public Integer getNivel() {
    return nivel;
  }

  public void setNivel(Integer nivel) {
    this.nivel = nivel;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public String getBiografia() {
    return biografia;
  }

  public void setBiografia(String biografia) {
    this.biografia = biografia;
  }

  public Integer getExperiencia() {
    return experiencia;
  }

  public void setExperiencia(Integer experiencia) {
    this.experiencia = experiencia;
  }
}
