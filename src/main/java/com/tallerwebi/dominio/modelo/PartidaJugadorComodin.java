package com.tallerwebi.dominio.modelo;

import jakarta.persistence.*;

@Entity
@Table(
  name = "partidas_jugadores_comodines",
  uniqueConstraints = { @UniqueConstraint(columnNames = { "partida_jugador_id", "comodin_id" }) }
)
public class PartidaJugadorComodin {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partida_jugador_id", nullable = false)
  private PartidaJugador partidaJugador;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comodin_id", nullable = false)
  private Comodin comodin;

  @Column(name = "cantidad_disponible", nullable = false)
  private int cantidadDisponible;

  @Column(name = "veces_usado", nullable = false)
  private int vecesUsado;

  public PartidaJugadorComodin() {
    this.cantidadDisponible = 1;
    this.vecesUsado = 0;
  }

  public PartidaJugadorComodin(
    PartidaJugador partidaJugador,
    Comodin comodin,
    int cantidadInicial
  ) {
    this.partidaJugador = partidaJugador;
    this.comodin = comodin;
    this.cantidadDisponible = cantidadInicial;
    this.vecesUsado = 0;
  }

  // --- Getters y Setters ---

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PartidaJugador getPartidaJugador() {
    return partidaJugador;
  }

  public void setPartidaJugador(PartidaJugador partidaJugador) {
    this.partidaJugador = partidaJugador;
  }

  public Comodin getComodin() {
    return comodin;
  }

  public void setComodin(Comodin comodin) {
    this.comodin = comodin;
  }

  public int getCantidadDisponible() {
    return cantidadDisponible;
  }

  public void setCantidadDisponible(int cantidadDisponible) {
    this.cantidadDisponible = cantidadDisponible;
  }

  public int getVecesUsado() {
    return vecesUsado;
  }

  public void setVecesUsado(int vecesUsado) {
    this.vecesUsado = vecesUsado;
  }
}
