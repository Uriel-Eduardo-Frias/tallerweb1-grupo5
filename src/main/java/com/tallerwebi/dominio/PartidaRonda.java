package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
@Table(
  name = "partida_rondas",
  uniqueConstraints = @UniqueConstraint(columnNames = { "partida_id", "numero" })
)
public class PartidaRonda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partida_id", nullable = false)
  private Partida partida;

  private int numero;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pregunta_id")
  private Pregunta pregunta;

  @Enumerated(EnumType.STRING)
  private EstadoRonda estado;

  public PartidaRonda() {}

  public PartidaRonda(Partida partida, int numero) {
    this.partida = partida;
    this.numero = numero;
    this.estado = EstadoRonda.PENDIENTE;
  }

  public Partida getPartida() {
    return partida;
  }

  public int getNumero() {
    return numero;
  }

  public Pregunta getPregunta() {
    return pregunta;
  }

  public void setPregunta(Pregunta pregunta) {
    this.pregunta = pregunta;
  }

  public EstadoRonda getEstado() {
    return estado;
  }

  public void setEstado(EstadoRonda estado) {
    this.estado = estado;
  }

  public void setNumero(int numero) {
    this.numero = numero;
  }

  public void setPartida(Partida partida) {
    this.partida = partida;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
