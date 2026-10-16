package com.tallerwebi.dominio.servicio.impl;

import com.tallerwebi.dominio.enumeration.EstadoJugador;
import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.enumeration.EstadoRonda;
import com.tallerwebi.dominio.enumeration.EstadoSala;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.ServicioResultadoPartida;
import com.tallerwebi.dominio.servicio.ServicioRonda;
import com.tallerwebi.dominio.servicio.ServicioSala;
import com.tallerwebi.dominio.servicio.ServicioVotacionCategoria;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioPartidaImp")
@Transactional
public class ServicioPartidaImp implements ServicioPartida {

  private final ServicioSala servicioSala;
  private final RepositorioPartida repositorioPartida;
  private final ServicioVotacionCategoria servicioVotacionCategoria;
  private final RepositorioPartidaJugador repositorioPartidaJugador;
  private final ServicioRonda servicioRonda;
  private final ServicioResultadoPartida servicioResultadoPartida;

  public ServicioPartidaImp(
    ServicioSala servicioSala,
    RepositorioPartida repositorioPartida,
    ServicioVotacionCategoria servicioVotacionCategoria,
    RepositorioPartidaJugador repositorioPartidaJugador,
    ServicioRonda servicioRonda,
    ServicioResultadoPartida servicioResultadoPartida
  ) {
    this.servicioSala = servicioSala;
    this.repositorioPartida = repositorioPartida;
    this.servicioVotacionCategoria = servicioVotacionCategoria;
    this.repositorioPartidaJugador = repositorioPartidaJugador;
    this.servicioRonda = servicioRonda;
    this.servicioResultadoPartida = servicioResultadoPartida;
  }

  private void crearJugadoresDePartida(Partida partida, Sala sala) {
    for (SalaJugador jugadorSala : sala.getJugadores()) {
      Usuario usuario = jugadorSala.getUsuario();

      PartidaJugador jugadorPartida = new PartidaJugador(partida, usuario);

      repositorioPartidaJugador.guardar(jugadorPartida);
    }
  }

  // INICIO Y FINALIZACIÓN DE PARTIDAS

  @Override
  public Partida iniciarPartida(String codigoSala, Usuario solicitante) {
    Sala sala = buscarSala(codigoSala);

    validarEsHost(sala, solicitante);

    Partida existente = repositorioPartida.obtenerPorCodigoSala(codigoSala);

    if (existente != null) {
      return existente;
    }

    validarSalaPuedeIniciarse(sala);
    cambiarSalaAEnCurso(sala);

    Partida partida = crearPartida(sala);

    crearPrimeraRonda(partida);
    partida.iniciarVotacionCategoria();

    repositorioPartida.guardar(partida);

    crearJugadoresDePartida(partida, sala);

    return partida;
  }

  @Override
  public Partida finalizarPartida(String codigoSala, Usuario solicitante) {
    Partida partida = buscarPartida(codigoSala);

    validarPartidaPuedeFinalizar(partida);
    validarEsHost(partida.getSala(), solicitante);

    servicioResultadoPartida.cerrarPartida(partida.getId());

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
    ronda.setPartida(partida);

    partida.getRondas().add(ronda);
  }

  private void cambiarSalaAEnCurso(Sala sala) {
    sala.setEstado(EstadoSala.EN_CURSO);
  }

  // BÚSQUEDAS

  @Override
  public Partida buscarPartidaPorId(Long id) {
    Partida partida = repositorioPartida.obtenerPorIdConRondas(id);

    if (partida == null) {
      throw new PartidaNoEncontradaException("No existe la partida " + id);
    }

    return partida;
  }

  @Override
  public Partida buscarPartidaPorCodigoSala(String codigoSala) {
    return buscarPartida(codigoSala);
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

  // VALIDACIONES

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
    if (
      solicitante == null ||
      solicitante.getId() == null ||
      sala.getHost() == null ||
      sala.getHost().getId() == null ||
      !sala.getHost().getId().equals(solicitante.getId())
    ) {
      throw new UsuarioNoEsHostException("Solo el host puede realizar esta acción");
    }
  }

  // ESTADO DE LOS JUGADORES

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

  private SalaJugador buscarJugadorEnSala(Sala sala, Usuario usuario) {
    if (usuario == null || usuario.getId() == null) {
      throw new JugadorInexistenteExeption("El usuario no pertenece a la sala");
    }

    return buscarJugadorPorIdEnSala(sala, usuario.getId());
  }

  private SalaJugador buscarJugadorPorIdEnSala(Sala sala, Long usuarioId) {
    for (SalaJugador jugador : sala.getJugadores()) {
      if (jugador.getUsuario().getId().equals(usuarioId)) {
        return jugador;
      }
    }

    throw new JugadorInexistenteExeption("El usuario no pertenece a la sala");
  }

  // PUNTAJES Y ESTADÍSTICAS

  @Override
  public void sumarPuntaje(PartidaJugador jugador, int puntos) {
    if (jugador != null && puntos > 0) {
      jugador.getEstadisticas().sumarPuntaje(puntos);
    }
  }

  @Override
  public void registrarAcierto(PartidaJugador jugador) {
    if (jugador != null) {
      jugador.getEstadisticas().registrarAcierto();
    }
  }

  @Override
  public void registrarFallo(PartidaJugador jugador) {
    if (jugador != null) {
      jugador.getEstadisticas().registrarFallo();
    }
  }

  // COMODINES

  @Override
  public boolean puedeUsarse(PartidaJugadorComodin comodinJugador) {
    return comodinJugador != null && comodinJugador.getCantidadDisponible() > 0;
  }

  @Override
  public boolean consumirComodin(PartidaJugadorComodin comodinJugador) {
    if (!puedeUsarse(comodinJugador)) {
      return false;
    }

    comodinJugador.setCantidadDisponible(comodinJugador.getCantidadDisponible() - 1);

    comodinJugador.setVecesUsado(comodinJugador.getVecesUsado() + 1);

    return true;
  }

  // RONDAS: DELEGACIÓN

  @Override
  public boolean finalizarRondaSiTodosRespondieron(Long partidaId) {
    return servicioRonda.finalizarRondaSiTodosRespondieron(partidaId);
  }

  @Override
  public void avanzarRonda(Long partidaId, Long usuarioId) {
    servicioRonda.avanzarRonda(partidaId, usuarioId);
  }

  // CONSULTAS PARA LA VISTA (se resuelven dentro de la transacción del servicio)

  @Override
  public boolean usuarioYaVoto(Long partidaId, Long usuarioId) {
    VotacionCategoria votacion = buscarPartidaPorId(partidaId).getVotacionCategoria();

    return votacion != null && votacion.yaVoto(usuarioId);
  }

  @Override
  public boolean esHost(Long partidaId, Long usuarioId) {
    Sala sala = buscarPartidaPorId(partidaId).getSala();

    return sala.getHost() != null && sala.getHost().getId().equals(usuarioId);
  }

  // VOTACIÓN DE CATEGORÍAS: DELEGACIÓN

  @Override
  public boolean registrarVotoCategoria(Long partidaId, Long usuarioId, Long categoriaId) {
    return servicioVotacionCategoria.registrarVotoCategoria(partidaId, usuarioId, categoriaId);
  }

  @Override
  public void finalizarVotacionCategoria(Long partidaId, Long usuarioId) {
    servicioVotacionCategoria.finalizarVotacionCategoria(partidaId, usuarioId);
  }
}
