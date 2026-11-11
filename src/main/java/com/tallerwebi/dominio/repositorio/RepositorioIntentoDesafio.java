package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.IntentoDesafio;
import java.util.List;

public interface RepositorioIntentoDesafio {
  IntentoDesafio buscarPorUsuarioYDesafio(Long usuarioId, Long desafioId);

  IntentoDesafio buscarIntentoPendiente(Long usuarioId, Long desafioId);

  List<IntentoDesafio> buscarRankingPorDesafio(Long desafioId);

  List<IntentoDesafio> buscarHistorialPorUsuario(Long usuarioId);

  List<IntentoDesafio> buscarFinalizadosPorDesafio(Long desafioId);

  List<IntentoDesafio> buscarFinalizadosPorUsuario(Long usuarioId);

  void guardar(IntentoDesafio intento);
}
