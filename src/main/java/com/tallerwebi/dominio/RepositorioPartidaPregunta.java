package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioPartidaPregunta {
  List<Categoria> obtenerTodasLasCategorias();
  Pregunta buscarPreguntaPorCategoria(Long categoriaId);
  Opcion buscarOpcionPorId(Long opcionId);
  Pregunta buscarPreguntaPorOpcionId(Long opcionId);
}
