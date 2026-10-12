package com.tallerwebi.dominio;

@FunctionalInterface
public interface ServicioRespuestaJugador {
  boolean registrarRespuesta(Long partidaId, Long usuarioId, Long opcionId);
}
