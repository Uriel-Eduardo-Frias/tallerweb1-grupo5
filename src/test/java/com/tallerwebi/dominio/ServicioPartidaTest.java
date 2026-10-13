package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.enumeration.*;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.repositorio.*;
import com.tallerwebi.dominio.servicio.*;
import com.tallerwebi.dominio.servicio.impl.ServicioPartidaImp;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPartidaTest {

  private ServicioPartida servicioPartida;
  private ServicioSala servicioSala;
  private ServicioVotacionCategoria servicioVotacionCategoria;

  private RepositorioPartida repositorioPartida;
  private RepositorioPartidaJugador repositorioPartidaJugador;
  private RepositorioRespuestaJugador repositorioRespuestaJugador;
  private NotificadorSala notificadorSala;

  private Sala sala;
  private Usuario host;

  @BeforeEach
  public void inicializar() {
    servicioSala = mock(ServicioSala.class);
    servicioVotacionCategoria = mock(ServicioVotacionCategoria.class);

    repositorioPartida = mock(RepositorioPartida.class);
    repositorioPartidaJugador = mock(RepositorioPartidaJugador.class);
    repositorioRespuestaJugador = mock(RepositorioRespuestaJugador.class);
    notificadorSala = mock(NotificadorSala.class);

    servicioPartida =
      new ServicioPartidaImp(
        servicioSala,
        repositorioPartida,
        servicioVotacionCategoria,
        repositorioPartidaJugador,
        repositorioRespuestaJugador,
        notificadorSala
      );

    host = givenUnUsuarioHost();
    sala = givenUnaSalaEnEsperaConUnJugador(host);

    when(servicioSala.buscarPorCodigo("ABC123")).thenReturn(sala);
  }

  // --------------------------------------------------
  // INICIAR PARTIDA
  // --------------------------------------------------

  @Test
  public void dadoQueHayUnaSalaEnEsperaCuandoInicioLaPartidaObtengoPartidaEnCurso() {
    // given: la sala está preparada en inicializar().

    // when
    Partida partida = servicioPartida.iniciarPartida("ABC123", host);

    // then
    thenLaPartidaEstaEnEstado(partida, EstadoPartida.EN_CURSO);
    thenLaSalaEstaEnEstado(sala, EstadoSala.EN_CURSO);
    thenLaPartidaTieneUnaRonda(partida);

    verify(repositorioPartida, times(1)).guardar(partida);
    verify(repositorioPartidaJugador, times(1)).guardar(any(PartidaJugador.class));
  }

  @Test
  public void dadoQueLaSalaNoTieneJugadoresCuandoInicioLaPartidaObtengoExcepcion() {
    // given
    sala.setJugadores(new ArrayList<>());

    // when
    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", host);

    // then
    assertThat(excepcion, instanceOf(SalaSinJugadoresException.class));
  }

  @Test
  public void dadoQueLaSalaNoEstaEnEsperaCuandoInicioLaPartidaObtengoExcepcion() {
    // given
    sala.setEstado(EstadoSala.EN_CURSO);

    // when
    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", host);

    // then
    assertThat(excepcion, instanceOf(SalaNoPuedeIniciarseException.class));
  }

  @Test
  public void dadoQueElSolicitanteNoEsElHostCuandoInicioLaPartidaObtengoExcepcion() {
    // given
    Usuario otroUsuario = givenUnUsuarioConId(2L);

    // when
    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", otroUsuario);

    // then
    assertThat(excepcion, instanceOf(UsuarioNoEsHostException.class));
  }

  @Test
  public void dadoQueLaSalaNoTieneHostCuandoInicioLaPartidaObtengoExcepcion() {
    // given
    sala.setHost(null);

    // when
    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", host);

    // then
    assertThat(excepcion, instanceOf(UsuarioNoEsHostException.class));
  }

  @Test
  public void dadoQueYaExisteUnaPartidaCuandoInicioNuevamenteObtengoLaExistente() {
    // given
    Partida partidaExistente = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partidaExistente);

    // when
    Partida resultado = servicioPartida.iniciarPartida("ABC123", host);

    // then
    assertThat(resultado, sameInstance(partidaExistente));

    verify(repositorioPartida, never()).guardar(any(Partida.class));
    verify(repositorioPartidaJugador, never()).guardar(any(PartidaJugador.class));
  }

  // --------------------------------------------------
  // FINALIZAR PARTIDA
  // --------------------------------------------------

  @Test
  public void dadoQueHayUnaPartidaEnCursoCuandoLaFinalizoObtengoPartidaFinalizada() {
    // given
    Partida partida = servicioPartida.iniciarPartida("ABC123", host);

    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partida);

    // when
    Partida resultado = servicioPartida.finalizarPartida("ABC123", host);

    // then
    thenLaPartidaEstaEnEstado(resultado, EstadoPartida.FINALIZADA);
    thenLaSalaEstaEnEstado(sala, EstadoSala.FINALIZADA);

    verify(repositorioPartida, times(2)).guardar(partida);
  }

  @Test
  public void dadoQueNoExisteUnaPartidaCuandoIntentoFinalizarlaObtengoExcepcion() {
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
  public void dadoQueLaPartidaNoEstaEnCursoCuandoIntentoFinalizarlaObtengoExcepcion() {
    // given
    Partida partida = new Partida(sala, EstadoPartida.FINALIZADA);

    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partida);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.finalizarPartida("ABC123", host)
    );

    // then
    assertThat(excepcion, instanceOf(IllegalStateException.class));
  }

  @Test
  public void dadoQueElSolicitanteNoEsElHostCuandoFinalizoLaPartidaObtengoExcepcion() {
    // given
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    Usuario otroUsuario = givenUnUsuarioConId(2L);

    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partida);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.finalizarPartida("ABC123", otroUsuario)
    );

    // then
    assertThat(excepcion, instanceOf(UsuarioNoEsHostException.class));
  }

  // --------------------------------------------------
  // BÚSQUEDA DE PARTIDAS
  // --------------------------------------------------

  @Test
  public void dadoQueExisteUnaPartidaCuandoLaBuscoPorCodigoObtengoLaPartida() {
    // given
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partida);

    // when
    Partida resultado = servicioPartida.buscarPartidaPorCodigoSala("ABC123");

    // then
    assertThat(resultado, sameInstance(partida));
  }

  @Test
  public void dadoQueNoExisteUnaPartidaPorCodigoCuandoLaBuscoObtengoExcepcion() {
    // given
    when(repositorioPartida.obtenerPorCodigoSala("NO-EXISTE")).thenReturn(null);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.buscarPartidaPorCodigoSala("NO-EXISTE")
    );

    // then
    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
  }

  @Test
  public void dadoQueExisteUnaPartidaCuandoLaBuscoPorIdObtengoLaPartida() {
    // given
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    // when
    Partida resultado = servicioPartida.buscarPartidaPorId(10L);

    // then
    assertThat(resultado, sameInstance(partida));
  }

  @Test
  public void dadoQueNoExisteUnaPartidaPorIdCuandoLaBuscoObtengoExcepcion() {
    // given
    when(repositorioPartida.obtenerPorIdConRondas(99L)).thenReturn(null);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.buscarPartidaPorId(99L)
    );

    // then
    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
  }

  // --------------------------------------------------
  // JUGADORES LISTOS
  // --------------------------------------------------

  @Test
  public void dadoQueUnJugadorPerteneceALaSalaCuandoSeMarcaListoObtengoEstadoListo() {
    // given
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    // when
    servicioPartida.marcarJugadorListo(10L, host);

    // then
    SalaJugador jugador = sala.getJugadores().get(0);

    assertThat(jugador.getEstadoJugador(), equalTo(EstadoJugador.LISTO));
  }

  @Test
  public void dadoQueUnUsuarioNoPerteneceALaSalaCuandoSeMarcaListoObtengoExcepcion() {
    // given
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    Usuario ajeno = givenUnUsuarioConId(2L);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.marcarJugadorListo(10L, ajeno)
    );

    // then
    assertThat(excepcion, instanceOf(JugadorInexistenteExeption.class));
  }

  @Test
  public void dadoQueTodosLosJugadoresEstanListosCuandoLosConsultoObtengoTrue() {
    // given
    sala.getJugadores().get(0).setEstadoJugador(EstadoJugador.LISTO);

    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    // when
    boolean resultado = servicioPartida.estanTodosListos(10L);

    // then
    assertTrue(resultado);
  }

  @Test
  public void dadoQueNoHayJugadoresCuandoConsultoSiEstanListosObtengoFalse() {
    // given
    sala.setJugadores(new ArrayList<>());

    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    // when
    boolean resultado = servicioPartida.estanTodosListos(10L);

    // then
    assertFalse(resultado);
  }

  @Test
  public void dadoQueUnJugadorNoEstaListoCuandoConsultoSiTodosLoEstanObtengoFalse() {
    // given
    sala.getJugadores().get(0).setEstadoJugador(EstadoJugador.CONECTADO);

    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    // when
    boolean resultado = servicioPartida.estanTodosListos(10L);

    // then
    assertFalse(resultado);
  }

  @Test
  public void dadoQueNoExisteUnaPartidaCuandoMarcoUnJugadorListoObtengoExcepcion() {
    // given
    when(repositorioPartida.obtenerPorIdConRondas(99L)).thenReturn(null);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.marcarJugadorListo(99L, host)
    );

    // then
    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
  }

  // --------------------------------------------------
  // FINALIZAR RONDA AUTOMÁTICAMENTE
  // --------------------------------------------------

  @Test
  public void dadoQueNoExisteUnaPartidaCuandoIntentoFinalizarLaRondaObtengoExcepcion() {
    // given
    when(repositorioPartida.obtenerPorIdConRondas(99L)).thenReturn(null);

    // when
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.finalizarRondaSiTodosRespondieron(99L)
    );

    // then
    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
  }

  // --------------------------------------------------
  // PUNTAJES Y ESTADÍSTICAS
  // --------------------------------------------------

  @Test
  public void dadoUnJugadorCuandoSumaPuntajePositivoObtienePuntajeIncrementado() {
    // given
    PartidaJugador jugador = givenUnJugadorConPuntaje(10);

    // when
    servicioPartida.sumarPuntaje(jugador, 5);

    // then
    thenElPuntajeDelJugadorEs(jugador, 15);
  }

  @Test
  public void dadoUnJugadorCuandoSumaPuntosCeroONegativosNoCambiaSuPuntaje() {
    // given
    PartidaJugador jugador = givenUnJugadorConPuntaje(10);

    // when
    servicioPartida.sumarPuntaje(jugador, 0);
    servicioPartida.sumarPuntaje(jugador, -4);

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
    jugador.setRachaRespuestasPartida(3);

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

  // --------------------------------------------------
  // COMODINES
  // --------------------------------------------------

  @Test
  public void dadoUnComodinConCantidadCuandoSeConsumeReduceCantidadYAumentaUso() {
    // given
    PartidaJugadorComodin comodin = givenUnComodinDisponibleParaJugador(1);

    // when
    boolean resultado = servicioPartida.consumirComodin(comodin);

    // then
    thenElComodinFueConsumidoConExito(resultado, comodin);
  }

  @Test
  public void dadoUnComodinSinCantidadCuandoSeConsumeNoPuedeUsarse() {
    // given
    PartidaJugadorComodin comodin = givenUnComodinDisponibleParaJugador(0);

    // when
    boolean resultado = servicioPartida.consumirComodin(comodin);

    // then
    thenElComodinNoPudoConsumirse(resultado, comodin);
  }

  @Test
  public void dadoUnComodinNuloCuandoSeConsumeOVerificaNoPuedeUsarse() {
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

  // --------------------------------------------------
  // DELEGACIÓN DE VOTACIÓN DE CATEGORÍAS
  // --------------------------------------------------

  @Test
  public void dadoUnVotoValidoCuandoRegistroLaCategoriaObtengoElResultadoDelServicio() {
    // given
    when(servicioVotacionCategoria.registrarVotoCategoria(10L, 1L, 5L)).thenReturn(true);

    // when
    boolean resultado = servicioPartida.registrarVotoCategoria(10L, 1L, 5L);

    // then
    assertTrue(resultado);

    verify(servicioVotacionCategoria, times(1)).registrarVotoCategoria(10L, 1L, 5L);
  }

  @Test
  public void dadoQueFinalizoLaVotacionCuandoInvocoElServicioSeDelegaLaOperacion() {
    // when
    servicioPartida.finalizarVotacionCategoria(10L, 1L);

    // then
    verify(servicioVotacionCategoria, times(1)).finalizarVotacionCategoria(10L, 1L);
  }

  // --------------------------------------------------
  // HELPERS DE PREPARACIÓN
  // --------------------------------------------------

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

  // --------------------------------------------------
  // HELPERS DE VALIDACIÓN
  // --------------------------------------------------

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

  private void thenElPuntajeDelJugadorEs(PartidaJugador jugador, int puntajeEsperado) {
    assertThat(jugador.getPuntajeAcumulado(), equalTo(puntajeEsperado));
  }

  private void thenElComodinFueConsumidoConExito(boolean resultado, PartidaJugadorComodin comodin) {
    assertThat(resultado, equalTo(true));
    assertThat(comodin.getCantidadDisponible(), equalTo(0));
    assertThat(comodin.getVecesUsado(), equalTo(1));
  }

  private void thenElComodinNoPudoConsumirse(boolean resultado, PartidaJugadorComodin comodin) {
    assertThat(resultado, equalTo(false));
    assertThat(comodin.getCantidadDisponible(), equalTo(0));
    assertThat(comodin.getVecesUsado(), equalTo(0));
  }

  // --------------------------------------------------
  // HELPER PARA CAPTURAR EXCEPCIONES
  // --------------------------------------------------

  private Throwable whenIniciarCapturandoExcepcion(String codigoSala, Usuario solicitante) {
    return whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.iniciarPartida(codigoSala, solicitante)
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
}
