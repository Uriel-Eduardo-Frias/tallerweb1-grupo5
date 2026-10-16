package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.ResultadoPartida;

public interface ServicioResultadoPartida {
  /** Cierra la partida: calcula posiciones, marca estados y notifica. Idempotente. */
  void cerrarPartida(Long partidaId);
  /**
   * Arma el resultado final para un jugador.
   *
   * @throws IllegalStateException si la partida todavía no finalizó
   * @throws IllegalArgumentException si el usuario no participó en la partida
   */
  ResultadoPartida obtenerResultado(Long partidaId, Long usuarioId);
}
