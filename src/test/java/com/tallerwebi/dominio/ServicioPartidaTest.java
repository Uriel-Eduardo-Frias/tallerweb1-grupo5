package com.tallerwebi.dominio;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPartidaTest {

  private ServicioPartida servicioPartida;
  private ServicioSala servicioSala;
  private RepositorioPartida repositorioPartida;

  private Sala sala;
  private Usuario host;

  @BeforeEach
  public void inicializar() {
    servicioSala = mock(ServicioSala.class);
    repositorioPartida = mock(RepositorioPartida.class);

    servicioPartida = new ServicioPartidaImp(servicioSala, repositorioPartida);

    host = givenUnUsuarioHost();
    sala = givenUnaSalaEnEsperaConUnJugador(host);

    when(servicioSala.buscarPorCodigo("ABC123")).thenReturn(sala);
  }

  @Test
  public void dadoQueHayUnaSalaEnEsperaCuandoInicioLaPartidaObtengoPartidaEnCurso() {
    // given
    // La sala en espera con su jugador se prepara en inicializar().

    // when
    Partida partida = whenInicioLaPartida("ABC123", host);

    // then
    thenLaPartidaEstaEnEstado(partida, EstadoPartida.EN_CURSO);
    thenLaSalaEstaEnEstado(sala, EstadoSala.EN_CURSO);
    thenLaPartidaTieneUnaRonda(partida);
    thenSeGuardaLaPartidaUnaVez(partida);
  }

  @Test
  public void dadoQueHayUnaPartidaEnCursoCuandoLaFinalizoObtengoPartidaFinalizada() {
    // given
    Partida partidaIniciada = whenInicioLaPartida("ABC123", host);
    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partidaIniciada);

    // when
    Partida partidaFinalizada = whenFinalizoLaPartida("ABC123", host);

    // then
    thenLaPartidaEstaEnEstado(partidaFinalizada, EstadoPartida.FINALIZADA);
    thenLaSalaEstaEnEstado(sala, EstadoSala.FINALIZADA);
    thenSeGuardaLaPartidaDosVeces(partidaFinalizada);
  }

  @Test
  public void dadoQueInicioUnaPartidaCuandoLaBuscoPorCodigoObtengoLaPartidaEnCurso() {
    // given
    Partida partidaIniciada = whenInicioLaPartida("ABC123", host);
    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partidaIniciada);

    // when
    Partida partidaEncontrada = whenBuscoLaPartidaPorCodigo("ABC123");

    // then
    thenLaPartidaEstaEnEstado(partidaEncontrada, EstadoPartida.EN_CURSO);
  }

  @Test
  void deberiaLanzarExcepcionSiLaSalaNoTieneJugadores() {
    // given
    sala.setJugadores(new ArrayList<>());

    // when
    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", host);

    // then
    assertThat(excepcion, instanceOf(SalaSinJugadoresException.class));
  }

  @Test
  void deberiaLanzarExcepcionSiLaSalaYaEstaEnCurso() {
    // given
    sala.setEstado(EstadoSala.EN_CURSO);

    // when
    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", host);

    // then
    assertThat(excepcion, instanceOf(SalaNoPuedeIniciarseException.class));
  }

  @Test
  void deberiaLanzarExcepcionSiNoExisteLaPartidaPorCodigo() {
    // given
    when(repositorioPartida.obtenerPorCodigoSala("NO-EXISTE")).thenReturn(null);

    // when
    Throwable excepcion = whenBuscarPartidaPorCodigoCapturandoExcepcion("NO-EXISTE");

    // then
    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
  }

  @Test
  void deberiaDevolverFalseSiNoHayJugadoresListos() {
    // given
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    sala.setJugadores(new ArrayList<>());
    when(repositorioPartida.obtenerPorId(10L)).thenReturn(partida);

    // when
    boolean resultado = servicioPartida.estanTodosListos(10L);

    // then
    assertFalse(resultado);
  }

  @Test
  void deberiaDevolverFalseSiAlgunJugadorNoEstaListo() {
    // given
    SalaJugador jugador = sala.getJugadores().get(0);
    jugador.setEstadoJugador(EstadoJugador.CONECTADO);

    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    when(repositorioPartida.obtenerPorId(10L)).thenReturn(partida);

    // when
    boolean resultado = servicioPartida.estanTodosListos(10L);

    // then
    assertFalse(resultado);
  }

  private Throwable whenIniciarCapturandoExcepcion(String codigoSala, Usuario solicitante) {
    try {
      servicioPartida.iniciarPartida(codigoSala, solicitante);
      return null;
    } catch (RuntimeException excepcion) {
      return excepcion;
    }
  }

  private Throwable whenBuscarPartidaPorCodigoCapturandoExcepcion(String codigoSala) {
    try {
      servicioPartida.buscarPartidaPorCodigoSala(codigoSala);
      return null;
    } catch (RuntimeException excepcion) {
      return excepcion;
    }
  }

  // Helpers de preparación

  private Usuario givenUnUsuarioHost() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    usuario.setUsername("Juan");
    return usuario;
  }

  private Sala givenUnaSalaEnEsperaConUnJugador(Usuario usuarioHost) {
    Sala salaPreparada = new Sala();
    salaPreparada.setCodigo("ABC123");
    salaPreparada.setNombre("Trivia del viernes");
    salaPreparada.setHost(usuarioHost);
    salaPreparada.setEstado(EstadoSala.EN_ESPERA);
    salaPreparada.setJugadores(new ArrayList<>());

    SalaJugador jugador = new SalaJugador();
    jugador.setSala(salaPreparada);
    jugador.setUsuario(usuarioHost);
    jugador.setEstadoJugador(EstadoJugador.CONECTADO);

    salaPreparada.getJugadores().add(jugador);
    return salaPreparada;
  }

  // Helpers de ejecución

  private Partida whenInicioLaPartida(String codigoSala, Usuario solicitante) {
    return servicioPartida.iniciarPartida(codigoSala, solicitante);
  }

  private Partida whenFinalizoLaPartida(String codigoSala, Usuario solicitante) {
    return servicioPartida.finalizarPartida(codigoSala, solicitante);
  }

  private Partida whenBuscoLaPartidaPorCodigo(String codigoSala) {
    return servicioPartida.buscarPartidaPorCodigoSala(codigoSala);
  }

  // Helpers de validación

  private void thenLaPartidaEstaEnEstado(Partida partida, EstadoPartida estadoEsperado) {
    assertThat(partida.getEstado(), equalTo(estadoEsperado));
  }

  private void thenLaSalaEstaEnEstado(Sala sala, EstadoSala estadoEsperado) {
    assertThat(sala.getEstado(), equalTo(estadoEsperado));
  }

  private void thenLaPartidaTieneUnaRonda(Partida partida) {
    assertThat(partida.getRondas().size(), equalTo(1));

    PartidaRonda ronda = partida.getRondas().get(0);
    assertThat(ronda.getNumero(), equalTo(1));
    assertThat(ronda.getEstado(), equalTo(EstadoRonda.VOTACION_CATEGORIA));
  }

  private void thenSeGuardaLaPartidaUnaVez(Partida partida) {
    verify(repositorioPartida, times(1)).guardar(partida);
  }

  private void thenSeGuardaLaPartidaDosVeces(Partida partida) {
    verify(repositorioPartida, times(2)).guardar(partida);
  }
}
