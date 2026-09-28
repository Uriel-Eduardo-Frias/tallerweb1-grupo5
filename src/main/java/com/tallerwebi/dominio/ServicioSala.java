package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioSala {
  void unirse(Sala sala, Usuario usuario);
  Sala crearSala(String codigo, String nombre, Usuario host);
  void salir(Sala sala, Usuario usuario);
  void cambiarHost(Sala sala, Usuario solicitante, Usuario nuevoHost);
  Sala buscarPorCodigo(String codigo);
  List<Sala> listarSalas();
}
