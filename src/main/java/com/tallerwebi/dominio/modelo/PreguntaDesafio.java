package com.tallerwebi.dominio.modelo;

import jakarta.persistence.*;

@Entity
@Table(
  name = "preguntas_desafio",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_desafio_numero_pregunta",
      columnNames = { "desafio_id", "numero" }
    ),
    @UniqueConstraint(name = "uk_desafio_pregunta", columnNames = { "desafio_id", "pregunta_id" }),
  }
)
public class PreguntaDesafio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "desafio_id", nullable = false)
  private DesafioDiario desafio;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "pregunta_id", nullable = false)
  private Pregunta pregunta;

  @Column(nullable = false)
  private Integer numero;

  public PreguntaDesafio() {}

  public PreguntaDesafio(DesafioDiario desafio, Pregunta pregunta, Integer numero) {
    this.desafio = desafio;
    this.pregunta = pregunta;
    this.numero = numero;
  }

  public Long getId() {
    return id;
  }

  public DesafioDiario getDesafio() {
    return desafio;
  }

  public Pregunta getPregunta() {
    return pregunta;
  }

  public Integer getNumero() {
    return numero;
  }
}
