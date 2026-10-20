package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
  private ServicioRonda servicioRonda;
  private ServicioResultadoPartida servicioResultadoPartida;

  private RepositorioPartida repositorioPartida;
  private RepositorioPartidaJugador repositorioPartidaJugador;

  private Sala sala;
  private Usuario host;

  @BeforeEach
  public void inicializar() {
    servicioSala = mock(ServicioSala.class);
    servicioVotacionCategoria = mock(ServicioVotacionCategoria.class);
    servicioRonda = mock(ServicioRonda.class);
    servicioResultadoPartida = mock(ServicioResultadoPartida.class);

    repositorioPartida = mock(RepositorioPartida.class);
    repositorioPartidaJugador = mock(RepositorioPartidaJugador.class);

    servicioPartida =
      new ServicioPartidaImp(
        servicioSala,
        repositorioPartida,
        servicioVotacionCategoria,
        repositorioPartidaJugador,
        servicioRonda,
        servicioResultadoPartida
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
    Partida partida = servicioPartida.iniciarPartida("ABC123", host);

    thenLaPartidaEstaEnEstado(partida, EstadoPartida.EN_CURSO);
    thenLaSalaEstaEnEstado(sala, EstadoSala.EN_CURSO);
    thenLaPartidaTieneUnaRonda(partida);

    verify(repositorioPartida).guardar(partida);
    verify(repositorioPartidaJugador).guardar(any(PartidaJugador.class));
  }

  @Test
  public void dadoQueLaSalaNoTieneJugadoresCuandoInicioLaPartidaObtengoExcepcion() {
    sala.setJugadores(new ArrayList<>());

    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", host);

    assertThat(excepcion, instanceOf(SalaSinJugadoresException.class));
  }

  @Test
  public void dadoQueLaSalaNoEstaEnEsperaCuandoInicioLaPartidaObtengoExcepcion() {
    sala.setEstado(EstadoSala.EN_CURSO);

    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", host);

    assertThat(excepcion, instanceOf(SalaNoPuedeIniciarseException.class));
  }

  @Test
  public void dadoQueElSolicitanteNoEsElHostCuandoInicioLaPartidaObtengoExcepcion() {
    Usuario otroUsuario = givenUnUsuarioConId(2L);

    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", otroUsuario);

    assertThat(excepcion, instanceOf(UsuarioNoEsHostException.class));
  }

  @Test
  public void dadoQueLaSalaNoTieneHostCuandoInicioLaPartidaObtengoExcepcion() {
    sala.setHost(null);

    Throwable excepcion = whenIniciarCapturandoExcepcion("ABC123", host);

    assertThat(excepcion, instanceOf(UsuarioNoEsHostException.class));
  }

  @Test
  public void dadoQueYaExisteUnaPartidaCuandoInicioNuevamenteObtengoLaExistente() {
    Partida partidaExistente = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partidaExistente);

    Partida resultado = servicioPartida.iniciarPartida("ABC123", host);

    assertThat(resultado, sameInstance(partidaExistente));

    verify(repositorioPartida, never()).guardar(any(Partida.class));
    verify(repositorioPartidaJugador, never()).guardar(any(PartidaJugador.class));
  }

  // --------------------------------------------------
  // FINALIZAR PARTIDA
  // --------------------------------------------------

  @Test
  public void dadoQueHayUnaPartidaEnCursoCuandoLaFinalizoSeDelegaElCierreAlServicioDeResultados() {
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partida);

    Partida resultado = servicioPartida.finalizarPartida("ABC123", host);

    assertThat(resultado, sameInstance(partida));

    verify(servicioResultadoPartida).cerrarPartida(partida.getId());
  }

  @Test
  public void dadoQueNoExisteUnaPartidaCuandoIntentoFinalizarlaObtengoExcepcion() {
    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(null);

    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.finalizarPartida("ABC123", host)
    );

    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
    verify(servicioResultadoPartida, never()).cerrarPartida(any());
  }

  @Test
  public void dadoQueLaPartidaNoEstaEnCursoCuandoIntentoFinalizarlaObtengoExcepcion() {
    Partida partida = new Partida(sala, EstadoPartida.FINALIZADA);

    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partida);

    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.finalizarPartida("ABC123", host)
    );

    assertThat(excepcion, instanceOf(IllegalStateException.class));
    verify(servicioResultadoPartida, never()).cerrarPartida(any());
  }

  @Test
  public void dadoQueElSolicitanteNoEsElHostCuandoFinalizoLaPartidaObtengoExcepcion() {
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    Usuario otroUsuario = givenUnUsuarioConId(2L);

    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partida);

    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.finalizarPartida("ABC123", otroUsuario)
    );

    assertThat(excepcion, instanceOf(UsuarioNoEsHostException.class));
    verify(servicioResultadoPartida, never()).cerrarPartida(any());
  }

  // --------------------------------------------------
  // BÚSQUEDA DE PARTIDAS
  // --------------------------------------------------

  @Test
  public void dadoQueExisteUnaPartidaCuandoLaBuscoPorCodigoObtengoLaPartida() {
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorCodigoSala("ABC123")).thenReturn(partida);

    Partida resultado = servicioPartida.buscarPartidaPorCodigoSala("ABC123");

    assertThat(resultado, sameInstance(partida));
  }

  @Test
  public void dadoQueNoExisteUnaPartidaPorCodigoCuandoLaBuscoObtengoExcepcion() {
    when(repositorioPartida.obtenerPorCodigoSala("NO-EXISTE")).thenReturn(null);

    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.buscarPartidaPorCodigoSala("NO-EXISTE")
    );

    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
  }

  @Test
  public void dadoQueExisteUnaPartidaCuandoLaBuscoPorIdObtengoLaPartida() {
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    Partida resultado = servicioPartida.buscarPartidaPorId(10L);

    assertThat(resultado, sameInstance(partida));
  }

  @Test
  public void dadoQueNoExisteUnaPartidaPorIdCuandoLaBuscoObtengoExcepcion() {
    when(repositorioPartida.obtenerPorIdConRondas(99L)).thenReturn(null);

    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.buscarPartidaPorId(99L)
    );

    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
  }

  // --------------------------------------------------
  // JUGADORES LISTOS
  // --------------------------------------------------

  @Test
  public void dadoQueUnJugadorPerteneceALaSalaCuandoSeMarcaListoObtengoEstadoListo() {
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    servicioPartida.marcarJugadorListo(10L, host);

    SalaJugador jugador = sala.getJugadores().get(0);

    assertThat(jugador.getEstadoJugador(), equalTo(EstadoJugador.LISTO));
  }

  @Test
  public void dadoQueUnUsuarioNoPerteneceALaSalaCuandoSeMarcaListoObtengoExcepcion() {
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    Usuario ajeno = givenUnUsuarioConId(2L);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.marcarJugadorListo(10L, ajeno)
    );

    assertThat(excepcion, instanceOf(JugadorInexistenteExeption.class));
  }

  @Test
  public void dadoQueTodosLosJugadoresEstanListosCuandoLosConsultoObtengoTrue() {
    sala.getJugadores().get(0).setEstadoJugador(EstadoJugador.LISTO);
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    assertTrue(servicioPartida.estanTodosListos(10L));
  }

  @Test
  public void dadoQueNoHayJugadoresCuandoConsultoSiEstanListosObtengoFalse() {
    sala.setJugadores(new ArrayList<>());
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    assertFalse(servicioPartida.estanTodosListos(10L));
  }

  @Test
  public void dadoQueUnJugadorNoEstaListoCuandoConsultoSiTodosLoEstanObtengoFalse() {
    sala.getJugadores().get(0).setEstadoJugador(EstadoJugador.CONECTADO);
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    assertFalse(servicioPartida.estanTodosListos(10L));
  }

  @Test
  public void dadoQueNoExisteUnaPartidaCuandoMarcoUnJugadorListoObtengoExcepcion() {
    when(repositorioPartida.obtenerPorIdConRondas(99L)).thenReturn(null);

    Throwable excepcion = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.marcarJugadorListo(99L, host)
    );

    assertThat(excepcion, instanceOf(PartidaNoEncontradaException.class));
  }

  // --------------------------------------------------
  // CONSULTAS PARA LA VISTA
  // --------------------------------------------------

  @Test
  public void dadoQueElUsuarioEsElHostCuandoConsultoSiEsHostObtengoTrue() {
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    assertTrue(servicioPartida.esHost(10L, 1L));
  }

  @Test
  public void dadoQueElUsuarioNoEsElHostCuandoConsultoSiEsHostObtengoFalse() {
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    assertFalse(servicioPartida.esHost(10L, 2L));
  }

  @Test
  public void dadoQueLaSalaNoTieneHostCuandoConsultoSiEsHostObtengoFalse() {
    sala.setHost(null);
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    when(repositorioPartida.obtenerPorIdConRondas(10L)).thenReturn(partida);

    assertFalse(servicioPartida.esHost(10L, 1L));
  }

  // --------------------------------------------------
  // PUNTAJES Y ESTADÍSTICAS
  // --------------------------------------------------

  @Test
  public void dadoUnJugadorCuandoSumaPuntajePositivoObtienePuntajeIncrementado() {
    PartidaJugador jugador = givenUnJugadorConPuntaje(10);

    servicioPartida.sumarPuntaje(jugador, 5);

    thenElPuntajeDelJugadorEs(jugador, 15);
  }

  @Test
  public void dadoUnJugadorCuandoSumaPuntosCeroONegativosNoCambiaSuPuntaje() {
    PartidaJugador jugador = givenUnJugadorConPuntaje(10);

    servicioPartida.sumarPuntaje(jugador, 0);
    servicioPartida.sumarPuntaje(jugador, -4);

    thenElPuntajeDelJugadorEs(jugador, 10);
  }

  @Test
  public void dadoUnJugadorNuloCuandoSumaPuntajeNoLanzaExcepcion() {
    Throwable excepcion = whenEjecutoCapturandoExcepcion(() -> servicioPartida.sumarPuntaje(null, 5)
    );

    assertThat(excepcion, is(nullValue()));
  }

  @Test
  public void dadoUnJugadorCuandoRegistraAciertoAumentanAciertosYRacha() {
    PartidaJugador jugador = new PartidaJugador();

    servicioPartida.registrarAcierto(jugador);

    assertThat(jugador.getAciertos(), equalTo(1));
    assertThat(jugador.getRachaRespuestasPartida(), equalTo(1));
  }

  @Test
  public void dadoUnJugadorCuandoRegistraFalloAumentanErroresYSeReiniciaRacha() {
    PartidaJugador jugador = new PartidaJugador();
    jugador.setRachaRespuestasPartida(3);

    servicioPartida.registrarFallo(jugador);

    assertThat(jugador.getErrores(), equalTo(1));
    assertThat(jugador.getRachaRespuestasPartida(), equalTo(0));
  }

  @Test
  public void dadoUnJugadorNuloCuandoRegistraAciertoOFalloNoLanzaExcepcion() {
    Throwable excepcionAcierto = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.registrarAcierto(null)
    );

    Throwable excepcionFallo = whenEjecutoCapturandoExcepcion(() ->
      servicioPartida.registrarFallo(null)
    );

    assertThat(excepcionAcierto, is(nullValue()));
    assertThat(excepcionFallo, is(nullValue()));
  }

  // --------------------------------------------------
  // COMODINES
  // --------------------------------------------------

  @Test
  public void dadoUnComodinConCantidadCuandoSeConsumeReduceCantidadYAumentaUso() {
    PartidaJugadorComodin comodin = givenUnComodinDisponibleParaJugador(1);

    boolean resultado = servicioPartida.consumirComodin(comodin);

    thenElComodinFueConsumidoConExito(resultado, comodin);
  }

  @Test
  public void dadoUnComodinSinCantidadCuandoSeConsumeNoPuedeUsarse() {
    PartidaJugadorComodin comodin = givenUnComodinDisponibleParaJugador(0);

    boolean resultado = servicioPartida.consumirComodin(comodin);

    thenElComodinNoPudoConsumirse(resultado, comodin);
  }

  @Test
  public void dadoUnComodinNuloCuandoSeConsumeOVerificaNoPuedeUsarse() {
    assertFalse(servicioPartida.consumirComodin(null));
    assertFalse(servicioPartida.puedeUsarse(null));
  }

  @Test
  public void dadoUnComodinConCantidadPuedeUsarse() {
    PartidaJugadorComodin conStock = givenUnComodinDisponibleParaJugador(2);
    PartidaJugadorComodin sinStock = givenUnComodinDisponibleParaJugador(0);

    assertTrue(servicioPartida.puedeUsarse(conStock));
    assertFalse(servicioPartida.puedeUsarse(sinStock));
  }

  // --------------------------------------------------
  // DELEGACIÓN DE RONDAS
  // --------------------------------------------------

  @Test
  public void dadoQueTodosRespondieronCuandoFinalizoLaRondaObtengoElResultadoDelServicioDeRondas() {
    when(servicioRonda.finalizarRondaSiTodosRespondieron(10L)).thenReturn(true);

    boolean resultado = servicioPartida.finalizarRondaSiTodosRespondieron(10L);

    assertTrue(resultado);

    verify(servicioRonda).finalizarRondaSiTodosRespondieron(10L);
  }

  @Test
  public void dadoQueFaltanRespuestasCuandoFinalizoLaRondaObtengoFalse() {
    when(servicioRonda.finalizarRondaSiTodosRespondieron(10L)).thenReturn(false);

    boolean resultado = servicioPartida.finalizarRondaSiTodosRespondieron(10L);

    assertFalse(resultado);
  }

  @Test
  public void dadoQueAvanzoLaRondaSeDelegaLaOperacionAlServicioDeRondas() {
    servicioPartida.avanzarRonda(10L, 1L);

    verify(servicioRonda).avanzarRonda(10L, 1L);
  }

  // --------------------------------------------------
  // DELEGACIÓN DE VOTACIÓN DE CATEGORÍAS
  // --------------------------------------------------

  @Test
  public void dadoUnVotoValidoCuandoRegistroLaCategoriaObtengoElResultadoDelServicio() {
    when(servicioVotacionCategoria.registrarVotoCategoria(10L, 1L, 5L)).thenReturn(true);

    boolean resultado = servicioPartida.registrarVotoCategoria(10L, 1L, 5L);

    assertTrue(resultado);

    verify(servicioVotacionCategoria).registrarVotoCategoria(10L, 1L, 5L);
  }

  @Test
  public void dadoQueFinalizoLaVotacionCuandoInvocoElServicioSeDelegaLaOperacion() {
    servicioPartida.finalizarVotacionCategoria(10L, 1L);

    verify(servicioVotacionCategoria).finalizarVotacionCategoria(10L, 1L);
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
