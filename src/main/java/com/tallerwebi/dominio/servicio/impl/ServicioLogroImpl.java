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
      evaluarLogrosDelUsuario(jugador.getUsuario());
    }
  }

  @Override
  public List<TipoLogro> evaluarLogrosDelUsuario(Usuario usuario) {
    List<TipoLogro> nuevos = new ArrayList<>();
    if (usuario == null || usuario.getId() == null) {
      return nuevos;
    }

    Map<TipoLogro, UsuarioLogro> yaDesbloqueados = logrosDelUsuario(usuario.getId());

    List<PartidaJugador> partidas = repositorioPartidaJugador.listarPartidasFinalizadasDelUsuario(
      usuario.getId()
    );

    EstadisticaUsuario estadistica = armarEstadistica(partidas);
    int victoriasConsecutivas = contarVictoriasConsecutivas(partidas);

    for (TipoLogro tipoLogro : TipoLogro.values()) {
      if (yaDesbloqueados.containsKey(tipoLogro)) {
        continue;
      }

      if (cumpleCondicion(tipoLogro, estadistica, victoriasConsecutivas)) {
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

  private EstadisticaUsuario armarEstadistica(List<PartidaJugador> partidas) {
    EstadisticaUsuario estadistica = new EstadisticaUsuario();

    for (PartidaJugador partida : partidas) {
      estadistica.sumarPartida(partida);
    }

    return estadistica;
  }

  private int contarVictoriasConsecutivas(List<PartidaJugador> partidasRecientesPrimero) {
    int victorias = 0;

    for (PartidaJugador partida : partidasRecientesPrimero) {
      Integer posicion = partida.getPosicionFinal();

      if (posicion == null || posicion != 1) {
        break;
      }

      victorias++;
    }

    return victorias;
  }

  private boolean cumpleCondicion(
    TipoLogro tipoLogro,
    EstadisticaUsuario estadistica,
    int victoriasConsecutivas
  ) {
    return switch (tipoLogro) {
      case EN_RACHA -> estadistica.getMejorRachaRespuestas() >= RACHA_EN_RACHA;
      case FUEGO_PURO -> estadistica.getMejorRachaRespuestas() >= FUEGO_PURO;
      case CAMPEON_DEBUTANTE -> estadistica.getPartidasGanadas() >= 1;
      case ERUDITO -> estadistica.getPreguntasAcertadas() >= RESPUESTAS_ERUDITO;
      case IMPARABLE -> victoriasConsecutivas >= VICTORIAS_IMPARABLE;
    };
  }
}
