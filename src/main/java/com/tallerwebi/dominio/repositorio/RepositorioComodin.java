package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.enumeration.TipoComodin;
import com.tallerwebi.dominio.modelo.Comodin;
import com.tallerwebi.dominio.modelo.PartidaJugadorComodin;
import java.util.List;

public interface RepositorioComodin {
  void guardar(PartidaJugadorComodin comodinJugador);
  void actualizar(PartidaJugadorComodin comodinJugador);
  PartidaJugadorComodin buscarPorJugadorYTipo(Long partidaJugadorId, TipoComodin tipo);
  List<PartidaJugadorComodin> obtenerPorPartidaJugador(Long partidaJugadorId);
  Comodin buscarComodinPorCodigo(TipoComodin tipo);
  void guardarComodinMaestro(Comodin comodin);
  List<Comodin> listarTodos();
}
