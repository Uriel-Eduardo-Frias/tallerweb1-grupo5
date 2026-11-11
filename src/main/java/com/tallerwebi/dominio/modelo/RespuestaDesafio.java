package com.tallerwebi.dominio.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
  name = "respuestas_desafio",
  uniqueConstraints = @UniqueConstraint(
    name = "uk_respuesta_intento_pregunta",
    columnNames = { "intento_id", "pregunta_id" }
  )
)
public class RespuestaDesafio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "intento_id", nullable = false)
  private IntentoDesafio intento;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "pregunta_id", nullable = false)
  private Pregunta pregunta;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "opcion_elegida_id")
  private Opcion opcionElegida;

  @Column(nullable = false)
  private boolean esCorrecta;

  @Column(nullable = false)
  private LocalDateTime fechaRespuesta;

  @Column(nullable = false)
  private boolean fuePorTiempo = false;

  public RespuestaDesafio() {}

  public RespuestaDesafio(
    IntentoDesafio intento,
    Pregunta pregunta,
    Opcion opcionElegida,
    boolean esCorrecta,
    LocalDateTime fechaRespuesta,
    boolean fuePorTiempo
  ) {
    this.intento = intento;
    this.pregunta = pregunta;
    this.opcionElegida = opcionElegida;
    this.esCorrecta = esCorrecta;
    this.fechaRespuesta = fechaRespuesta;
    this.fuePorTiempo = fuePorTiempo;
  }

  public Long getId() {
    return id;
  }

  public IntentoDesafio getIntento() {
    return intento;
  }

  public void setIntento(IntentoDesafio intento) {
    this.intento = intento;
  }

  public Pregunta getPregunta() {
    return pregunta;
  }

  public Opcion getOpcionElegida() {
    return opcionElegida;
  }

  public boolean isEsCorrecta() {
    return esCorrecta;
  }

  public LocalDateTime getFechaRespuesta() {
    return fechaRespuesta;
  }

  public boolean isFuePorTiempo() {
    return fuePorTiempo;
  }
}
