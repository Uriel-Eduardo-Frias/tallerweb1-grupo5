package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
@Table(
  name = "sala_jugadores",
  uniqueConstraints = @UniqueConstraint(columnNames = { "sala_codigo", "usuario_id" })
)
public class SalaJugador {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sala_codigo", nullable = false)
  private Sala sala;

  private boolean esAnfitrion;

  @Enumerated(EnumType.STRING)
  private EstadoJugador estadoJugador;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  public SalaJugador() {}

  public SalaJugador(Sala sala, EstadoJugador estadoJugador, boolean esAnfitrion, Usuario usuario) {
    this.sala = sala;
    this.estadoJugador = estadoJugador;
    this.esAnfitrion = esAnfitrion;
    this.usuario = usuario;
  }

  public Sala getSala() {
    return sala;
  }

  public void setSala(Sala sala) {
    this.sala = sala;
  }

  public boolean isEsAnfitrion() {
    return esAnfitrion;
  }

  public void setEsAnfitrion(boolean esAnfitrion) {
    this.esAnfitrion = esAnfitrion;
  }

  public EstadoJugador getEstadoJugador() {
    return estadoJugador;
  }

  public void setEstadoJugador(EstadoJugador estadoJugador) {
    this.estadoJugador = estadoJugador;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }
}
