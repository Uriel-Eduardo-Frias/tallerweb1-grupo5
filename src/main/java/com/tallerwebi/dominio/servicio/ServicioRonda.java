package com.tallerwebi.dominio.servicio;

public interface ServicioRonda {
  boolean finalizarRondaSiTodosRespondieron(Long partidaId);

  void avanzarRonda(Long partidaId, Long usuarioId);
}
