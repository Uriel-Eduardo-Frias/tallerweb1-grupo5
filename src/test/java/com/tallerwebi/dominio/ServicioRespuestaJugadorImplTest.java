package com.tallerwebi.dominio;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.JugadorBloqueadoException;
import com.tallerwebi.dominio.excepcion.PrimerIntentoFallidoDobleChanceException;
import com.tallerwebi.dominio.excepcion.RespuestaDuplicadaException;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.repositorio.RepositorioOpcion;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioRespuestaJugador;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.impl.ServicioRespuestaJugadorImpl;
import java.time.LocalDateTime;
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
    when(ronda.getId()).thenReturn(50L);
    when(ronda.getPregunta()).thenReturn(pregunta);
    when(pregunta.getIdentificador()).thenReturn(10L);
    when(repositorioOpcion.obtenerPorId(20L)).thenReturn(opcion);
    when(opcion.getPregunta()).thenReturn(pregunta);
    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(1L, 2L)).thenReturn(jugador);
    when(jugador.getId()).thenReturn(30L);

    when(repositorioRespuestaJugador.existeRespuestaEnRonda(50L, 30L)).thenReturn(false);
    when(jugador.estaBloqueado()).thenReturn(false);
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
    when(repositorioRespuestaJugador.existeRespuestaEnRonda(50L, 30L)).thenReturn(true);

    assertThrows(RespuestaDuplicadaException.class, () -> servicio.registrarRespuesta(1L, 2L, 20L));

    verify(repositorioRespuestaJugador, never()).guardar(any(RespuestaJugador.class));
    verify(servicioPartida, never()).registrarAcierto(jugador);
    verify(servicioPartida, never()).registrarFallo(jugador);
  }

  @Test
  public void dadoQueElJugadorEstaBloqueadoCuandoRegistroRespuestaObtengoExcepcion() {
    when(jugador.estaBloqueado()).thenReturn(true);
    when(jugador.getBloqueadoHasta()).thenReturn(LocalDateTime.now().plusSeconds(30));

    assertThrows(JugadorBloqueadoException.class, () -> servicio.registrarRespuesta(1L, 2L, 20L));
  }

  @Test
  public void dadoQueTieneDobleChanceYRespondeMalObtengoPrimerIntentoFallidoException() {
    when(opcion.getEsCorrecta()).thenReturn(false);
    when(jugador.getDobleChanceRondaId()).thenReturn(50L);

    assertThrows(
      PrimerIntentoFallidoDobleChanceException.class,
      () -> servicio.registrarRespuesta(1L, 2L, 20L)
    );

    verify(jugador).setDobleChanceRondaId(null);
    verify(repositorioPartidaJugador).guardar(jugador);
  }

  @Test
  public void dadoQueTieneDobleChanceYRespondeBienSeBorraDobleChanceYSeRegistraAcierto() {
    when(opcion.getEsCorrecta()).thenReturn(true);
    when(jugador.getDobleChanceRondaId()).thenReturn(50L);

    boolean resultado = servicio.registrarRespuesta(1L, 2L, 20L);

    assertTrue(resultado);
    verify(jugador).setDobleChanceRondaId(null);
    verify(repositorioPartidaJugador).guardar(jugador);
    verify(servicioPartida).registrarAcierto(jugador);
  }

  @Test
  public void dadoQueSeConsultaSiYaRespondioYRespondioDevuelveVerdadero() {
    when(repositorioRespuestaJugador.existeRespuestaEnRonda(50L, 30L)).thenReturn(true);
    assertTrue(servicio.yaRespondio(1L, 2L));
  }

  @Test
  public void dadoQueSeConsultaSiYaRespondioYNoRespondioDevuelveFalso() {
    when(repositorioRespuestaJugador.existeRespuestaEnRonda(50L, 30L)).thenReturn(false);
    assertFalse(servicio.yaRespondio(1L, 2L));
  }

  @Test
  public void dadoQueObtengoResultadoYExisteRespuestaDevuelveSuValorCorrecto() {
    RespuestaJugador respuesta = mock(RespuestaJugador.class);
    when(respuesta.getEsCorrecta()).thenReturn(true);
    when(repositorioRespuestaJugador.buscarRespuestaEnRonda(50L, 30L)).thenReturn(respuesta);

    assertTrue(servicio.obtenerResultado(1L, 2L));
  }

  @Test
  public void dadoQueObtengoResultadoYNoExisteRespuestaDevuelveNull() {
    when(repositorioRespuestaJugador.buscarRespuestaEnRonda(50L, 30L)).thenReturn(null);
    assertNull(servicio.obtenerResultado(1L, 2L));
  }

  @Test
  public void dadoQueSeAgotaElTiempoSeRegistraRespuestaIncorrectaPorTiempo() {
    servicio.registrarTiempoAgotado(1L, 2L);

    verify(repositorioRespuestaJugador).guardar(any(RespuestaJugador.class));
    verify(servicioPartida).registrarFallo(jugador);
    verify(servicioPartida).finalizarRondaSiTodosRespondieron(1L);
  }

  @Test
  public void dadoQueSeAgotaElTiempoYElJugadorTeniaDobleChanceSeLeResetea() {
    when(jugador.getDobleChanceRondaId()).thenReturn(50L);

    servicio.registrarTiempoAgotado(1L, 2L);

    verify(jugador).setDobleChanceRondaId(null);
    verify(repositorioPartidaJugador).guardar(jugador);
    verify(repositorioRespuestaJugador).guardar(any(RespuestaJugador.class));
  }

  @Test
  public void dadoQueConsultoSiFuePorTiempoYLoFueDevuelveVerdadero() {
    RespuestaJugador respuesta = mock(RespuestaJugador.class);
    when(respuesta.isFuePorTiempo()).thenReturn(true);
    when(repositorioRespuestaJugador.buscarRespuestaEnRonda(50L, 30L)).thenReturn(respuesta);

    assertTrue(servicio.fuePorTiempo(1L, 2L));
  }

  @Test
  public void dadoQueConsultoSiFuePorTiempoYNoLoFueDevuelveFalso() {
    RespuestaJugador respuesta = mock(RespuestaJugador.class);
    when(respuesta.isFuePorTiempo()).thenReturn(false);
    when(repositorioRespuestaJugador.buscarRespuestaEnRonda(50L, 30L)).thenReturn(respuesta);

    assertFalse(servicio.fuePorTiempo(1L, 2L));
  }

  @Test
  public void dadoQueConsultoSiFuePorTiempoYNoHayRespuestaDevuelveFalso() {
    when(repositorioRespuestaJugador.buscarRespuestaEnRonda(50L, 30L)).thenReturn(null);
    assertFalse(servicio.fuePorTiempo(1L, 2L));
  }
}
