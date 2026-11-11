package com.tallerwebi.dominio.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "intentos_desafio")
public class IntentoDesafio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "desafio_id", nullable = false)
  private DesafioDiario desafio;

  @Column(nullable = false)
  private LocalDateTime fechaInicio;

  private LocalDateTime fechaFin;

  @Column(nullable = false)
  private Integer puntaje = 0;

  @OneToMany(mappedBy = "intento", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RespuestaDesafio> respuestas = new ArrayList<>();

  public IntentoDesafio() {}

  public IntentoDesafio(Usuario usuario, DesafioDiario desafio, LocalDateTime fechaInicio) {
    this.usuario = usuario;
    this.desafio = desafio;
    this.fechaInicio = fechaInicio;
  }

  public void agregarRespuesta(RespuestaDesafio respuesta) {
    respuestas.add(respuesta);
    respuesta.setIntento(this);
  }

  public boolean estaFinalizado() {
    return fechaFin != null;
  }

  public Long getId() {
    return id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public DesafioDiario getDesafio() {
    return desafio;
  }

  public void setDesafio(DesafioDiario desafio) {
    this.desafio = desafio;
  }

  public LocalDateTime getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDateTime fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDateTime getFechaFin() {
    return fechaFin;
  }

  public void setFechaFin(LocalDateTime fechaFin) {
    this.fechaFin = fechaFin;
  }

  public Integer getPuntaje() {
    return puntaje;
  }

  public void setPuntaje(Integer puntaje) {
    this.puntaje = puntaje;
  }

  public List<RespuestaDesafio> getRespuestas() {
    return respuestas;
  }
}
