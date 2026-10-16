package com.tallerwebi.dominio.servicio;

public interface ServicioRespuestaJugador {
  boolean registrarRespuesta(Long partidaId, Long usuarioId, Long opcionId);

  boolean yaRespondio(Long partidaId, Long usuarioId);

  /** @return true/false según la respuesta del jugador en la ronda actual, o null si no respondió. */
  Boolean obtenerResultado(Long partidaId, Long usuarioId);
}
