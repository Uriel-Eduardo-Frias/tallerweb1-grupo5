package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioPartida {
  Partida obtenerPorId(Long id);
  Partida obtenerPorCodigoSala(String codigoSala);
  void guardar(Partida partida);
  List<Comodin> listarComodines();
  Comodin buscarComodinPorCodigo(TipoComodin codigo);
  List<PartidaJugador> listarPartidasDeUsuario(Long usuarioId);
}
