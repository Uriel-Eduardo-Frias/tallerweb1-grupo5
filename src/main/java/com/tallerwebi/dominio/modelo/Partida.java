package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.EstadoPartida;
import com.tallerwebi.dominio.enums.ModoJuego;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "partidas")
public class Partida {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sala_codigo", nullable = false, unique = true)
  private Sala sala;

  @Enumerated(EnumType.STRING)
  private EstadoPartida estado;

  @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PartidaRonda> rondas = new ArrayList<>();

  private Integer rondaActual;
  private Integer totalRondas;

  @Enumerated(EnumType.STRING)
  private ModoJuego modoJuego;

  @Column(name = "fecha_inicio", nullable = false)
  private LocalDateTime fechaInicio;

  @Column(name = "fecha_fin")
  private LocalDateTime fechaFin;

  public Partida() {}

  public Partida(Sala sala, EstadoPartida estado) {
    this.sala = sala;
    this.estado = estado;
    this.rondaActual = 1;
    this.totalRondas = 5;
    this.modoJuego = ModoJuego.TURNO_TODOS;
  }

  public void agregarRonda(PartidaRonda ronda) {
    this.rondas.add(ronda);
  }

  public PartidaRonda obtenerRondaActual() {
    for (PartidaRonda ronda : rondas) {
      if (ronda.getNumero() == rondaActual) {
        return ronda;
      }
    }

    return null;
  }

  public Sala getSala() {
    return sala;
  }

  public EstadoPartida getEstado() {
    return estado;
  }

  public void setSala(Sala sala) {
    this.sala = sala;
  }

  public void setEstado(EstadoPartida estado) {
    this.estado = estado;
  }

  public List<PartidaRonda> getRondas() {
    return rondas;
  }

  public void setRondas(List<PartidaRonda> rondas) {
    this.rondas = rondas;
  }

  public Integer getTotalRondas() {
    return totalRondas;
  }

  public void setTotalRondas(Integer totalRondas) {
    this.totalRondas = totalRondas;
  }

  public Integer getRondaActual() {
    return rondaActual;
  }

  public void setRondaActual(Integer rondaActual) {
    this.rondaActual = rondaActual;
  }

  public ModoJuego getModoJuego() {
    return modoJuego;
  }

  public void setModoJuego(ModoJuego modoJuego) {
    this.modoJuego = modoJuego;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
}
