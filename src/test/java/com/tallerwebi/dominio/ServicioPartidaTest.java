package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

  // ---------- iniciar partida ----------

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
  void deberiaLanzarExcepcionAlIniciarSiElSolicitanteNoEsElHost() {
    // given
    Usuario otroUsuario = givenUnUsuarioConId(2L);

    // when
    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", otroUsuario);

    // then
    assertThat(excepcion, instanceOf(UsuarioNoEsHostException.class));
  }

  @Test
  void deberiaLanzarExcepcionAlIniciarSiLaSalaNoTieneHost() {
    // given
    sala.setHost(null);

    // when
    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", host);

    // then
    assertThat(excepcion, instanceOf(UsuarioNoEsHostException.class));
  }

  // ---------- finalizar partida ----------

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
  void deberiaLanzarExcepcionAlFinalizarUnaPartidaInexistente() {
    // given
    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(null);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.finalizarPartida("ABC123", host)
    );

    // then
    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
  }

  @Test
  void deberiaLanzarExcepcionAlFinalizarUnaPartidaQueNoEstaEnCurso() {
    // given
    Partida partidaFinalizada = new Partida(sala, EstadoPartida.FINALIZADA);
    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partidaFinalizada);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.finalizarPartida("ABC123", host)
    );

    // then
    assertThat(excepcion, instanceOf(IllegalStateException.class));
  }

  @Test
  void deberiaLanzarExcepcionAlFinalizarSiElSolicitanteNoEsElHost() {
    // given
    Partida partidaEnCurso = new Partida(sala, EstadoPartida.EN_CURSO);
    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partidaEnCurso);
    Usuario otroUsuario = givenUnUsuarioConId(2L);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.finalizarPartida("ABC123", otroUsuario)
    );

    // then
    assertThat(excepcion, instanceOf(UsuarioNoEsHostException.class));
  }

  // ---------- buscar partida ----------

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
  void deberiaLanzarExcepcionSiNoExisteLaPartidaPorCodigo() {
    // given
    when(repositorioPartida.obtenerPorCodigoSala("NO-EXISTE")).thenReturn(null);

    // when
    Throwable excepcion = whenBuscarPartidaPorCodigoCapturandoExcepcion("NO-EXISTE");

    // then
    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
  }

  @Test
  void deberiaEncontrarLaPartidaPorId() {
    // given
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    when(repositorioPartida.obtenerPorId(10L)).thenReturn(partida);

    // when
    Partida encontrada = servicioPartida.buscarPartidaPorId(10L);

    // then
    assertThat(encontrada, sameInstance(partida));
  }

  @Test
  void deberiaLanzarExcepcionSiNoExisteLaPartidaPorId() {
    // given
    when(repositorioPartida.obtenerPorId(99L)).thenReturn(null);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.buscarPartidaPorId(99L)
    );

    // then
    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
  }

  // ---------- jugadores listos ----------

  @Test
  void deberiaMarcarAlJugadorComoListo() {
    // given
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    when(repositorioPartida.obtenerPorId(10L)).thenReturn(partida);

    // when
    servicioPartida.marcarJugadorListo(10L, host);

    // then
    SalaJugador jugador = sala.getJugadores().get(0);
    assertThat(jugador.getEstadoJugador(), equalTo(EstadoJugador.LISTO));
  }

  @Test
  void deberiaLanzarExcepcionSiElUsuarioNoPerteneceALaSalaAlMarcarListo() {
    // given
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    when(repositorioPartida.obtenerPorId(10L)).thenReturn(partida);
    Usuario ajeno = givenUnUsuarioConId(2L);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.marcarJugadorListo(10L, ajeno)
    );

    // then
    assertThat(excepcion, instanceOf(JugadorInexistenteExeption.class));
  }

  @Test
  void deberiaDevolverTrueSiTodosLosJugadoresEstanListos() {
    // given
    sala.getJugadores().get(0).setEstadoJugador(EstadoJugador.LISTO);
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    when(repositorioPartida.obtenerPorId(10L)).thenReturn(partida);

    // when
    boolean resultado = servicioPartida.estanTodosListos(10L);

    // then
    assertTrue(resultado);
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

  // ---------- puntaje, aciertos y fallos ----------

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
  public void dadoUnJugadorCuandoSumaPuntosCeroONegativosNoCambiaSuPuntaje() {
    // given
    PartidaJugador jugador = givenUnJugadorConPuntaje(10);

    // when
    whenSumoPuntajeAJugador(jugador, 0);
    whenSumoPuntajeAJugador(jugador, -4);

    // then
    thenElPuntajeDelJugadorEs(jugador, 10);
  }

  @Test
  public void dadoUnJugadorNuloCuandoSumaPuntajeNoLanzaExcepcion() {
    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() -> servicioPartida.sumarPuntaje(null, 5)
    );

    // then
    assertThat(excepcion, is(nullValue()));
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

  @Test
  public void dadoUnJugadorNuloCuandoRegistraAciertoOFalloNoLanzaExcepcion() {
    // when
    Throwable excepcionAcierto = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.registrarAcierto(null)
    );
    Throwable excepcionFallo = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.registrarFallo(null)
    );

    // then
    assertThat(excepcionAcierto, is(nullValue()));
    assertThat(excepcionFallo, is(nullValue()));
  }

  // ---------- comodines ----------

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

  @Test
  public void dadoUnComodinNuloCuandoSeConsumeOSeVerificaNoPuedeUsarse() {
    // when / then
    assertFalse(servicioPartida.consumirComodin(null));
    assertFalse(servicioPartida.puedeUsarse(null));
  }

  @Test
  public void dadoUnComodinConCantidadPuedeUsarse() {
    // given
    PartidaJugadorComodin conStock = givenUnComodinDisponibleParaJugador(2);
    PartidaJugadorComodin sinStock = givenUnComodinDisponibleParaJugador(0);

    // when / then
    assertTrue(servicioPartida.puedeUsarse(conStock));
    assertFalse(servicioPartida.puedeUsarse(sinStock));
  }

  // ---------- helpers de preparación ----------

  private Usuario givenUnUsuarioHost() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    usuario.setUsername("Juan");
    return usuario;
  }

  private Usuario givenUnUsuarioConId(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setUsername("usuario" + id);
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

  // ---------- helpers de ejecución ----------

  private Partida whenInicioLaPartida(String codigoSala, Usuario solicitante) {
    return servicioPartida.iniciarPartida(codigoSala, solicitante);
  }

  private Partida whenFinalizoLaPartida(String codigoSala, Usuario solicitante) {
    return servicioPartida.finalizarPartida(codigoSala, solicitante);
  }

  private Partida whenBuscoLaPartidaPorCodigo(String codigoSala) {
    return servicioPartida.buscarPartidaPorCodigoSala(codigoSala);
  }

  private void whenSumoPuntajeAJugador(PartidaJugador jugador, int puntos) {
    servicioPartida.sumarPuntaje(jugador, puntos);
  }

  private boolean whenConsumoElComodin(PartidaJugadorComodin comodinJugador) {
    return servicioPartida.consumirComodin(comodinJugador);
  }

  private Throwable whenIniciarCapturandoExcepcion(String codigoSala, Usuario solicitante) {
    return whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.iniciarPartida(codigoSala, solicitante)
    );
  }

  private Throwable whenBuscarPartidaPorCodigoCapturandoExcepcion(String codigoSala) {
    return whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.buscarPartidaPorCodigoSala(codigoSala)
    );
  }

  private Throwable whenEjecutoCapturandoExcepcion(Runnable accion) {
    try {
      accion.run();
      return null;
    } catch (RuntimeException excepcion) {
      return excepcion;
    }
  }

  // ---------- helpers de validación ----------

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
}
