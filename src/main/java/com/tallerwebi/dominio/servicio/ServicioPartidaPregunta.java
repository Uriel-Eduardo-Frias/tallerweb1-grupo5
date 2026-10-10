package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.Categoria;
import com.tallerwebi.dominio.modelo.Pregunta;

import java.util.Map;

public interface ServicioPartidaPregunta {
  Map<Long, Categoria> obtenerCategorias();

  Pregunta obtenerPreguntaPorCategoria(Long identificadorCategoria);

  Boolean verificarRespuesta(Long opcionId);

  String obtenerTextoRespuestaCorrecta(Long opcionId);

  Pregunta obtenerPreguntaPorOpcionId(Long opcionId);
}
