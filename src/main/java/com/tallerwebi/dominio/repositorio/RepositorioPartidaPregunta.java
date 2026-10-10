package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.Categoria;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Pregunta;

import java.util.List;

public interface RepositorioPartidaPregunta {
  List<Categoria> obtenerTodasLasCategorias();
  Pregunta buscarPreguntaPorCategoria(Long categoriaId);
  Opcion buscarOpcionPorId(Long opcionId);
  Pregunta buscarPreguntaPorOpcionId(Long opcionId);
}
