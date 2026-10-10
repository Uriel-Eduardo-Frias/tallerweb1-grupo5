package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.SalaJugador;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificadorSala {

  private final SimpMessagingTemplate messagingTemplate;

  public NotificadorSala(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  public void jugadorSeUnio(Sala sala) {
    List<String> jugadores = obtenerJugadores(sala);

    EventoSala evento = new EventoSala("JUGADOR_SE_UNIO", sala.getCodigo(), jugadores);

    messagingTemplate.convertAndSend("/topic/salas/" + sala.getCodigo(), evento);
  }

  private List<String> obtenerJugadores(Sala sala) {
    List<String> jugadores = new ArrayList<>();

    for (SalaJugador jugador : sala.getJugadores()) {
      jugadores.add(jugador.getUsuario().getUsername());
    }

    return jugadores;
  }
}
