package com.tallerwebi.dominio;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.enums.EstadoJugador;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.SalaJugador;
import com.tallerwebi.dominio.modelo.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class NotificadorSalaTest {

  @Test
  void jugadorSeUnioDebeEnviarEventoAlTopicDeLaSala() {
    // given
    SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);

    Usuario ana = mock(Usuario.class);
    when(ana.getUsername()).thenReturn("ana");

    Usuario bob = mock(Usuario.class);
    when(bob.getUsername()).thenReturn("bob");

    Sala sala = new Sala("TRV-AAAA", "Sala", ana);
    sala.getJugadores().add(new SalaJugador(sala, EstadoJugador.CONECTADO, true, ana));
    sala.getJugadores().add(new SalaJugador(sala, EstadoJugador.CONECTADO, false, bob));

    NotificadorSala notificadorSala = new NotificadorSala(messagingTemplate);

    // when
    notificadorSala.jugadorSeUnio(sala);

    // then
    verify(messagingTemplate).convertAndSend(eq("/topic/salas/TRV-AAAA"), any(EventoSala.class));
  }
}
