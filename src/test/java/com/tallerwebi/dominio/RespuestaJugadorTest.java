package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.Pregunta;
import com.tallerwebi.dominio.modelo.RespuestaJugador;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class RespuestaJugadorTest {

  @Test
  public void dadoQueCreoUnaRespuestaVaciaCuandoConsultoSusAtributosObtengoValoresIniciales() {
    RespuestaJugador respuesta = new RespuestaJugador();

    assertNull(respuesta.getId());
    assertNull(respuesta.getPartidaJugador());
    assertNull(respuesta.getPregunta());
    assertNull(respuesta.getOpcionElegida());
    assertFalse(respuesta.getEsCorrecta());
    assertNull(respuesta.getFechaRespuesta());
  }

  @Test
  public void dadoQueCreoUnaRespuestaCompletaCuandoConsultoObtengoLosValoresAsignados() {
    PartidaJugador partidaJugador = new PartidaJugador();
    Pregunta pregunta = new Pregunta();
    Opcion opcion = new Opcion();
    LocalDateTime fecha = LocalDateTime.of(2026, 10, 14, 10, 30);

    RespuestaJugador respuesta = new RespuestaJugador(
      partidaJugador,
      pregunta,
      opcion,
      true,
      fecha
    );

    assertSame(partidaJugador, respuesta.getPartidaJugador());
    assertSame(pregunta, respuesta.getPregunta());
    assertSame(opcion, respuesta.getOpcionElegida());
    assertTrue(respuesta.getEsCorrecta());
    assertEquals(fecha, respuesta.getFechaRespuesta());
    assertNull(respuesta.getId());
  }

  @Test
  public void dadoQueTengoUnaRespuestaCuandoAsignoUnIdObtengoElIdAsignado() {
    RespuestaJugador respuesta = new RespuestaJugador();

    respuesta.setId(10L);

    assertEquals(Long.valueOf(10L), respuesta.getId());
  }

  @Test
  public void dadoQueTengoUnaRespuestaCuandoCambioElJugadorObtengoElJugadorNuevo() {
    RespuestaJugador respuesta = new RespuestaJugador();
    PartidaJugador jugador = new PartidaJugador();

    respuesta.setPartidaJugador(jugador);

    assertSame(jugador, respuesta.getPartidaJugador());
  }

  @Test
  public void dadoQueTengoUnaRespuestaCuandoCambioLaPreguntaObtengoLaPreguntaNueva() {
    RespuestaJugador respuesta = new RespuestaJugador();
    Pregunta pregunta = new Pregunta();

    respuesta.setPregunta(pregunta);

    assertSame(pregunta, respuesta.getPregunta());
  }

  @Test
  public void dadoQueTengoUnaRespuestaCuandoCambioLaOpcionObtengoLaOpcionNueva() {
    RespuestaJugador respuesta = new RespuestaJugador();
    Opcion opcion = new Opcion();

    respuesta.setOpcionElegida(opcion);

    assertSame(opcion, respuesta.getOpcionElegida());
  }

  @Test
  public void dadoQueTengoUnaRespuestaCuandoCambioSiEsCorrectaObtengoElValorNuevo() {
    RespuestaJugador respuesta = new RespuestaJugador();

    respuesta.setEsCorrecta(true);
    assertTrue(respuesta.getEsCorrecta());

    respuesta.setEsCorrecta(false);
    assertFalse(respuesta.getEsCorrecta());
  }

  @Test
  public void dadoQueTengoUnaRespuestaCuandoCambioLaFechaObtengoLaFechaNueva() {
    RespuestaJugador respuesta = new RespuestaJugador();
    LocalDateTime fecha = LocalDateTime.of(2026, 10, 14, 11, 45);

    respuesta.setFechaRespuesta(fecha);

    assertEquals(fecha, respuesta.getFechaRespuesta());
  }

  @Test
  public void dadoQueTengoUnaRespuestaCuandoAsignoValoresNulosObtengoValoresNulos() {
    RespuestaJugador respuesta = new RespuestaJugador();

    respuesta.setPartidaJugador(null);
    respuesta.setPregunta(null);
    respuesta.setOpcionElegida(null);
    respuesta.setFechaRespuesta(null);

    assertNull(respuesta.getPartidaJugador());
    assertNull(respuesta.getPregunta());
    assertNull(respuesta.getOpcionElegida());
    assertNull(respuesta.getFechaRespuesta());
  }
}
