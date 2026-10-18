package com.tallerwebi.dominio.servicio.impl;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.enumeration.EstadoRonda;
import com.tallerwebi.dominio.enumeration.EstadoSala;
import com.tallerwebi.dominio.excepcion.PartidaNoEncontradaException;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.PartidaRonda;
import com.tallerwebi.dominio.modelo.VotacionCategoria;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioRespuestaJugador;
import com.tallerwebi.dominio.servicio.ServicioRonda;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ciclo de vida de una ronda:
 * VOTACION_CATEGORIA -> PREGUNTA_EN_CURSO -> RESULTADO_PREGUNTA -> (siguiente ronda | fin de partida).
 */
@Service("servicioRonda")
@Transactional
public class ServicioRondaImpl implements ServicioRonda {

  private static final String EVENTO_PARTIDA_FINALIZADA = "PARTIDA_FINALIZADA";
  private static final String EVENTO_NUEVA_VOTACION = "NUEVA_VOTACION_CATEGORIA";

  private final RepositorioPartida repositorioPartida;
  private final RepositorioPartidaJugador repositorioPartidaJugador;
  private final RepositorioRespuestaJugador repositorioRespuestaJugador;
  private final NotificadorSala notificadorSala;

  public ServicioRondaImpl(
    RepositorioPartida repositorioPartida,
    RepositorioPartidaJugador repositorioPartidaJugador,
    RepositorioRespuestaJugador repositorioRespuestaJugador,
    NotificadorSala notificadorSala
  ) {
    this.repositorioPartida = repositorioPartida;
    this.repositorioPartidaJugador = repositorioPartidaJugador;
    this.repositorioRespuestaJugador = repositorioRespuestaJugador;
    this.notificadorSala = notificadorSala;
  }

  @Override
  public boolean finalizarRondaSiTodosRespondieron(Long partidaId) {
    Partida partida = buscarPartidaValida(partidaId);
    PartidaRonda ronda = partida.obtenerRondaActual();

    if (
      ronda == null ||
      ronda.getEstado() != EstadoRonda.PREGUNTA_EN_CURSO ||
      ronda.getPregunta() == null
    ) {
      return false;
    }

    List<PartidaJugador> jugadores = repositorioPartidaJugador.obtenerPorPartida(partidaId);

    if (!todosRespondieron(ronda, jugadores)) {
      return false;
    }

    ronda.setEstado(EstadoRonda.RESULTADO_PREGUNTA);
    notificadorSala.rondaResultado(partida.getSala().getCodigo(), partida.getId());

    return true;
  }

  @Override
  public void avanzarRonda(Long partidaId, Long usuarioId) {
    Partida partida = buscarPartidaValida(partidaId);

    validarEsHost(partida, usuarioId);

    PartidaRonda ronda = obtenerRondaActiva(partida);

    if (ronda.getEstado() != EstadoRonda.RESULTADO_PREGUNTA) {
      return; // idempotente: si ya avanzó, no hace nada
    }

    ronda.setEstado(EstadoRonda.FINALIZADA);

    if (esUltimaRonda(partida)) {
      finalizarPartida(partida);
      return;
    }

    avanzarARondaSiguiente(partida);
  }

  private Partida buscarPartidaValida(Long partidaId) {
    Partida partida = repositorioPartida.obtenerPorIdConRondas(partidaId);

    if (partida == null) {
      throw new PartidaNoEncontradaException("No existe la partida " + partidaId);
    }

    return partida;
  }

  private void validarEsHost(Partida partida, Long usuarioId) {
    if (!partida.getSala().getHost().getId().equals(usuarioId)) {
      throw new IllegalStateException("Solo el anfitrión puede avanzar de ronda");
    }
  }

  private PartidaRonda obtenerRondaActiva(Partida partida) {
    PartidaRonda ronda = partida.obtenerRondaActual();

    if (ronda == null || ronda.getPregunta() == null) {
      throw new IllegalStateException("La partida no tiene una pregunta activa.");
    }

    return ronda;
  }

  private boolean todosRespondieron(PartidaRonda ronda, List<PartidaJugador> jugadores) {
    if (jugadores.isEmpty()) {
      return false;
    }

    for (PartidaJugador jugador : jugadores) {
      if (!repositorioRespuestaJugador.existeRespuestaEnRonda(ronda.getId(), jugador.getId())) {
        return false;
      }
    }

    return true;
  }

  private boolean esUltimaRonda(Partida partida) {
    return partida.getRondaActual() >= partida.getTotalRondas();
  }

  private void finalizarPartida(Partida partida) {
    partida.setEstado(EstadoPartida.FINALIZADA);
    partida.setFechaFin(LocalDateTime.now());
    partida.getSala().setEstado(EstadoSala.FINALIZADA);

    notificadorSala.rondaActualizada(
      partida.getSala().getCodigo(),
      partida.getId(),
      EVENTO_PARTIDA_FINALIZADA
    );
  }

  private void avanzarARondaSiguiente(Partida partida) {
    int numeroSiguiente = partida.getRondaActual() + 1;

    PartidaRonda siguiente = new PartidaRonda(partida, numeroSiguiente);
    siguiente.setEstado(EstadoRonda.VOTACION_CATEGORIA);

    partida.getRondas().add(siguiente);
    partida.setRondaActual(numeroSiguiente);

    reiniciarVotacionCategoria(partida);

    notificadorSala.rondaActualizada(
      partida.getSala().getCodigo(),
      partida.getId(),
      EVENTO_NUEVA_VOTACION
    );
  }

  private void reiniciarVotacionCategoria(Partida partida) {
    VotacionCategoria votacion = partida.getVotacionCategoria();

    if (votacion == null) {
      partida.iniciarVotacionCategoria();
      return;
    }

    votacion.reiniciar();
  }
}
