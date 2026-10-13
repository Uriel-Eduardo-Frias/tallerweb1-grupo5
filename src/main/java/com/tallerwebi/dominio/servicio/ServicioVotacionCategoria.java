package com.tallerwebi.dominio.servicio;

public interface ServicioVotacionCategoria {
  boolean registrarVotoCategoria(Long partidaId, Long usuarioId, Long categoriaId);

  void finalizarVotacionCategoria(Long partidaId, Long usuarioId);
}
