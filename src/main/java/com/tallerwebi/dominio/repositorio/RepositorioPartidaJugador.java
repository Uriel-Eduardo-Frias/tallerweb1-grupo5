package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.PartidaJugador;
import java.util.List;

public interface RepositorioPartidaJugador {
  PartidaJugador obtenerPorPartidaYUsuario(Long partidaId, Long usuarioId);
  void guardar(PartidaJugador jugador);
  List<PartidaJugador> obtenerPorPartida(Long partidaId);

  List<PartidaJugador> listarPartidasFinalizadasDelUsuario(Long usuarioId);
  List<PartidaJugador> listarTodasLasPartidasFinalizadas();
}
