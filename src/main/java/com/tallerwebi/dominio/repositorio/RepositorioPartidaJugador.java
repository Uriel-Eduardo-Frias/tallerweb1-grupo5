package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.PartidaJugador;

public interface RepositorioPartidaJugador {
  PartidaJugador obtenerPorPartidaYUsuario(Long partidaId, Long usuarioId);
  void guardar(PartidaJugador jugador);
}
