package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Categoria;
import com.tallerwebi.dominio.Pregunta;
import com.tallerwebi.dominio.ServicioPartidaPregunta;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class ControladorPartidaPregunta {

  private static final String VISTA_VOTACION = "votacion";
  private static final String VISTA_PREGUNTA = "pregunta";
  private static final String ATTR_MAPA_CATEGORIAS = "mapaDeCategorias";

  private final ServicioPartidaPregunta servicioPartidaPregunta;

  @Autowired
  public ControladorPartidaPregunta(ServicioPartidaPregunta servicioPartidaPregunta) {
    this.servicioPartidaPregunta = servicioPartidaPregunta;
  }

  // 1. Muestra la lista de categorías
  @RequestMapping(value = "/votacion", method = RequestMethod.GET)
  public ModelAndView mostrarPantalla() {
    Map<String, Object> modelo = new ModelMap();
    Map<Long, Categoria> mapaDeCategorias = this.servicioPartidaPregunta.obtenerCategorias();
    modelo.put(ATTR_MAPA_CATEGORIAS, mapaDeCategorias);
    return new ModelAndView(VISTA_VOTACION, modelo);
  }

  // 2. Recibe la categoría elegida y muestra la pregunta
  @RequestMapping(value = "/iniciar-pregunta", method = RequestMethod.POST)
  public ModelAndView iniciarPregunta(@RequestParam("categoriaId") Long categoriaId) {
    Map<String, Object> modelo = new ModelMap();

    Pregunta pregunta = this.servicioPartidaPregunta.obtenerPreguntaPorCategoria(categoriaId);

    modelo.put("pregunta", pregunta);
    modelo.put("yaRespondio", false);

    return new ModelAndView(VISTA_PREGUNTA, modelo);
  }

  // 3. Recibe la opción elegida y muestra si acertó o falló
  @RequestMapping(value = "/responder", method = RequestMethod.POST)
  public ModelAndView procesarRespuesta(@RequestParam("opcionId") Long opcionId) {
    Map<String, Object> modelo = new ModelMap();

    boolean esCorrecta = this.servicioPartidaPregunta.verificarRespuesta(opcionId);
    String textoCorrecto = this.servicioPartidaPregunta.obtenerTextoRespuestaCorrecta(opcionId);

    modelo.put("yaRespondio", true);
    modelo.put("esCorrecta", esCorrecta);
    modelo.put("textoCorrecto", textoCorrecto);

    return new ModelAndView(VISTA_PREGUNTA, modelo);
  }
}
