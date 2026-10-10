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

  @Test
  public void dadoUnJugadorCuandoSumaPuntajePositivoObtienePuntajeAcumuladoIncrementado() {
    // given
    PartidaJugador jugador = givenUnJugadorConPuntaje(10);

    // when
    whenSumoPuntajeAJugador(jugador, 5);

    // then
    thenElPuntajeDelJugadorEs(jugador, 15);
  }

  @Test
  public void dadoUnComodinConCantidadCuandoSeConsumeSeReduceSuCantidadYAumentaSuUso() {
    // given
    PartidaJugadorComodin comodinJugador = givenUnComodinDisponibleParaJugador(1);

    // when
    boolean resultado = whenConsumoElComodin(comodinJugador);

    // then
    thenElComodinFueConsumidoConExito(resultado, comodinJugador);
  }

  @Test
  public void dadoUnComodinSinCantidadCuandoSeConsumeFallaLaOperacion() {
    // given
    PartidaJugadorComodin comodinJugador = givenUnComodinDisponibleParaJugador(0);

    // when
    boolean resultado = whenConsumoElComodin(comodinJugador);

    // then
    thenElComodinNoPudoConsumirse(resultado, comodinJugador);
  }

  /*
  @Test
  public void dadaUnaRondaConRespuestaDeJugadorCuandoSeVerificaSiRespondioObtieneTrue() {
    // given
    PartidaRonda ronda = givenUnaRondaConRespuestaDeJugador(100L);

    // when
    boolean respondio = whenVerificoSiJugadorRespondio(ronda, 100L);

    // then
    thenElJugadorHaRespondido(respondio);
  }
*/
  private PartidaJugador givenUnJugadorConPuntaje(int puntajeInicial) {
    PartidaJugador jugador = new PartidaJugador();
    jugador.setPuntajeAcumulado(puntajeInicial);
    return jugador;
  }

  private PartidaJugadorComodin givenUnComodinDisponibleParaJugador(int cantidadInicial) {
    PartidaJugador jugador = new PartidaJugador();
    Comodin comodin = new Comodin();
    return new PartidaJugadorComodin(jugador, comodin, cantidadInicial);
  }

  /*
  private PartidaRonda givenUnaRondaConRespuestaDeJugador(Long partidaJugadorId) {
    PartidaRonda ronda = new PartidaRonda();
    PartidaJugador jugador = new PartidaJugador();
    jugador.setId(partidaJugadorId);

    RespuestaJugador respuesta = new RespuestaJugador();
    respuesta.setPartidaJugador(jugador);
    ronda.getRespuestas().add(respuesta);
    return ronda;
  }
 */

  // --- Helpers de ejecución (When) ---
  private void whenSumoPuntajeAJugador(PartidaJugador jugador, int puntos) {
    servicioPartida.sumarPuntaje(jugador, puntos);
  }

  private boolean whenConsumoElComodin(PartidaJugadorComodin comodinJugador) {
    return servicioPartida.consumirComodin(comodinJugador);
  }

  /*
  private boolean whenVerificoSiJugadorRespondio(PartidaRonda ronda, Long partidaJugadorId) {
    return servicioPartida.haRespondido(ronda, partidaJugadorId);
  }
*/

  // --- Helpers de validación (Then) ---
  private void thenElPuntajeDelJugadorEs(PartidaJugador jugador, int puntajeEsperado) {
    assertThat(jugador.getPuntajeAcumulado(), equalTo(puntajeEsperado));
  }

  private void thenElComodinFueConsumidoConExito(
    boolean resultado,
    PartidaJugadorComodin comodinJugador
  ) {
    assertThat(resultado, equalTo(true));
    assertThat(comodinJugador.getCantidadDisponible(), equalTo(0));
    assertThat(comodinJugador.getVecesUsado(), equalTo(1));
  }

  private void thenElComodinNoPudoConsumirse(
    boolean resultado,
    PartidaJugadorComodin comodinJugador
  ) {
    assertThat(resultado, equalTo(false));
    assertThat(comodinJugador.getCantidadDisponible(), equalTo(0));
    assertThat(comodinJugador.getVecesUsado(), equalTo(0));
  }

  private void thenElJugadorHaRespondido(boolean respondio) {
    assertThat(respondio, equalTo(true));
  }

  @Test
  public void dadoUnJugadorCuandoRegistraAciertoAumentanAciertosYRacha() {
    // given
    PartidaJugador jugador = new PartidaJugador();

    // when
    servicioPartida.registrarAcierto(jugador);

    // then
    assertThat(jugador.getAciertos(), equalTo(1));
    assertThat(jugador.getRachaRespuestasPartida(), equalTo(1));
  }

  @Test
  public void dadoUnJugadorCuandoRegistraFalloAumentanErroresYSeReiniciaRacha() {
    // given
    PartidaJugador jugador = new PartidaJugador();
    jugador.setRachaRespuestasPartida(3); // tenía racha previa

    // when
    servicioPartida.registrarFallo(jugador);

    // then
    assertThat(jugador.getErrores(), equalTo(1));
    assertThat(jugador.getRachaRespuestasPartida(), equalTo(0));
  }
  /*
  @Test
  public void dadaUnaRondaConVariasRespuestasCuandoBuscoPrimerAciertoObtieneElMasRapido() {
    // given
    PartidaRonda ronda = new PartidaRonda();

    RespuestaJugador r1 = new RespuestaJugador();
    r1.setEsAcierto(true);
    r1.setTiempoRespuestaMs(2000L);

    RespuestaJugador r2 = new RespuestaJugador();
    r2.setEsAcierto(true);
    r2.setTiempoRespuestaMs(1000L); // Más rápido (debe ser el ganador)

    ronda.getRespuestas().add(r1);
    ronda.getRespuestas().add(r2);

    // when
    RespuestaJugador primerAcierto = servicioPartida.obtenerPrimerAcierto(ronda);

    // then
    assertThat(primerAcierto, equalTo(r2));
  }
 */
}
