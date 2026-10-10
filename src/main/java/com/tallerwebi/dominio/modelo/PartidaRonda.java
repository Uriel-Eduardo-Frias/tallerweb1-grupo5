package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.EstadoRonda;
import jakarta.persistence.*;
import java.time.LocalDateTime;

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

  @Column(name = "fecha_inicio", nullable = false)
  private LocalDateTime fechaInicio;

  @Column(name = "fecha_fin")
  private LocalDateTime fechaFin;

  public PartidaRonda(Partida partida, Integer numeroRonda) {
    this();
    this.partida = partida;
    this.numero = numeroRonda;
  }

  public PartidaRonda() {
    this.estado = EstadoRonda.VOTACION_CATEGORIA;
    this.fechaInicio = LocalDateTime.now();
    //this.respuestas = new ArrayList<>();
    //this.votaciones = new ArrayList<>();
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

  public void setNumero(Integer numero) {
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
  /*
  public List<RespuestaJugador> getRespuestas() { return respuestas; }
  public void setRespuestas(List<RespuestaJugador> respuestas) { this.respuestas = respuestas; }
  public List<VotacionRonda> getVotaciones() { return votaciones; }
  public void setVotaciones(List<VotacionRonda> votaciones) { this.votaciones = votaciones; }
  */
}
