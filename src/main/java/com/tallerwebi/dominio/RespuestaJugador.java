package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "respuestas_jugadores")
public class RespuestaJugador {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partida_jugador_id", nullable = false)
  private PartidaJugador partidaJugador;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pregunta_id", nullable = false)
  private Pregunta pregunta;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "opcion_elegida_id", nullable = false)
  private Opcion opcionElegida;

  @Column(name = "es_correcta", nullable = false)
  private boolean esCorrecta;

  @Column(name = "fecha_respuesta", nullable = false)
  private LocalDateTime fechaRespuesta;

  public RespuestaJugador() {}

  public RespuestaJugador(
    PartidaJugador partidaJugador,
    Pregunta pregunta,
    Opcion opcionElegida,
    boolean esCorrecta,
    LocalDateTime fechaRespuesta
  ) {
    this.partidaJugador = partidaJugador;
    this.pregunta = pregunta;
    this.opcionElegida = opcionElegida;
    this.esCorrecta = esCorrecta;
    this.fechaRespuesta = fechaRespuesta;
  }

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

  public Pregunta getPregunta() {
    return pregunta;
  }

  public void setPregunta(Pregunta pregunta) {
    this.pregunta = pregunta;
  }

  public Opcion getOpcionElegida() {
    return opcionElegida;
  }

  public void setOpcionElegida(Opcion opcionElegida) {
    this.opcionElegida = opcionElegida;
  }

  public boolean getEsCorrecta() {
    return esCorrecta;
  }

  public void setEsCorrecta(boolean esCorrecta) {
    this.esCorrecta = esCorrecta;
  }

  public LocalDateTime getFechaRespuesta() {
    return fechaRespuesta;
  }

  public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
    this.fechaRespuesta = fechaRespuesta;
  }
}
