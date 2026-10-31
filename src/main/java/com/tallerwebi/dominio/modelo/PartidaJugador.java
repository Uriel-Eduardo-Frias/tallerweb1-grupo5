package com.tallerwebi.dominio.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partidas_jugadores")
public class PartidaJugador {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partida_id", nullable = false)
  private Partida partida;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @Embedded
  private EstadisticasJugador estadisticas = new EstadisticasJugador();

  @Column(name = "posicion_final")
  private Integer posicionFinal;

  @Column(name = "estado_jugador_partida", length = 30)
  private String estadoJugadorPartida = "JUGANDO";

  // Ronda en la que el jugador habilitó su segundo intento.
  @Column(name = "doble_chance_ronda_id")
  private Long dobleChanceRondaId;

  // Fecha y hora hasta la que el jugador está bloqueado (10 segundos)
  @Column(name = "bloqueado_hasta")
  private LocalDateTime bloqueadoHasta;

  public PartidaJugador() {}

  public PartidaJugador(Partida partida, Usuario usuario) {
    this();
    this.partida = partida;
    this.usuario = usuario;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Partida getPartida() {
    return partida;
  }

  public void setPartida(Partida partida) {
    this.partida = partida;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public EstadisticasJugador getEstadisticas() {
    return estadisticas;
  }

  public Integer getPosicionFinal() {
    return posicionFinal;
  }

  public void setPosicionFinal(Integer posicionFinal) {
    this.posicionFinal = posicionFinal;
  }

  public String getEstadoJugadorPartida() {
    return estadoJugadorPartida;
  }

  public void setEstadoJugadorPartida(String estadoJugadorPartida) {
    this.estadoJugadorPartida = estadoJugadorPartida;
  }

  public Long getDobleChanceRondaId() {
    return dobleChanceRondaId;
  }

  public void setDobleChanceRondaId(Long dobleChanceRondaId) {
    this.dobleChanceRondaId = dobleChanceRondaId;
  }

  public LocalDateTime getBloqueadoHasta() {
    return bloqueadoHasta;
  }

  public void setBloqueadoHasta(LocalDateTime bloqueadoHasta) {
    this.bloqueadoHasta = bloqueadoHasta;
  }

  public boolean estaBloqueado() {
    return bloqueadoHasta != null && LocalDateTime.now().isBefore(bloqueadoHasta);
  }

  // Delegados: mantienen la API anterior para no romper código existente

  public int getPuntajeAcumulado() {
    return estadisticas.getPuntajeAcumulado();
  }

  public void setPuntajeAcumulado(int puntajeAcumulado) {
    estadisticas.setPuntajeAcumulado(puntajeAcumulado);
  }

  public int getAciertos() {
    return estadisticas.getAciertos();
  }

  public void setAciertos(int aciertos) {
    estadisticas.setAciertos(aciertos);
  }

  public int getErrores() {
    return estadisticas.getErrores();
  }

  public void setErrores(int errores) {
    estadisticas.setErrores(errores);
  }

  public int getRachaRespuestasPartida() {
    return estadisticas.getRachaRespuestasPartida();
  }

  public void setRachaRespuestasPartida(int rachaRespuestasPartida) {
    estadisticas.setRachaRespuestasPartida(rachaRespuestasPartida);
  }

  public int getMejorRachaPartida() {
    return estadisticas.getMejorRachaPartida();
  }

  public void setMejorRachaPartida(int mejorRachaPartida) {
    estadisticas.setMejorRachaPartida(mejorRachaPartida);
  }
}