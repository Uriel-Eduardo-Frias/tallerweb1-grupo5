package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.Comodin;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.enumeration.TipoComodin;

import java.util.List;

public interface RepositorioPartida {
  Partida obtenerPorId(Long id);
  Partida obtenerPorCodigoSala(String codigoSala);
  void guardar(Partida partida);
  List<Comodin> listarComodines();
  Comodin buscarComodinPorCodigo(TipoComodin codigo);
  List<PartidaJugador> listarPartidasDeUsuario(Long usuarioId);
  Partida obtenerPorIdConRondas(Long id);
}
