package com.tallerwebi.dominio.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultadoPartida {

  private final List<JugadorResultado> jugadores;
  private final List<JugadorResultado> ganadores;
  private final JugadorResultado miResultado;
  private final String duracion;
  private final int rondasJugadas;
  private final int totalRondas;

  public ResultadoPartida(
    List<JugadorResultado> jugadores,
    String duracion,
    int rondasJugadas,
    int totalRondas
  ) {
    this.jugadores = Collections.unmodifiableList(new ArrayList<>(jugadores));
    this.ganadores = Collections.unmodifiableList(filtrarGanadores(jugadores));
    this.miResultado = buscarUsuarioActual(jugadores);
    this.duracion = duracion;
    this.rondasJugadas = rondasJugadas;
    this.totalRondas = totalRondas;
  }

  private static List<JugadorResultado> filtrarGanadores(List<JugadorResultado> jugadores) {
    List<JugadorResultado> resultado = new ArrayList<>();

    for (JugadorResultado jugador : jugadores) {
      if (jugador.isGanador()) {
        resultado.add(jugador);
      }
    }

    return resultado;
  }

  private static JugadorResultado buscarUsuarioActual(List<JugadorResultado> jugadores) {
    for (JugadorResultado jugador : jugadores) {
      if (jugador.isEsUsuarioActual()) {
        return jugador;
      }
    }

    return null;
  }

  public List<JugadorResultado> getJugadores() {
    return jugadores;
  }

  public List<JugadorResultado> getGanadores() {
    return ganadores;
  }

  public boolean isHayEmpate() {
    return ganadores.size() > 1;
  }

  public String getNombresGanadores() {
    List<String> nombres = new ArrayList<>();

    for (JugadorResultado ganador : ganadores) {
      nombres.add(ganador.getNombre());
    }

    return String.join(", ", nombres);
  }

  public JugadorResultado getMiResultado() {
    return miResultado;
  }

  public String getDuracion() {
    return duracion;
  }

  public int getRondasJugadas() {
    return rondasJugadas;
  }

  public int getTotalRondas() {
    return totalRondas;
  }
}
