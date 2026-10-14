package com.tallerwebi.dominio;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.repositorio.RepositorioOpcion;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioRespuestaJugador;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.impl.ServicioRespuestaJugadorImpl;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

public class ServicioRespuestaJugadorImplTest {

  private ServicioPartida servicioPartida;
  private RepositorioOpcion repositorioOpcion;
  private RepositorioPartidaJugador repositorioPartidaJugador;
  private RepositorioRespuestaJugador repositorioRespuestaJugador;
  private ServicioRespuestaJugadorImpl servicio;

  private Partida partida;
  private PartidaRonda ronda;
  private Pregunta pregunta;
  private Opcion opcion;
  private PartidaJugador jugador;

  @Before
  public void preparar() {
    servicioPartida = mock(ServicioPartida.class);
    repositorioOpcion = mock(RepositorioOpcion.class);
    repositorioPartidaJugador = mock(RepositorioPartidaJugador.class);
    repositorioRespuestaJugador = mock(RepositorioRespuestaJugador.class);

    servicio =
      new ServicioRespuestaJugadorImpl(
        servicioPartida,
        repositorioOpcion,
        repositorioPartidaJugador,
        repositorioRespuestaJugador
      );

    partida = mock(Partida.class);
    ronda = mock(PartidaRonda.class);
    pregunta = mock(Pregunta.class);
    opcion = mock(Opcion.class);
    jugador = mock(PartidaJugador.class);

    when(servicioPartida.buscarPartidaPorId(1L)).thenReturn(partida);
    when(partida.obtenerRondaActual()).thenReturn(ronda);
    when(ronda.getPregunta()).thenReturn(pregunta);
    when(pregunta.getIdentificador()).thenReturn(10L);
    when(repositorioOpcion.obtenerPorId(20L)).thenReturn(opcion);
    when(opcion.getPregunta()).thenReturn(pregunta);
    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(1L, 2L)).thenReturn(jugador);
    when(jugador.getId()).thenReturn(30L);
    when(repositorioRespuestaJugador.existeRespuesta(10L, 30L)).thenReturn(false);
  }

  @Test
  public void dadoQueLaOpcionEsCorrectaCuandoRegistroLaRespuestaObtengoVerdadero() {
    when(opcion.getEsCorrecta()).thenReturn(true);

    boolean resultado = servicio.registrarRespuesta(1L, 2L, 20L);

    assertTrue(resultado);
    verify(repositorioRespuestaJugador).guardar(any(RespuestaJugador.class));
    verify(servicioPartida).registrarAcierto(jugador);
    verify(servicioPartida).sumarPuntaje(jugador, 100);
    verify(servicioPartida).finalizarRondaSiTodosRespondieron(1L);
  }

  @Test
  public void dadoQueLaOpcionEsIncorrectaCuandoRegistroLaRespuestaObtengoFalso() {
    when(opcion.getEsCorrecta()).thenReturn(false);

    boolean resultado = servicio.registrarRespuesta(1L, 2L, 20L);

    assertFalse(resultado);
    verify(repositorioRespuestaJugador).guardar(any(RespuestaJugador.class));
    verify(servicioPartida).registrarFallo(jugador);
    verify(servicioPartida, never()).registrarAcierto(jugador);
    verify(servicioPartida, never()).sumarPuntaje(jugador, 100);
    verify(servicioPartida).finalizarRondaSiTodosRespondieron(1L);
  }

  @Test
  public void dadoQueLaPartidaNoExisteCuandoRegistroLaRespuestaObtengoExcepcion() {
    when(servicioPartida.buscarPartidaPorId(1L)).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> servicio.registrarRespuesta(1L, 2L, 20L));

    verify(repositorioOpcion, never()).obtenerPorId(anyLong());
  }

  @Test
  public void dadoQueLaRondaNoExisteCuandoRegistroLaRespuestaObtengoExcepcion() {
    when(partida.obtenerRondaActual()).thenReturn(null);

    assertThrows(IllegalStateException.class, () -> servicio.registrarRespuesta(1L, 2L, 20L));
  }

  @Test
  public void dadoQueLaRondaNoTienePreguntaCuandoRegistroLaRespuestaObtengoExcepcion() {
    when(ronda.getPregunta()).thenReturn(null);

    assertThrows(IllegalStateException.class, () -> servicio.registrarRespuesta(1L, 2L, 20L));
  }

  @Test
  public void dadoQueLaOpcionNoExisteCuandoRegistroLaRespuestaObtengoExcepcion() {
    when(repositorioOpcion.obtenerPorId(20L)).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> servicio.registrarRespuesta(1L, 2L, 20L));
  }

  @Test
  public void dadoQueLaOpcionNoPerteneceALaPreguntaCuandoRegistroObtengoExcepcion() {
    Pregunta otraPregunta = mock(Pregunta.class);
    when(otraPregunta.getIdentificador()).thenReturn(99L);
    when(opcion.getPregunta()).thenReturn(otraPregunta);

    assertThrows(IllegalArgumentException.class, () -> servicio.registrarRespuesta(1L, 2L, 20L));
  }

  @Test
  public void dadoQueLaOpcionNoTienePreguntaCuandoRegistroObtengoExcepcion() {
    when(opcion.getPregunta()).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> servicio.registrarRespuesta(1L, 2L, 20L));
  }

  @Test
  public void dadoQueElUsuarioNoParticipaCuandoRegistroLaRespuestaObtengoExcepcion() {
    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(1L, 2L)).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> servicio.registrarRespuesta(1L, 2L, 20L));
  }

  @Test
  public void dadoQueElJugadorYaRespondioCuandoRegistroOtraRespuestaObtengoExcepcion() {
    when(repositorioRespuestaJugador.existeRespuesta(10L, 30L)).thenReturn(true);

    assertThrows(IllegalStateException.class, () -> servicio.registrarRespuesta(1L, 2L, 20L));

    verify(repositorioRespuestaJugador, never()).guardar(any(RespuestaJugador.class));
    verify(servicioPartida, never()).registrarAcierto(jugador);
    verify(servicioPartida, never()).registrarFallo(jugador);
  }
}
