package com.tallerwebi.dominio.servicio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.enumeration.EstadoRonda;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioVotacionCategoria")
@Transactional
public class ServicioVotacionCategoriaImpl implements ServicioVotacionCategoria {

  private final RepositorioPartida repositorioPartida;
  private final ServicioPartidaPregunta servicioPartidaPregunta;
  private final NotificadorSala notificadorSala;

  public ServicioVotacionCategoriaImpl(
    RepositorioPartida repositorioPartida,
    ServicioPartidaPregunta servicioPartidaPregunta,
    NotificadorSala notificadorSala
  ) {
    this.repositorioPartida = repositorioPartida;
    this.servicioPartidaPregunta = servicioPartidaPregunta;
    this.notificadorSala = notificadorSala;
  }

  @Override
  public boolean registrarVotoCategoria(Long partidaId, Long usuarioId, Long categoriaId) {
    Partida partida = buscarPartidaPorId(partidaId);

    validarVotacionAbierta(partida);

    SalaJugador jugador = buscarJugadorPorIdEnSala(partida.getSala(), usuarioId);

    Categoria categoria = buscarCategoriaPorId(categoriaId);

    VotacionCategoria votacion = partida.getVotacionCategoria();

    validarQueNoHayaVotado(votacion, usuarioId);

    VotoCategoria voto = new VotoCategoria(jugador.getUsuario(), categoria);

    votacion.agregarVoto(voto);

    int cantidadJugadores = partida.getSala().getJugadores().size();

    return votacion.todosVotaron(cantidadJugadores);
  }

  @Override
  public void finalizarVotacionCategoria(Long partidaId, Long usuarioId) {
    Partida partida = buscarPartidaPorId(partidaId);

    validarEsHost(partida, usuarioId);
    validarVotacionAbierta(partida);

    VotacionCategoria votacion = partida.getVotacionCategoria();

    validarQueTodosHayanVotado(partida, votacion);

    Categoria categoriaGanadora = obtenerCategoriaGanadora(votacion);

    Pregunta pregunta = obtenerPreguntaDeCategoria(categoriaGanadora);

    PartidaRonda ronda = buscarRondaActual(partida);

    actualizarRonda(ronda, pregunta, categoriaGanadora, votacion);

    notificarVotacionFinalizada(partida);
  }

  private Partida buscarPartidaPorId(Long partidaId) {
    Partida partida = repositorioPartida.obtenerPorId(partidaId);

    if (partida == null) {
      throw new IllegalArgumentException("No existe una partida con el identificador indicado");
    }

    return partida;
  }

  private void validarVotacionAbierta(Partida partida) {
    PartidaRonda ronda = partida.obtenerRondaActual();

    if (ronda == null || ronda.getEstado() != EstadoRonda.VOTACION_CATEGORIA) {
      throw new IllegalStateException("La votación de categoría no está abierta");
    }

    if (partida.getVotacionCategoria() == null) {
      throw new IllegalStateException("La partida no tiene una votación de categoría activa");
    }
  }

  private SalaJugador buscarJugadorPorIdEnSala(Sala sala, Long usuarioId) {
    for (SalaJugador jugador : sala.getJugadores()) {
      if (jugador.getUsuario().getId().equals(usuarioId)) {
        return jugador;
      }
    }

    throw new IllegalArgumentException("El usuario no pertenece a esta sala");
  }

  private Categoria buscarCategoriaPorId(Long categoriaId) {
    Map<Long, Categoria> categorias = servicioPartidaPregunta.obtenerCategorias();

    Categoria categoria = categorias.get(categoriaId);

    if (categoria == null) {
      throw new IllegalArgumentException("No existe la categoría indicada");
    }

    return categoria;
  }

  private void validarQueNoHayaVotado(VotacionCategoria votacion, Long usuarioId) {
    if (votacion.yaVoto(usuarioId)) {
      throw new IllegalStateException("El usuario ya votó una categoría");
    }
  }

  private void validarEsHost(Partida partida, Long usuarioId) {
    Long idHost = partida.getSala().getHost().getId();

    if (!idHost.equals(usuarioId)) {
      throw new IllegalStateException("Solo el anfitrión puede finalizar la votación");
    }
  }

  private void validarQueTodosHayanVotado(Partida partida, VotacionCategoria votacion) {
    int cantidadJugadores = partida.getSala().getJugadores().size();

    if (!votacion.todosVotaron(cantidadJugadores)) {
      throw new IllegalStateException("No se puede finalizar la votación: faltan votos");
    }
  }

  private Categoria obtenerCategoriaGanadora(VotacionCategoria votacion) {
    Map<Long, Integer> cantidadVotosPorCategoria = new HashMap<>();
    Map<Long, Categoria> categoriasPorId = new HashMap<>();

    contarVotos(votacion, cantidadVotosPorCategoria, categoriasPorId);

    if (cantidadVotosPorCategoria.isEmpty()) {
      throw new IllegalStateException("No hay votos para determinar la categoría ganadora");
    }

    int maximoVotos = Collections.max(cantidadVotosPorCategoria.values());

    List<Categoria> categoriasEmpatadas = obtenerCategoriasEmpatadas(
      cantidadVotosPorCategoria,
      categoriasPorId,
      maximoVotos
    );

    return elegirCategoriaEmpatada(categoriasEmpatadas);
  }

  private void contarVotos(
    VotacionCategoria votacion,
    Map<Long, Integer> cantidadVotosPorCategoria,
    Map<Long, Categoria> categoriasPorId
  ) {
    for (VotoCategoria voto : votacion.getVotosCategoria()) {
      Categoria categoria = voto.getCategoria();
      Long categoriaId = categoria.getId();

      categoriasPorId.put(categoriaId, categoria);

      int cantidadActual = cantidadVotosPorCategoria.getOrDefault(categoriaId, 0);

      cantidadVotosPorCategoria.put(categoriaId, cantidadActual + 1);
    }
  }

  private List<Categoria> obtenerCategoriasEmpatadas(
    Map<Long, Integer> cantidadVotosPorCategoria,
    Map<Long, Categoria> categoriasPorId,
    int maximoVotos
  ) {
    List<Categoria> categoriasEmpatadas = new ArrayList<>();

    for (Map.Entry<Long, Integer> entrada : cantidadVotosPorCategoria.entrySet()) {
      if (entrada.getValue() == maximoVotos) {
        Categoria categoria = categoriasPorId.get(entrada.getKey());

        categoriasEmpatadas.add(categoria);
      }
    }

    return categoriasEmpatadas;
  }

  private Categoria elegirCategoriaEmpatada(List<Categoria> categoriasEmpatadas) {
    Collections.shuffle(categoriasEmpatadas);

    return categoriasEmpatadas.get(0);
  }

  private Pregunta obtenerPreguntaDeCategoria(Categoria categoria) {
    Pregunta pregunta = servicioPartidaPregunta.obtenerPreguntaPorCategoria(categoria.getId());

    if (pregunta == null) {
      throw new IllegalStateException("No hay preguntas disponibles para la categoría elegida");
    }

    return pregunta;
  }

  private PartidaRonda buscarRondaActual(Partida partida) {
    PartidaRonda ronda = partida.obtenerRondaActual();

    if (ronda == null) {
      throw new IllegalStateException("No existe una ronda actual para esta partida");
    }

    return ronda;
  }

  private void actualizarRonda(
    PartidaRonda ronda,
    Pregunta pregunta,
    Categoria categoriaGanadora,
    VotacionCategoria votacion
  ) {
    votacion.setCategoriaElegida(categoriaGanadora);
    ronda.setPregunta(pregunta);
    ronda.setEstado(EstadoRonda.PREGUNTA_EN_CURSO);
  }

  private void notificarVotacionFinalizada(Partida partida) {
    notificadorSala.votacionCategoriaFinalizada(partida.getSala().getCodigo(), partida.getId());
  }
}
