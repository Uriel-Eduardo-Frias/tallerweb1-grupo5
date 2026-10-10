package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.EstadoSala;
import com.tallerwebi.dominio.enums.ModoJuego;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salas")
public class Sala {

  @Id
  private String codigo;

  private String nombre;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "host_id")
  private Usuario host;

  @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SalaJugador> jugadores = new ArrayList<>();

  private Integer maxJugadores;

  @Enumerated(EnumType.STRING)
  private EstadoSala estado;

  private int totalRondas;

  @Enumerated(EnumType.STRING)
  private ModoJuego modoJuego;

  private boolean esPrivada;

  public Sala() {}

  public Sala(String codigo, String nombre, Usuario host) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.host = host;
    this.jugadores = new ArrayList<>();
    this.maxJugadores = 4;
    this.estado = EstadoSala.EN_ESPERA;
  }

  public String getCodigo() {
    return this.codigo;
  }

  public String getNombre() {
    return this.nombre;
  }

  public Usuario getHost() {
    return this.host;
  }

  public List<SalaJugador> getJugadores() {
    return this.jugadores;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void setHost(Usuario host) {
    this.host = host;
  }

  public void setJugadores(List<SalaJugador> jugadores) {
    this.jugadores = jugadores;
  }

  public Integer getMaxJugadores() {
    return maxJugadores;
  }

  public void setMaxJugadores(Integer maxJugadores) {
    this.maxJugadores = maxJugadores;
  }

  public EstadoSala getEstado() {
    return estado;
  }

  public void iniciar() {
    if (estado != EstadoSala.EN_ESPERA) {
      throw new IllegalStateException("La sala no está en espera");
    }

    estado = EstadoSala.EN_CURSO;
  }

  public void setEstado(EstadoSala estadoSala) {
    this.estado = estadoSala;
  }

  public int getTotalRondas() {
    return totalRondas;
  }

  public void setTotalRondas(int totalRondas) {
    this.totalRondas = totalRondas;
  }

  public ModoJuego getModoJuego() {
    return modoJuego;
  }

  public void setModoJuego(ModoJuego modoJuego) {
    this.modoJuego = modoJuego;
  }

  public boolean isEsPrivada() {
    return esPrivada;
  }

  public void setEsPrivada(boolean esPrivada) {
    this.esPrivada = esPrivada;
  }
}
