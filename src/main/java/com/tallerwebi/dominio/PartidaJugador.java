package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

  @Column(name = "puntaje_acumulado", nullable = false)
  private int puntajeAcumulado;

  @Column(name = "posicion_final")
  private Integer posicionFinal;

  @Column(nullable = false)
  private int aciertos;

  @Column(nullable = false)
  private int errores;

  @Column(name = "racha_respuestas_partida", nullable = false)
  private int rachaRespuestasPartida;

  @Column(name = "estado_jugador_partida", length = 30)
  private String estadoJugadorPartida;

  // Ronda en la que el jugador habilitó su segundo intento. Se persiste para
  // que el comodín siga funcionando entre solicitudes HTTP independientes.
  @Column(name = "doble_chance_ronda_id")
  private Long dobleChanceRondaId;

  //@OneToMany(mappedBy = "partidaJugador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  //private List<PartidaJugadorComodin> comodines;

  public PartidaJugador() {
    this.puntajeAcumulado = 0;
    this.aciertos = 0;
    this.errores = 0;
    this.rachaRespuestasPartida = 0;
    this.estadoJugadorPartida = "JUGANDO";
    //this.comodines = new ArrayList<PartidaJugadorComodin>();
  }

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

  public int getPuntajeAcumulado() {
    return puntajeAcumulado;
  }

  public void setPuntajeAcumulado(int puntajeAcumulado) {
    this.puntajeAcumulado = puntajeAcumulado;
  }

  public Integer getPosicionFinal() {
    return posicionFinal;
  }

  public void setPosicionFinal(Integer posicionFinal) {
    this.posicionFinal = posicionFinal;
  }

  public int getAciertos() {
    return aciertos;
  }

  public void setAciertos(int aciertos) {
    this.aciertos = aciertos;
  }

  public int getErrores() {
    return errores;
  }

  public void setErrores(int errores) {
    this.errores = errores;
  }

  public int getRachaRespuestasPartida() {
    return rachaRespuestasPartida;
  }

  public void setRachaRespuestasPartida(int rachaRespuestasPartida) {
    this.rachaRespuestasPartida = rachaRespuestasPartida;
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
  //public List<PartidaJugadorComodin> getComodines() { return comodines; }
  //public void setComodines(List<PartidaJugadorComodin> comodines) { this.comodines = comodines; }
}
