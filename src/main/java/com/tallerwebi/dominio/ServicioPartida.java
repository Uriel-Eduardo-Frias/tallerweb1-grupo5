package com.tallerwebi.dominio;

import java.util.Map;


public interface ServicioPartida {

    Map<Long, Categoria> obtenerCategorias();

    Pregunta obtenerPreguntaPorCategoria(Long identificadorCategoria);

}