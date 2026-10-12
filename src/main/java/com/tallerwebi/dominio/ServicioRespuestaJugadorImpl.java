package com.tallerwebi.dominio;

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
    Pregunta pregunta = obtenerPreguntaActual(partida);
    Opcion opcion = obtenerOpcionValida(opcionId, pregunta);
    PartidaJugador jugador = obtenerJugadorValido(partidaId, usuarioId);

    boolean esCorrecta = opcion.getEsCorrecta();

    RespuestaJugador respuesta = new RespuestaJugador(
      jugador,
      pregunta,
      opcion,
      esCorrecta,
      LocalDateTime.now()
    );

    repositorioRespuestaJugador.guardar(respuesta);

    return esCorrecta;
  }

  private Partida obtenerPartidaValida(Long partidaId) {
    Partida partida = servicioPartida.buscarPartidaPorId(partidaId);

    if (partida == null) {
      throw new IllegalArgumentException("La partida no existe.");
    }

    return partida;
  }

  private Pregunta obtenerPreguntaActual(Partida partida) {
    PartidaRonda ronda = partida.obtenerRondaActual();

    if (ronda == null || ronda.getPregunta() == null) {
      throw new IllegalStateException("La partida no tiene una pregunta activa.");
    }

    return ronda.getPregunta();
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
