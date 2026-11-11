package com.tallerwebi.dominio.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
  name = "desafios_diarios",
  uniqueConstraints = @UniqueConstraint(name = "uk_desafio_diario_fecha", columnNames = "fecha")
)
public class DesafioDiario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private LocalDate fecha;

  @OneToMany(mappedBy = "desafio", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("numero ASC")
  private List<PreguntaDesafio> preguntas = new ArrayList<>();

  public DesafioDiario() {}

  public DesafioDiario(LocalDate fecha) {
    this.fecha = fecha;
  }

  public void agregarPregunta(Pregunta pregunta, int numero) {
    this.preguntas.add(new PreguntaDesafio(this, pregunta, numero));
  }

  public Long getId() {
    return id;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }

  public List<PreguntaDesafio> getPreguntas() {
    return preguntas;
  }
}
