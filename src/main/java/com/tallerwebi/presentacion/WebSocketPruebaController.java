package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebSocketPruebaController {

  @RequestMapping("/websocket-prueba")
  public String mostrarPagina() {
    return "websocket-prueba";
  }
}
