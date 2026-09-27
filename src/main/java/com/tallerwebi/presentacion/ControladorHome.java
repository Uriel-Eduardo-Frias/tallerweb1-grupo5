package com.tallerwebi.presentacion;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorHome {

  // Pagina principal
  @RequestMapping(path = "/", method = RequestMethod.GET)
  public ModelAndView inicio() {
    return new ModelAndView("redirect:/login");
  }

  // Ir al home
  @RequestMapping(path = "/home", method = RequestMethod.GET)
  public ModelAndView irAlHome() {
    Map<String, Object> modelo = new ModelMap();
    return new ModelAndView("home", modelo);
  }

  // Ir a la pagina de buscar jugadores
  @RequestMapping(path = "/buscar-jugadores", method = RequestMethod.GET)
  public ModelAndView buscarJugadores() {
    Map<String, Object> modelo = new ModelMap();
    return new ModelAndView("buscar-jugadores", modelo);
  }
}
