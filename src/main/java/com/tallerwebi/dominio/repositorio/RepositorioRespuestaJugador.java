package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.RespuestaJugador;

@FunctionalInterface
public interface RepositorioRespuestaJugador {
  void guardar(RespuestaJugador respuesta);
}
