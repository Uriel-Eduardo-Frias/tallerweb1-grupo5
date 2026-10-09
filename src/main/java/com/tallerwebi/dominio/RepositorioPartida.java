package com.tallerwebi.dominio;

public interface RepositorioPartida {
  Partida obtenerPorId(Long id);
  Partida obtenerPorCodigoSala(String codigoSala);
  void guardar(Partida partida);
}
