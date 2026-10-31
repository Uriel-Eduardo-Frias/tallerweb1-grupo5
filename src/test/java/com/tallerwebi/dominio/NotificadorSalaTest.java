package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.SalaJugador;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.presentacion.dto.EventoSala;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class NotificadorSalaTest {

  private SimpMessagingTemplate messagingTemplate;
  private NotificadorSala notificadorSala;
  private Sala sala;
  private SalaJugador jugador;
  private Usuario usuario;

  @BeforeEach
  public void dadoQueTengoUnNotificadorYSala() {
    messagingTemplate = mock(SimpMessagingTemplate.class);
    notificadorSala = new NotificadorSala(messagingTemplate);

    sala = mock(Sala.class);
    jugador = mock(SalaJugador.class);
    usuario = mock(Usuario.class);

    when(sala.getCodigo()).thenReturn("ABC123");
    when(sala.getJugadores()).thenReturn(Collections.singletonList(jugador));
    when(jugador.getUsuario()).thenReturn(usuario);
    when(usuario.getUsername()).thenReturn("juan");
  }

  @Test
  public void dadoQueUnJugadorSeUneCuandoNotificoObtengoEventoDeUnion() {
    notificadorSala.jugadorSeUnio(sala);

    verificarEventoEnviado("JUGADOR_SE_UNIO", "ABC123", Arrays.asList("juan"), null);
  }

  @Test
  public void dadoQueUnJugadorEstaListoCuandoNotificoObtengoEventoDeJugadorListo() {
    notificadorSala.jugadorListo(sala);

    verificarEventoEnviado("JUGADOR_LISTO", "ABC123", Arrays.asList("juan"), null);
  }

  @Test
  public void dadoQueComienzaUnaPartidaCuandoNotificoObtengoEventoDeInicio() {
    notificadorSala.partidaIniciada("ABC123", 10L);

    verificarEventoEnviado("PARTIDA_INICIADA", "ABC123", Collections.emptyList(), 10L);
  }

  @Test
  public void dadoQueFinalizaLaVotacionCuandoNotificoObtengoEventoDeVotacionFinalizada() {
    notificadorSala.votacionCategoriaFinalizada("ABC123", 20L);

    verificarEventoEnviado("VOTACION_CATEGORIA_FINALIZADA", "ABC123", Collections.emptyList(), 20L);
  }

  @Test
  public void dadoQueSeActualizaUnaRondaCuandoNotificoObtengoEventoDeRondaActualizada() {
    notificadorSala.rondaActualizada("ABC123", 30L, "RONDA_ACTUALIZADA");

    verificarEventoEnviado("RONDA_ACTUALIZADA", "ABC123", Collections.emptyList(), 30L);
  }

  private void verificarEventoEnviado(
    String tipo,
    String codigoSala,
    List<String> jugadores,
    Long partidaId
  ) {
    ArgumentCaptor<EventoSala> captor = ArgumentCaptor.forClass(EventoSala.class);

    verify(messagingTemplate).convertAndSend(eq("/topic/salas/" + codigoSala), captor.capture());

    EventoSala evento = captor.getValue();

    assertEquals(tipo, evento.getTipo());
    assertEquals(codigoSala, evento.getCodigoSala());
    assertEquals(jugadores, evento.getJugadores());
    assertEquals(partidaId, evento.getPartidaId());
  }
}
