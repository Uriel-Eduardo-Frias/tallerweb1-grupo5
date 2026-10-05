package com.tallerwebi.presentacion;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

  @MessageMapping("/prueba")
  @SendTo("/topic/mensajes")
  public String recibirMensaje(String mensaje) {
    return mensaje;
  }
}
