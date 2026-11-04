package com.tallerwebi.dominio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "progreso_usuarios")
public class ProgresoUsuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false, unique = true)
  private Usuario usuario;

  @Column(name = "aciertos_totales", nullable = false)
  private int aciertosTotales;

  @Column(name = "victorias_consecutivas", nullable = false)
  private int victoriasConsecutivas;

  public ProgresoUsuario() {}

  public ProgresoUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public void registrarPartida(int aciertos, boolean gano) {
    aciertosTotales += aciertos;

    if (gano) {
      victoriasConsecutivas++;
    } else {
      victoriasConsecutivas = 0;
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public int getAciertosTotales() {
    return aciertosTotales;
  }

  public void setAciertosTotales(int aciertosTotales) {
    this.aciertosTotales = aciertosTotales;
  }

  public int getVictoriasConsecutivas() {
    return victoriasConsecutivas;
  }

  public void setVictoriasConsecutivas(int victoriasConsecutivas) {
    this.victoriasConsecutivas = victoriasConsecutivas;
  }
}
