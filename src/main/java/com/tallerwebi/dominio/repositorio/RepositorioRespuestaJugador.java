package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.RespuestaJugador;

import java.util.List;

public interface RepositorioRespuestaJugador {
  void guardar(RespuestaJugador respuesta);
  boolean existeRespuesta(Long preguntaId, Long partidaJugadorId);
  List<RespuestaJugador> obtenerPorPregunta(Long preguntaId);
}
