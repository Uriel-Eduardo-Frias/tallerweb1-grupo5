package com.tallerwebi.dominio.servicio.impl;

import com.tallerwebi.dominio.enumeration.TipoComodin;
import com.tallerwebi.dominio.modelo.Comodin;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.PartidaJugadorComodin;
import com.tallerwebi.dominio.modelo.PartidaRonda;
import com.tallerwebi.dominio.repositorio.RepositorioComodin;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.ServicioComodin;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import com.tallerwebi.presentacion.dto.Comodin5050DTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioComodin")
@Transactional
public class ServicioComodinImpl implements ServicioComodin {

  private static final int NIVEL_INICIAL = 1;
  private static final int NIVEL_INTERMEDIO = 2;
  private static final int NIVEL_AVANZADO = 3;

  private static final int SEGUNDOS_TIEMPO_EXTRA = 10;
  private static final int PENALIZACION_CINCUENTA_CINCUENTA = 5;
  private static final int CANTIDAD_OPCIONES_DESCARTADAS = 2;
  private static final int CANTIDAD_MINIMA_DISPONIBLE = 0;
  private static final int STOCK_INICIAL_COMODIN = 1;

  private final RepositorioComodin repositorioComodin;
  private final RepositorioPartida repositorioPartida;
  private final RepositorioPartidaJugador repositorioPartidaJugador;
  private final ServicioEstadistica servicioEstadistica;

  @Autowired
  public ServicioComodinImpl(
    RepositorioComodin repositorioComodin,
    RepositorioPartida repositorioPartida,
    RepositorioPartidaJugador repositorioPartidaJugador,
    ServicioEstadistica servicioEstadistica
  ) {
    this.repositorioComodin = repositorioComodin;
    this.repositorioPartida = repositorioPartida;
    this.repositorioPartidaJugador = repositorioPartidaJugador;
    this.servicioEstadistica = servicioEstadistica;
  }

  @Override
  public int obtenerNivelJugador(Long usuarioId) {
    if (usuarioId == null || servicioEstadistica == null) {
      return NIVEL_INICIAL;
    }
    return servicioEstadistica.calcularNivelUsuario(usuarioId);
  }

  private boolean esComodinPermitidoParaJugador(PartidaJugador jugador, TipoComodin tipo) {
    Long usuarioId = (jugador != null && jugador.getUsuario() != null)
      ? jugador.getUsuario().getId()
      : null;

    int nivel = obtenerNivelJugador(usuarioId);

    if (tipo == TipoComodin.TIEMPO_EXTRA) {
      return true;
    }
    if (tipo == TipoComodin.DOBLE_CHANCE) {
      return nivel >= NIVEL_INTERMEDIO;
    }
    if (tipo == TipoComodin.CINCUENTA_CINCUENTA) {
      return nivel >= NIVEL_AVANZADO;
    }
    return false;
  }

  @Override
  public void inicializarComodinesParaJugador(PartidaJugador jugador) {
    List<TipoComodin> permitidos = new ArrayList<>();
    permitidos.add(TipoComodin.TIEMPO_EXTRA);

    Long usuarioId = (jugador != null && jugador.getUsuario() != null)
      ? jugador.getUsuario().getId()
      : null;

    int nivel = obtenerNivelJugador(usuarioId);
    if (nivel >= NIVEL_INTERMEDIO) {
      permitidos.add(TipoComodin.DOBLE_CHANCE);
    }
    if (nivel >= NIVEL_AVANZADO) {
      permitidos.add(TipoComodin.CINCUENTA_CINCUENTA);
    }

    for (TipoComodin tipo : permitidos) {
      obtenerOCrearComodinDeJugador(jugador, tipo);
    }
  }

  @Override
  public List<PartidaJugadorComodin> obtenerComodinesDeJugador(Long partidaId, Long usuarioId) {
    PartidaJugador jugador = obtenerJugador(partidaId, usuarioId);
    inicializarComodinesParaJugador(jugador);
    return repositorioComodin.obtenerPorPartidaJugador(jugador.getId());
  }

  @Override
  public boolean tieneComodinDisponible(Long partidaId, Long usuarioId, TipoComodin tipo) {
    try {
      PartidaJugador jugador = obtenerJugador(partidaId, usuarioId);
      if (!esComodinPermitidoParaJugador(jugador, tipo)) {
        return false;
      }
      PartidaJugadorComodin comodinJugador = repositorioComodin.buscarPorJugadorYTipo(
        jugador.getId(),
        tipo
      );
      if (comodinJugador == null) {
        comodinJugador = obtenerOCrearComodinDeJugador(jugador, tipo);
      }
      return (
        comodinJugador != null &&
        comodinJugador.getCantidadDisponible() > CANTIDAD_MINIMA_DISPONIBLE
      );
    } catch (Exception excepcion) {
      return false;
    }
  }

  @Override
  public int aplicarTiempoExtra(Long partidaId, Long usuarioId) {
    consumirComodinValido(partidaId, usuarioId, TipoComodin.TIEMPO_EXTRA);
    return SEGUNDOS_TIEMPO_EXTRA;
  }

  @Override
  public Comodin5050DTO aplicarCincuentaCincuenta(Long partidaId, Long usuarioId) {
    consumirComodinValido(partidaId, usuarioId, TipoComodin.CINCUENTA_CINCUENTA);

    Partida partida = repositorioPartida.obtenerPorIdConRondas(partidaId);
    PartidaRonda ronda = partida.obtenerRondaActual();
    List<Opcion> opciones = ronda.getPregunta().getOpciones();

    List<Long> incorrectas = new ArrayList<>();
    for (Opcion opcion : opciones) {
      if (!Boolean.TRUE.equals(opcion.getEsCorrecta())) {
        incorrectas.add(opcion.getId());
      }
    }

    Collections.shuffle(incorrectas);
    List<Long> descartadas = incorrectas.subList(0, CANTIDAD_OPCIONES_DESCARTADAS);

    return new Comodin5050DTO(descartadas, PENALIZACION_CINCUENTA_CINCUENTA);
  }

  @Override
  public void activarDobleChance(Long partidaId, Long usuarioId) {
    consumirComodinValido(partidaId, usuarioId, TipoComodin.DOBLE_CHANCE);

    Partida partida = repositorioPartida.obtenerPorIdConRondas(partidaId);
    PartidaJugador jugador = obtenerJugador(partidaId, usuarioId);
    PartidaRonda ronda = partida.obtenerRondaActual();

    jugador.setDobleChanceRondaId(ronda.getId());
    repositorioPartidaJugador.guardar(jugador);
  }

  private PartidaJugadorComodin consumirComodinValido(
    Long partidaId,
    Long usuarioId,
    TipoComodin tipo
  ) {
    PartidaJugador jugador = obtenerJugador(partidaId, usuarioId);

    if (!esComodinPermitidoParaJugador(jugador, tipo)) {
      throw new IllegalStateException(
        "El jugador no posee el nivel necesario para usar el comodín " + tipo
      );
    }

    PartidaJugadorComodin comodinJugador = repositorioComodin.buscarPorJugadorYTipo(
      jugador.getId(),
      tipo
    );

    if (comodinJugador == null) {
      comodinJugador = obtenerOCrearComodinDeJugador(jugador, tipo);
    }

    if (comodinJugador.getCantidadDisponible() <= CANTIDAD_MINIMA_DISPONIBLE) {
      throw new IllegalStateException("El comodín " + tipo + " ya fue utilizado en esta partida.");
    }

    comodinJugador.setCantidadDisponible(comodinJugador.getCantidadDisponible() - 1);
    comodinJugador.setVecesUsado(comodinJugador.getVecesUsado() + 1);
    repositorioComodin.actualizar(comodinJugador);
    return comodinJugador;
  }

  private PartidaJugadorComodin obtenerOCrearComodinDeJugador(
    PartidaJugador jugador,
    TipoComodin tipo
  ) {
    PartidaJugadorComodin comodinJugador = repositorioComodin.buscarPorJugadorYTipo(
      jugador.getId(),
      tipo
    );

    if (comodinJugador == null) {
      Comodin comodin = repositorioComodin.buscarComodinPorCodigo(tipo);
      if (comodin == null) {
        comodin = new Comodin(tipo, tipo.getEtiqueta(), tipo.getDescripcion(), null);
        repositorioComodin.guardarComodinMaestro(comodin);
      }
      comodinJugador = new PartidaJugadorComodin(jugador, comodin, STOCK_INICIAL_COMODIN);
      repositorioComodin.guardar(comodinJugador);
    }

    return comodinJugador;
  }

  private PartidaJugador obtenerJugador(Long partidaId, Long usuarioId) {
    PartidaJugador jugadorPartida = repositorioPartidaJugador.obtenerPorPartidaYUsuario(
      partidaId,
      usuarioId
    );
    if (jugadorPartida == null) {
      throw new IllegalArgumentException("El jugador no participa en la partida.");
    }
    return jugadorPartida;
  }
}
