package com.tallerwebi.dominio;

import java.util.Map;

public interface ServicioPartidaPregunta {
  Map<Long, Categoria> obtenerCategorias();

  Pregunta obtenerPreguntaPorCategoria(Long identificadorCategoria);

  Boolean verificarRespuesta(Long opcionId);

  String obtenerTextoRespuestaCorrecta(Long opcionId);
}
