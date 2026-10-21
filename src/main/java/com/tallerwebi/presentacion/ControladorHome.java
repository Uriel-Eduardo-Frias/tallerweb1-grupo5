package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorHome {

  private static final int TAMANIO_TOP = 5;
  private ServicioEstadistica servicioEstadistica;

  @Autowired
  public ControladorHome(ServicioEstadistica servicioEstadistica) {
    this.servicioEstadistica = servicioEstadistica;
  }

  // Pagina principal
  @RequestMapping(path = "/", method = RequestMethod.GET)
  public ModelAndView inicio() {
    return new ModelAndView("redirect:/home");
  }

  // Ir al home
  @RequestMapping(path = "/home", method = RequestMethod.GET)
  public ModelAndView irAlHome() {
    Map<String, Object> modelo = new ModelMap();
    modelo.put("topRanking", servicioEstadistica.obtenerLeaderBoard(TAMANIO_TOP));
    return new ModelAndView("home", modelo);
  }
}
