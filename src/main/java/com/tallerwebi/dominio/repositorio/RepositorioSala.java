package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.Sala;

import java.util.List;

public interface RepositorioSala {
  Sala obtenerSalaPorCodigo(String codigo);
  void guardar(Sala sala);
  List<Sala> listarSalas();
  Sala obtenerPorCodigoConJugadores(String codigo);
}
