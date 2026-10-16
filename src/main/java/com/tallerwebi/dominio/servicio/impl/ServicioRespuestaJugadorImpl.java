package com.tallerwebi.dominio.servicio.impl;

import com.tallerwebi.dominio.excepcion.RespuestaDuplicadaException;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.PartidaRonda;
import com.tallerwebi.dominio.modelo.Pregunta;
import com.tallerwebi.dominio.modelo.RespuestaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioOpcion;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioRespuestaJugador;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.ServicioRespuestaJugador;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioRespuestaJugadorImpl implements ServicioRespuestaJugador {

  private final ServicioPartida servicioPartida;
  private final RepositorioOpcion repositorioOpcion;
  private final RepositorioPartidaJugador repositorioPartidaJugador;
  private final RepositorioRespuestaJugador repositorioRespuestaJugador;

  public ServicioRespuestaJugadorImpl(
    ServicioPartida servicioPartida,
    RepositorioOpcion repositorioOpcion,
    RepositorioPartidaJugador repositorioPartidaJugador,
    RepositorioRespuestaJugador repositorioRespuestaJugador
  ) {
    this.servicioPartida = servicioPartida;
    this.repositorioOpcion = repositorioOpcion;
    this.repositorioPartidaJugador = repositorioPartidaJugador;
    this.repositorioRespuestaJugador = repositorioRespuestaJugador;
  }

  @Override
  public boolean registrarRespuesta(Long partidaId, Long usuarioId, Long opcionId) {
    Partida partida = obtenerPartidaValida(partidaId);
    PartidaRonda ronda = obtenerRondaActual(partida);
    Pregunta pregunta = ronda.getPregunta();
    Opcion opcion = obtenerOpcionValida(opcionId, pregunta);
    PartidaJugador jugador = obtenerJugadorValido(partidaId, usuarioId);

    boolean yaRespondio = repositorioRespuestaJugador.existeRespuestaEnRonda(
      ronda.getId(),
      jugador.getId()
    );

    if (yaRespondio) {
      throw new RespuestaDuplicadaException();
    }

    boolean esCorrecta = opcion.getEsCorrecta();

    RespuestaJugador respuesta = new RespuestaJugador(
      jugador,
      ronda,
      pregunta,
      opcion,
      esCorrecta,
      LocalDateTime.now()
    );

    repositorioRespuestaJugador.guardar(respuesta);

    if (esCorrecta) {
      servicioPartida.registrarAcierto(jugador);
      servicioPartida.sumarPuntaje(jugador, 100);
    } else {
      servicioPartida.registrarFallo(jugador);
    }

    servicioPartida.finalizarRondaSiTodosRespondieron(partidaId);

    return esCorrecta;
  }

  @Override
  public boolean yaRespondio(Long partidaId, Long usuarioId) {
    Partida partida = obtenerPartidaValida(partidaId);
    PartidaRonda ronda = obtenerRondaActual(partida);
    PartidaJugador jugador = obtenerJugadorValido(partidaId, usuarioId);

    return repositorioRespuestaJugador.existeRespuestaEnRonda(ronda.getId(), jugador.getId());
  }

  @Override
  public Boolean obtenerResultado(Long partidaId, Long usuarioId) {
    Partida partida = obtenerPartidaValida(partidaId);
    PartidaRonda ronda = obtenerRondaActual(partida);
    PartidaJugador jugador = obtenerJugadorValido(partidaId, usuarioId);

    RespuestaJugador respuesta = repositorioRespuestaJugador.buscarRespuestaEnRonda(
      ronda.getId(),
      jugador.getId()
    );

    return respuesta == null ? null : respuesta.getEsCorrecta();
  }

  private Partida obtenerPartidaValida(Long partidaId) {
    Partida partida = servicioPartida.buscarPartidaPorId(partidaId);

    if (partida == null) {
      throw new IllegalArgumentException("La partida no existe.");
    }

    return partida;
  }

  private PartidaRonda obtenerRondaActual(Partida partida) {
    PartidaRonda ronda = partida.obtenerRondaActual();

    if (ronda == null || ronda.getPregunta() == null) {
      throw new IllegalStateException("La partida no tiene una pregunta activa.");
    }

    return ronda;
  }

  private Opcion obtenerOpcionValida(Long opcionId, Pregunta pregunta) {
    Opcion opcion = repositorioOpcion.obtenerPorId(opcionId);

    if (opcion == null) {
      throw new IllegalArgumentException("La opción seleccionada no existe.");
    }

    if (
      opcion.getPregunta() == null ||
      !pregunta.getIdentificador().equals(opcion.getPregunta().getIdentificador())
    ) {
      throw new IllegalArgumentException("La opción no pertenece a la pregunta actual.");
    }

    return opcion;
  }

  private PartidaJugador obtenerJugadorValido(Long partidaId, Long usuarioId) {
    PartidaJugador jugador = repositorioPartidaJugador.obtenerPorPartidaYUsuario(
      partidaId,
      usuarioId
    );

    if (jugador == null) {
      throw new IllegalArgumentException("El usuario no participa en esta partida.");
    }

    return jugador;
  }
}
