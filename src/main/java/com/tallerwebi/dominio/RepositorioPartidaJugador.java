package com.tallerwebi.dominio;

public interface RepositorioPartidaJugador {
  PartidaJugador obtenerPorPartidaYUsuario(Long partidaId, Long usuarioId);
  void guardar(PartidaJugador jugador);
}
