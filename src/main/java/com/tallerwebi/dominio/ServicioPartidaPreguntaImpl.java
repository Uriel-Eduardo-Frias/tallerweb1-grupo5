package com.tallerwebi.dominio;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioPartidaPreguntaImpl implements ServicioPartidaPregunta {

  private final RepositorioPartidaPregunta repositorioPartidaPregunta;

  @Autowired
  public ServicioPartidaPreguntaImpl(RepositorioPartidaPregunta repositorioPartidaPregunta) {
    this.repositorioPartidaPregunta = repositorioPartidaPregunta;
  }

  @Override
  @Transactional(readOnly = true)
  public Map<Long, Categoria> obtenerCategorias() {
    List<Categoria> listaCategorias = this.repositorioPartidaPregunta.obtenerTodasLasCategorias();
    Map<Long, Categoria> mapaCategorias = new HashMap<>();

    if (listaCategorias != null) {
      for (Categoria categoria : listaCategorias) {
        mapaCategorias.put(categoria.getId(), categoria);
      }
    }
    return mapaCategorias;
  }

  @Override
  @Transactional(readOnly = true)
  public Pregunta obtenerPreguntaPorCategoria(Long identificadorCategoria) {
    if (identificadorCategoria == null) {
      return null;
    }
    Pregunta pregunta =
      this.repositorioPartidaPregunta.buscarPreguntaPorCategoria(identificadorCategoria);
    if (pregunta != null && pregunta.getOpciones() != null) {
      Collections.shuffle(pregunta.getOpciones());
    }
    return pregunta;
  }

  @Override
  @Transactional(readOnly = true)
  public Boolean verificarRespuesta(Long opcionId) {
    if (opcionId == null) {
      return false;
    }

    Opcion opcion = this.repositorioPartidaPregunta.buscarOpcionPorId(opcionId);
    if (opcion != null && Boolean.TRUE.equals(opcion.getEsCorrecta())) {
      return true;
    }
    return false;
  }

  @Override
  @Transactional(readOnly = true)
  public String obtenerTextoRespuestaCorrecta(Long opcionId) {
    if (opcionId == null) {
      return "";
    }

    Pregunta pregunta = this.repositorioPartidaPregunta.buscarPreguntaPorOpcionId(opcionId);
    if (pregunta != null && pregunta.getOpciones() != null) {
      for (Opcion opcion : pregunta.getOpciones()) {
        if (Boolean.TRUE.equals(opcion.getEsCorrecta())) {
          return opcion.getTexto();
        }
      }
    }
    return "";
  }

  @Override
  public Pregunta obtenerPreguntaPorOpcionId(Long opcionId) {
    return this.repositorioPartidaPregunta.buscarPreguntaPorOpcionId(opcionId);
  }
}
