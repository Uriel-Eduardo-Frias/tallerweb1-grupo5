package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Categoria;
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

  @RequestMapping(value = "/votacion", method = RequestMethod.GET)
  public ModelAndView mostrarPantalla() {
    Map<String, Object> modelo = new ModelMap();

    // Obtenemos las categorías del servicio
    Map<Long, Categoria> mapaDeCategorias = this.servicioPartidaPregunta.obtenerCategorias();

    // Las cargamos al modelo para que Thymeleaf las pueda iterar
    modelo.put(ATTR_MAPA_CATEGORIAS, mapaDeCategorias);

    return new ModelAndView(VISTA_VOTACION, modelo);
  }

  @RequestMapping(value = "/responder", method = RequestMethod.POST)
  public ModelAndView procesarRespuesta(@RequestParam("opcionId") Long opcionId) {
    Map<String, Object> modelo = new ModelMap();
    modelo.put("opcionElegida", opcionId);
    return new ModelAndView(VISTA_PREGUNTA, modelo);
  }
}
