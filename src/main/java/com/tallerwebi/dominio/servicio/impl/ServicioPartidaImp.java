package com.tallerwebi.dominio.servicio.impl;

import java.util.List;

import com.tallerwebi.dominio.enums.EstadoJugador;
import com.tallerwebi.dominio.enums.EstadoPartida;
import com.tallerwebi.dominio.enums.EstadoRonda;
import com.tallerwebi.dominio.enums.EstadoSala;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.ServicioSala;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioPartidaImp")
@Transactional
public class ServicioPartidaImp implements ServicioPartida {

  private final ServicioSala servicioSala;
  private final RepositorioPartida repositorioPartida;

  public ServicioPartidaImp(ServicioSala servicioSala, RepositorioPartida repositorioPartida) {
    this.servicioSala = servicioSala;
    this.repositorioPartida = repositorioPartida;
  }

  @Override
  public Partida iniciarPartida(String codigoSala, Usuario solicitante) {
    Sala sala = buscarSala(codigoSala);

    validarSalaPuedeIniciarse(sala);
    validarEsHost(sala, solicitante);

    cambiarSalaAEnCurso(sala);

    Partida partida = crearPartida(sala);
    crearPrimeraRonda(partida);

    repositorioPartida.guardar(partida);
    return partida;
  }

  @Override
  public Partida finalizarPartida(String codigoSala, Usuario solicitante) {
    Partida partida = buscarPartida(codigoSala);

    validarPartidaPuedeFinalizar(partida);
    validarEsHost(partida.getSala(), solicitante);

    cambiarEstadoPartida(partida);
    cambiarSalaAFinalizada(partida.getSala());

    repositorioPartida.guardar(partida);
    return partida;
  }

  @Override
  public Partida buscarPartidaPorId(Long id) {
    Partida partida = repositorioPartida.obtenerPorId(id);

    if (partida == null) {
      throw new PartidaNoEncontradaException("No existe la partida " + id);
    }

    return partida;
  }

  @Override
  public Partida buscarPartidaPorCodigoSala(String codigoSala) {
    return buscarPartida(codigoSala);
  }

  @Override
  public void marcarJugadorListo(Long idPartida, Usuario usuario) {
    Partida partida = buscarPartidaPorId(idPartida);
    SalaJugador jugador = buscarJugadorEnSala(partida.getSala(), usuario);

    jugador.setEstadoJugador(EstadoJugador.LISTO);
  }

  @Override
  public boolean estanTodosListos(Long idPartida) {
    Partida partida = buscarPartidaPorId(idPartida);
    List<SalaJugador> jugadores = partida.getSala().getJugadores();

    if (jugadores.isEmpty()) {
      return false;
    }

    for (SalaJugador jugador : jugadores) {
      if (jugador.getEstadoJugador() != EstadoJugador.LISTO) {
        return false;
      }
    }

    return true;
  }

  private Sala buscarSala(String codigoSala) {
    return servicioSala.buscarPorCodigo(codigoSala);
  }

  private Partida buscarPartida(String codigoSala) {
    Partida partida = repositorioPartida.obtenerPorCodigoSala(codigoSala);

    if (partida == null) {
      throw new PartidaNoEncontradaException("No hay una partida para la sala " + codigoSala);
    }

    return partida;
  }

  private Partida crearPartida(Sala sala) {
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    partida.setTotalRondas(sala.getTotalRondas());
    partida.setModoJuego(sala.getModoJuego());
    return partida;
  }

  private void crearPrimeraRonda(Partida partida) {
    PartidaRonda ronda = new PartidaRonda(partida, 1);
    ronda.setEstado(EstadoRonda.VOTACION_CATEGORIA);

    // La ronda es el lado dueño de la relación.
    ronda.setPartida(partida);
    partida.getRondas().add(ronda);
  }

  private void cambiarSalaAEnCurso(Sala sala) {
    sala.setEstado(EstadoSala.EN_CURSO);
  }

  private void cambiarSalaAFinalizada(Sala sala) {
    sala.setEstado(EstadoSala.FINALIZADA);
  }

  private void cambiarEstadoPartida(Partida partida) {
    partida.setEstado(EstadoPartida.FINALIZADA);
  }

  private void validarSalaPuedeIniciarse(Sala sala) {
    if (sala.getEstado() != EstadoSala.EN_ESPERA) {
      throw new SalaNoPuedeIniciarseException("La sala no está esperando jugadores");
    }

    if (sala.getJugadores().isEmpty()) {
      throw new SalaSinJugadoresException("No hay jugadores para iniciar la partida");
    }
  }

  private void validarPartidaPuedeFinalizar(Partida partida) {
    if (partida.getEstado() != EstadoPartida.EN_CURSO) {
      throw new IllegalStateException("La partida no se encuentra en curso");
    }
  }

  private void validarEsHost(Sala sala, Usuario solicitante) {
    if (sala.getHost() == null || !sala.getHost().getId().equals(solicitante.getId())) {
      throw new UsuarioNoEsHostException("Solo el host puede realizar esta acción");
    }
  }

  private SalaJugador buscarJugadorEnSala(Sala sala, Usuario usuario) {
    for (SalaJugador jugador : sala.getJugadores()) {
      if (jugador.getUsuario().getId().equals(usuario.getId())) {
        return jugador;
      }
    }

    throw new JugadorInexistenteExeption("El usuario no pertenece a la sala");
  }

  //Busca la respuesta de un jugador específico dentro de una ronda dada.
  /*
  public RespuestaJugador buscarRespuestaDeJugador(PartidaRonda partidaRonda, Long partidaJugadorId) {
    if (partidaRonda == null || partidaRonda.getRespuestas() == null || partidaJugadorId == null) {
      return null;
    }

    for (RespuestaJugador r : partidaRonda.getRespuestas()) {
      if (r.getPartidaJugador() != null && partidaJugadorId.equals(r.getPartidaJugador().getId())) {
        return r;
      }
    }
    return null;
  }
  */

  //Verifica si un jugador ya ha respondido en una ronda específica.
  /*
  public boolean haRespondido(PartidaRonda partidaRonda, Long partidaJugadorId) {
    return buscarRespuestaDeJugador(partidaRonda, partidaJugadorId) != null;
  }
  */

  //Obtiene el primer acierto (la respuesta correcta con menor tiempo en milisegundos) de la ronda.
  /*
  public RespuestaJugador obtenerPrimerAcierto(PartidaRonda partidaRonda) {
    if (partidaRonda == null || partidaRonda.getRespuestas() == null) {
      return null;
    }

    RespuestaJugador primerAcierto = null;
    for (RespuestaJugador r : partidaRonda.getRespuestas()) {
      if (r.isEsAcierto()) {
        if (primerAcierto == null || r.getTiempoRespuestaMs() < primerAcierto.getTiempoRespuestaMs()) {
          primerAcierto = r;
        }
      }
    }
    return primerAcierto;
  }
  */

  @Override
  public void sumarPuntaje(PartidaJugador jugador, int puntos) {
    if (jugador != null && puntos > 0) {
      jugador.setPuntajeAcumulado(jugador.getPuntajeAcumulado() + puntos);
    }
  }

  @Override
  public void registrarAcierto(PartidaJugador jugador) {
    if (jugador != null) {
      jugador.setAciertos(jugador.getAciertos() + 1);
      jugador.setRachaRespuestasPartida(jugador.getRachaRespuestasPartida() + 1);
    }
  }

  @Override
  public void registrarFallo(PartidaJugador jugador) {
    if (jugador != null) {
      jugador.setErrores(jugador.getErrores() + 1);
      jugador.setRachaRespuestasPartida(0);
    }
  }

  /*
  public PartidaJugadorComodin buscarComodin(PartidaJugador jugador, Long comodinId) {
    if (jugador == null || jugador.getComodines() == null || comodinId == null) {
      return null;
    }

    for (PartidaJugadorComodin pjc : jugador.getComodines()) {
      if (pjc.getComodin() != null && comodinId.equals(pjc.getComodin().getId())) {
        return pjc;
      }
    }
    return null;
  }
  */

  /**
   * Verifica si un comodín del jugador tiene cantidad disponible para ser usado.
   */
  @Override
  public boolean puedeUsarse(PartidaJugadorComodin comodinJugador) {
    if (comodinJugador == null) {
      return false;
    }
    return comodinJugador.getCantidadDisponible() > 0;
  }

  /**
   * Intenta consumir un comodín del jugador, reduciendo su disponibilidad y sumando un uso.
   */
  @Override
  public boolean consumirComodin(PartidaJugadorComodin comodinJugador) {
    if (puedeUsarse(comodinJugador)) {
      comodinJugador.setCantidadDisponible(comodinJugador.getCantidadDisponible() - 1);
      comodinJugador.setVecesUsado(comodinJugador.getVecesUsado() + 1);
      return true;
    }
    return false;
  }
}
