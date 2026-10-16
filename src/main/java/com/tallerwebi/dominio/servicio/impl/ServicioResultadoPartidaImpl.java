package com.tallerwebi.dominio.servicio.impl;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.enumeration.EstadoRonda;
import com.tallerwebi.dominio.enumeration.EstadoSala;
import com.tallerwebi.dominio.excepcion.PartidaNoEncontradaException;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.ServicioResultadoPartida;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// NUEVO: único lugar donde se cierra una partida (cierre normal y cierre anticipado del host).
@Service("servicioResultadoPartida")
@Transactional
public class ServicioResultadoPartidaImpl implements ServicioResultadoPartida {

  private static final String EVENTO_PARTIDA_FINALIZADA = "PARTIDA_FINALIZADA";

  /**
   * Orden del ranking: más puntaje, luego más aciertos, luego mejor racha (de mayor a menor).
   */
  private static final Comparator<PartidaJugador> RANKING = Comparator
    .comparingInt(PartidaJugador::getPuntajeAcumulado)
    .thenComparingInt(PartidaJugador::getAciertos)
    .thenComparingInt(PartidaJugador::getMejorRachaPartida)
    .reversed();

  private final RepositorioPartida repositorioPartida;
  private final RepositorioPartidaJugador repositorioPartidaJugador;
  private final NotificadorSala notificadorSala;

  public ServicioResultadoPartidaImpl(
    RepositorioPartida repositorioPartida,
    RepositorioPartidaJugador repositorioPartidaJugador,
    NotificadorSala notificadorSala
  ) {
    this.repositorioPartida = repositorioPartida;
    this.repositorioPartidaJugador = repositorioPartidaJugador;
    this.notificadorSala = notificadorSala;
  }

  @Override
  public void cerrarPartida(Long partidaId) {
    Partida partida = buscarPartida(partidaId);

    if (partida.getEstado() == EstadoPartida.FINALIZADA) {
      return; // idempotente: si ya se cerró, no se vuelve a calcular ni notificar
    }

    asignarPosiciones(repositorioPartidaJugador.obtenerPorPartida(partidaId));
    marcarComoFinalizada(partida);

    notificadorSala.rondaActualizada(
      partida.getSala().getCodigo(),
      partida.getId(),
      EVENTO_PARTIDA_FINALIZADA
    );
  }

  @Override
  public ResultadoPartida obtenerResultado(Long partidaId, Long usuarioId) {
    Partida partida = buscarPartida(partidaId);

    if (partida.getEstado() != EstadoPartida.FINALIZADA) {
      throw new IllegalStateException("La partida todavía no finalizó");
    }

    List<PartidaJugador> jugadores = repositorioPartidaJugador.obtenerPorPartida(partidaId);

    validarQueParticipo(jugadores, usuarioId);
    completarPosicionesFaltantes(jugadores);

    return new ResultadoPartida(
      armarRanking(jugadores, usuarioId),
      calcularDuracion(partida),
      partida.getRondaActual(),
      partida.getTotalRondas()
    );
  }

  private Partida buscarPartida(Long partidaId) {
    Partida partida = repositorioPartida.obtenerPorIdConRondas(partidaId);

    if (partida == null) {
      throw new PartidaNoEncontradaException("No existe la partida " + partidaId);
    }

    return partida;
  }

  /**
   * Posiciones tipo competencia: los empatados comparten posición (1, 1, 3...).
   */
  private void asignarPosiciones(List<PartidaJugador> jugadores) {
    List<PartidaJugador> ordenados = new ArrayList<>(jugadores);
    ordenados.sort(RANKING);

    int posicion = 0;

    for (int i = 0; i < ordenados.size(); i++) {
      PartidaJugador actual = ordenados.get(i);

      if (i == 0 || RANKING.compare(ordenados.get(i - 1), actual) != 0) {
        posicion = i + 1;
      }

      actual.setPosicionFinal(posicion);
    }
  }

  private void marcarComoFinalizada(Partida partida) {
    LocalDateTime ahora = LocalDateTime.now();

    partida.setEstado(EstadoPartida.FINALIZADA);
    partida.setFechaFin(ahora);
    partida.getSala().setEstado(EstadoSala.FINALIZADA);

    PartidaRonda ronda = partida.obtenerRondaActual();

    if (ronda != null && ronda.getEstado() != EstadoRonda.FINALIZADA) {
      ronda.setEstado(EstadoRonda.FINALIZADA);
      ronda.setFechaFin(ahora);
    }
  }

  private void validarQueParticipo(List<PartidaJugador> jugadores, Long usuarioId) {
    for (PartidaJugador jugador : jugadores) {
      if (jugador.getUsuario().getId().equals(usuarioId)) {
        return;
      }
    }

    throw new IllegalArgumentException("El usuario no participa en esta partida.");
  }

  /**
   * Cubre partidas que se cerraron antes de existir este cálculo (posicionFinal nula).
   */
  private void completarPosicionesFaltantes(List<PartidaJugador> jugadores) {
    for (PartidaJugador jugador : jugadores) {
      if (jugador.getPosicionFinal() == null) {
        asignarPosiciones(jugadores);
        return;
      }
    }
  }

  private List<JugadorResultado> armarRanking(List<PartidaJugador> jugadores, Long usuarioId) {
    List<PartidaJugador> ordenados = new ArrayList<>(jugadores);

    ordenados.sort(Comparator.comparing(PartidaJugador::getPosicionFinal).thenComparing(RANKING));

    List<JugadorResultado> ranking = new ArrayList<>();

    for (PartidaJugador jugador : ordenados) {
      ranking.add(
        new JugadorResultado(
          jugador.getPosicionFinal(),
          jugador.getUsuario().getUsername(),
          jugador.getPuntajeAcumulado(),
          jugador.getAciertos(),
          jugador.getErrores(),
          jugador.getMejorRachaPartida(),
          jugador.getUsuario().getId().equals(usuarioId)
        )
      );
    }

    return ranking;
  }

  private String calcularDuracion(Partida partida) {
    if (partida.getFechaInicio() == null || partida.getFechaFin() == null) {
      return "-";
    }

    Duration duracion = Duration.between(partida.getFechaInicio(), partida.getFechaFin());

    return duracion.toMinutes() + " min " + duracion.toSecondsPart() + " s";
  }
}
