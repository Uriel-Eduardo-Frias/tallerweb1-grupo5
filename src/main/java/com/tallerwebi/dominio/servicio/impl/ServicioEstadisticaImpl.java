package com.tallerwebi.dominio.servicio.impl;

import com.tallerwebi.dominio.modelo.EstadisticaUsuario;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import com.tallerwebi.dominio.servicio.ServicioPartidaPregunta;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ServicioEstadisticaImpl implements ServicioEstadistica {

  private final RepositorioPartidaJugador repositorioPartidaJugador;

  public ServicioEstadisticaImpl(RepositorioPartidaJugador repositorioPartidaJugador) {
    this.repositorioPartidaJugador = repositorioPartidaJugador;
  }

  @Override
  public EstadisticaUsuario obtenerEstadisticaUsuario(Long usuarioId) {
    EstadisticaUsuario estadistica = new EstadisticaUsuario();

    if (usuarioId == null) {
      return estadistica;
    }

    List<PartidaJugador> partidas = repositorioPartidaJugador.listarPartidasFinalizadasDelUsuario(
      usuarioId
    );

    boolean esLaUltima = true;

    for (PartidaJugador partida : partidas) {
      estadistica.sumarPartida(partida);

      if (esLaUltima) {
        estadistica.setRachaActualRespuestas(partida.getRachaRespuestasPartida());
        esLaUltima = false;
      }
    }

    return estadistica;
  }

  @Override
  public List<EstadisticaUsuario> obtenerLeaderBoard(int limite) {
    List<PartidaJugador> partidas = repositorioPartidaJugador.listarTodasLasPartidasFinalizadas();
    Map<Long, EstadisticaUsuario> porUsuario = new LinkedHashMap<>();
    for (PartidaJugador partida : partidas) {
      Long usuarioId = partida.getUsuario().getId();
      EstadisticaUsuario estadistica = porUsuario.get(usuarioId);

      if (estadistica == null) {
        estadistica = new EstadisticaUsuario();
        estadistica.setUsername(partida.getUsuario().getUsername());
        porUsuario.put(usuarioId, estadistica);
      }

      estadistica.sumarPartida(partida);
    }

    List<EstadisticaUsuario> ranking = new ArrayList<>(porUsuario.values());

    ranking.sort(
      Comparator
        .comparingInt(EstadisticaUsuario::getPartidasGanadas)
        .thenComparingDouble(EstadisticaUsuario::getPromedioAciertos)
        .reversed()
    );

    if (ranking.size() > limite) {
      return new ArrayList<>(ranking.subList(0, limite));
    }

    return ranking;
  }
}
