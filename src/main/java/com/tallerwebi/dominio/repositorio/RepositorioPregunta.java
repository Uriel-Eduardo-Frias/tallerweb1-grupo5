package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.Pregunta;
import java.util.List;

@FunctionalInterface
public interface RepositorioPregunta {
  List<Pregunta> buscarAleatorias(int cantidad);
}
