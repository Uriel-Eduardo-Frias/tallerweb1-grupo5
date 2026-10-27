package com.tallerwebi.dominio.servicio.impl;

import com.tallerwebi.dominio.modelo.EstadisticaUsuario;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ServicioEstadisticaImpl implements ServicioEstadistica {

  private static final int VICTORIAS_PARA_NIVEL_TRES = 2;
  private static final int VICTORIAS_PARA_NIVEL_DOS = 1;
  private static final int NIVEL_INICIAL = 1;
  private static final int NIVEL_INTERMEDIO = 2;
  private static final int NIVEL_AVANZADO = 3;

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
  public int calcularNivelUsuario(Long usuarioId) {
    if (usuarioId == null) {
      return NIVEL_INICIAL;
    }

    EstadisticaUsuario estadistica = obtenerEstadisticaUsuario(usuarioId);
    int partidasGanadas = (estadistica != null) ? estadistica.getPartidasGanadas() : 0;

    if (partidasGanadas >= VICTORIAS_PARA_NIVEL_TRES) {
      return NIVEL_AVANZADO;
    }
    if (partidasGanadas == VICTORIAS_PARA_NIVEL_DOS) {
      return NIVEL_INTERMEDIO;
    }
    return NIVEL_INICIAL;
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
