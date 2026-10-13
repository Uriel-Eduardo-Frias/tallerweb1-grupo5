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
  private static final String TOPIC_SALA = "/topic/salas/";

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

  public void jugadorListo(Sala sala) {
    List<String> jugadores = obtenerJugadores(sala);

    EventoSala evento = new EventoSala("JUGADOR_LISTO", sala.getCodigo(), jugadores);

    messagingTemplate.convertAndSend(TOPIC_SALA + sala.getCodigo(), evento);
  }

  public void partidaIniciada(String codigoSala, Long partidaId) {
    EventoSala evento = new EventoSala("PARTIDA_INICIADA", codigoSala, new ArrayList<>());
    evento.setPartidaId(partidaId);

    messagingTemplate.convertAndSend(TOPIC_SALA + codigoSala, evento);
  }

  public void votacionCategoriaFinalizada(String codigoSala, Long partidaId) {
    EventoSala evento = new EventoSala(
      "VOTACION_CATEGORIA_FINALIZADA",
      codigoSala,
      new ArrayList<>()
    );

    evento.setPartidaId(partidaId);

    messagingTemplate.convertAndSend(TOPIC_SALA + codigoSala, evento);
  }
}
