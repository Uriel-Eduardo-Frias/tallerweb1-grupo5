package com.tallerwebi.dominio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class EventoSalaTest {

  private EventoSala evento;
  private List<String> jugadores;

  @Before
  public void dadoQueTengoUnEventoSala() {
    jugadores = Arrays.asList("juan", "pedro");
    evento = new EventoSala("JUGADOR_SE_UNIO", "ABC123", jugadores);
  }

  @Test
  public void dadoQueCreoUnEventoCuandoConsultoObtengoSusDatos() {
    assertEquals("JUGADOR_SE_UNIO", evento.getTipo());
    assertEquals("ABC123", evento.getCodigoSala());
    assertEquals(jugadores, evento.getJugadores());
    assertNull(evento.getPartidaId());
  }

  @Test
  public void dadoQueTengoUnEventoCuandoModificoTipoObtengoElNuevoTipo() {
    evento.setTipo("JUGADOR_LISTO");

    assertEquals("JUGADOR_LISTO", evento.getTipo());
  }

  @Test
  public void dadoQueTengoUnEventoCuandoModificoCodigoObtengoElNuevoCodigo() {
    evento.setCodigoSala("XYZ789");

    assertEquals("XYZ789", evento.getCodigoSala());
  }

  @Test
  public void dadoQueTengoUnEventoCuandoModificoJugadoresObtengoLaNuevaLista() {
    List<String> nuevosJugadores = Collections.singletonList("maria");

    evento.setJugadores(nuevosJugadores);

    assertEquals(nuevosJugadores, evento.getJugadores());
  }

  @Test
  public void dadoQueTengoUnEventoCuandoAsignoPartidaObtengoSuIdentificador() {
    evento.setPartidaId(50L);

    assertEquals(Long.valueOf(50L), evento.getPartidaId());
  }
}
