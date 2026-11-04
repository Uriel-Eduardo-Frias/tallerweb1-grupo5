package com.tallerwebi.dominio.servicio.impl;

import com.tallerwebi.dominio.enumeration.TipoLogro;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.repositorio.RepositorioLogro;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.ServicioLogro;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioLogroImpl implements ServicioLogro {

  // LOGROS
  private static final int RACHA_EN_RACHA = 3;
  private static final int FUEGO_PURO = 5;
  private static final int RESPUESTAS_ERUDITO = 50;
  private static final int VICTORIAS_IMPARABLE = 3;

  private final RepositorioLogro repositorioLogro;
  private final RepositorioPartidaJugador repositorioPartidaJugador;

  public ServicioLogroImpl(
    RepositorioLogro repositorioLogro,
    RepositorioPartidaJugador repositorioPartidaJugador
  ) {
    this.repositorioLogro = repositorioLogro;
    this.repositorioPartidaJugador = repositorioPartidaJugador;
  }

  @Override
  public void evaluarLogroDePartida(Long partidaId) {
    for (PartidaJugador jugador : repositorioPartidaJugador.obtenerPorPartida(partidaId)) {
      evaluarLogrosDelJugador(jugador);
    }
  }

  @Override
  public List<TipoLogro> evaluarLogrosDelJugador(PartidaJugador jugador) {
    List<TipoLogro> nuevos = new ArrayList<>();

    if (jugador == null) {
      return nuevos;
    }

    Usuario usuario = jugador.getUsuario();

    if (usuario == null || usuario.getId() == null) {
      return nuevos;
    }

    Map<TipoLogro, UsuarioLogro> yaDesbloqueados = logrosDelUsuario(usuario.getId());

    // Salida temprana: si ya tiene todos, no hay nada más que calcular ni acumular.
    if (yaDesbloqueados.size() == TipoLogro.values().length) {
      return nuevos;
    }

    boolean gano = jugador.getPosicionFinal() != null && jugador.getPosicionFinal() == 1;
    ProgresoUsuario progreso = actualizarProgreso(usuario, jugador.getAciertos(), gano);

    for (TipoLogro tipoLogro : TipoLogro.values()) {
      if (yaDesbloqueados.containsKey(tipoLogro)) {
        continue;
      }

      if (cumpleCondicion(tipoLogro, jugador, gano, progreso)) {
        repositorioLogro.guardarLogro(new UsuarioLogro(usuario, tipoLogro));
        nuevos.add(tipoLogro);
      }
    }

    return nuevos;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Logro> obtenerLogrosDisponibles(Long usuarioId) {
    Map<TipoLogro, UsuarioLogro> desbloqueados = logrosDelUsuario(usuarioId);
    List<Logro> vistas = new ArrayList<>();

    for (TipoLogro tipoLogro : TipoLogro.values()) {
      UsuarioLogro logro = desbloqueados.get(tipoLogro);

      vistas.add(new Logro(tipoLogro, logro == null ? null : logro.getFechaDesbloqueo()));
    }

    return vistas;
  }

  private Map<TipoLogro, UsuarioLogro> logrosDelUsuario(Long usuarioId) {
    Map<TipoLogro, UsuarioLogro> mapa = new EnumMap<>(TipoLogro.class);

    if (usuarioId == null) {
      return mapa;
    }

    for (UsuarioLogro logro : repositorioLogro.obtenerLogroPorUsuario(usuarioId)) {
      mapa.put(logro.getTipoLogro(), logro);
    }

    return mapa;
  }

  private boolean cumpleCondicion(
    TipoLogro tipoLogro,
    PartidaJugador jugador,
    boolean gano,
    ProgresoUsuario progreso
  ) {
    return switch (tipoLogro) {
      case EN_RACHA -> jugador.getMejorRachaPartida() >= RACHA_EN_RACHA;
      case FUEGO_PURO -> jugador.getMejorRachaPartida() >= FUEGO_PURO;
      case CAMPEON_DEBUTANTE -> gano;
      case ERUDITO -> progreso.getAciertosTotales() >= RESPUESTAS_ERUDITO;
      case IMPARABLE -> progreso.getVictoriasConsecutivas() >= VICTORIAS_IMPARABLE;
    };
  }

  private ProgresoUsuario actualizarProgreso(Usuario usuario, int aciertos, boolean gano) {
    ProgresoUsuario progreso = repositorioLogro.obtenerProgreso(usuario.getId());

    if (progreso == null) {
      progreso = new ProgresoUsuario(usuario);
    }

    progreso.registrarPartida(aciertos, gano);
    repositorioLogro.guardarProgreso(progreso);

    return progreso;
  }
}
