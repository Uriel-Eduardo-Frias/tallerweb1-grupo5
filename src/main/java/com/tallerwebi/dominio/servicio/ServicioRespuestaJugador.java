package com.tallerwebi.dominio.servicio;

public interface ServicioRespuestaJugador {
  boolean registrarRespuesta(Long partidaId, Long usuarioId, Long opcionId);
  boolean yaRespondio(Long partidaId, Long usuarioId);
  Boolean obtenerResultado(Long partidaId, Long usuarioId);

  void registrarTiempoAgotado(Long partidaId, Long usuarioId);
  boolean fuePorTiempo(Long partidaId, Long usuarioId);
}
