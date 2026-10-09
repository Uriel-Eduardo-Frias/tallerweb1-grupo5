package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioSala {
  Sala obtenerSalaPorCodigo(String codigo);
  void guardar(Sala sala);
  List<Sala> listarSalas();
  Sala obtenerPorCodigoConJugadores(String codigo);
}
