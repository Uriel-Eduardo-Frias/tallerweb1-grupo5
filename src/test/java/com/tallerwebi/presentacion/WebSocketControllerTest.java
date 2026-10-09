package com.tallerwebi.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class WebSocketControllerTest {

  @Test
  void recibirMensajeDebeDevolverElMismoMensaje() {
    assertEquals("hola", new WebSocketController().recibirMensaje("hola"));
  }

  @Test
  void mostrarPaginaDebeDevolverLaVistaDePrueba() {
    assertEquals("websocket-prueba", new WebSocketPruebaController().mostrarPagina());
  }
}
