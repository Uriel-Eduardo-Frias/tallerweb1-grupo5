package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.SalaJugador;
import com.tallerwebi.presentacion.dto.EventoSala;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class NotificadorSala {

  private static final String TOPIC_SALA = "/topic/salas/";

  private final SimpMessagingTemplate messagingTemplate;

  public NotificadorSala(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  public void jugadorSeUnio(Sala sala) {
    enviar(
            sala.getCodigo(),
            new EventoSala("JUGADOR_SE_UNIO", sala.getCodigo(), obtenerJugadores(sala))
    );
  }

  public void jugadorListo(Sala sala) {
    enviar(
            sala.getCodigo(),
            new EventoSala("JUGADOR_LISTO", sala.getCodigo(), obtenerJugadores(sala))
    );
  }

  public void partidaIniciada(String codigoSala, Long partidaId) {
    rondaActualizada(codigoSala, partidaId, "PARTIDA_INICIADA");
  }

  public void votacionCategoriaFinalizada(String codigoSala, Long partidaId) {
    rondaActualizada(codigoSala, partidaId, "VOTACION_CATEGORIA_FINALIZADA");
  }

  public void rondaResultado(String codigoSala, Long partidaId) {
    rondaActualizada(codigoSala, partidaId, "RONDA_RESULTADO");
  }

  public void rondaActualizada(String codigoSala, Long partidaId, String tipoEvento) {
    EventoSala evento = new EventoSala(tipoEvento, codigoSala, new ArrayList<>());
    evento.setPartidaId(partidaId);

    enviar(codigoSala, evento);
  }

  public void notificarComodinOponente(String codigoSala, Long partidaId, String tipoComodin, String usernameObjetivo) {
    EventoSala evento = new EventoSala(tipoComodin, codigoSala, Collections.singletonList(usernameObjetivo));
    evento.setPartidaId(partidaId);

    enviar(codigoSala, evento);
  }

  private List<String> obtenerJugadores(Sala sala) {
    List<String> jugadores = new ArrayList<>();

    for (SalaJugador jugador : sala.getJugadores()) {
      jugadores.add(jugador.getUsuario().getUsername());
    }

    return jugadores;
  }

  private void enviar(String codigoSala, EventoSala evento) {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(
              new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                  messagingTemplate.convertAndSend(TOPIC_SALA + codigoSala, evento);
                }
              }
      );
      return;
    }

    messagingTemplate.convertAndSend(TOPIC_SALA + codigoSala, evento);
  }
}