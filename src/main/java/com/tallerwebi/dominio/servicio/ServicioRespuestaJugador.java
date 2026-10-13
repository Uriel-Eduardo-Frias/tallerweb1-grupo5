package com.tallerwebi.dominio.servicio;

@FunctionalInterface
public interface ServicioRespuestaJugador {
  boolean registrarRespuesta(Long partidaId, Long usuarioId, Long opcionId);
}
