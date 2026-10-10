package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import com.tallerwebi.dominio.enums.EstadoJugador;
import com.tallerwebi.dominio.enums.ModoJuego;
import com.tallerwebi.dominio.excepcion.SalaLlenaException;
import com.tallerwebi.dominio.excepcion.SalaNoEncontradaException;
import com.tallerwebi.dominio.excepcion.UsuarioNoEsHostException;
import com.tallerwebi.dominio.excepcion.UsuarioNoPerteneceASalaException;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.SalaJugador;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioSala;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.impl.ServicioSalaIm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioSalaTest {

  private RepositorioSala repositorioSala;
  private RepositorioUsuario repositorioUsuario;
  private ServicioSalaIm servicioSala;

  @BeforeEach
  void inicializar() {
    repositorioSala = mock(RepositorioSala.class);
    repositorioUsuario = mock(RepositorioUsuario.class);

    servicioSala = new ServicioSalaIm(repositorioSala, repositorioUsuario);
  }

  @Test
  void deberiaLanzarExcepcionCuandoLaSalaAlcanzaSuCapacidadMaxima() {
    // given
    Sala sala = givenUnaSalaLlena();

    // when
    Throwable excepcion = whenUnirseCapturandoExcepcion(sala.getCodigo(), 2L);

    // then
    thenSeLanza(excepcion, SalaLlenaException.class);
    thenLaCantidadDeJugadoresEs(sala, 1);
    thenNoSeGuardaLaSala();
  }

  @Test
  void deberiaLanzarExcepcionSiElUsuarioNoPerteneceALaSala() {
    // given
    Sala sala = givenUnaSalaConUnHost();
    Usuario usuarioAjeno = usuario(2L, "Pedro");
    givenUsuarioPersistido(usuarioAjeno);

    // when
    Throwable excepcion = whenSalirCapturandoExcepcion(sala, usuarioAjeno);

    // then
    thenSeLanza(excepcion, UsuarioNoPerteneceASalaException.class);
    thenLaCantidadDeJugadoresEs(sala, 1);
    thenElHostEs(sala, sala.getHost());
    thenNoSeGuardaLaSala();
  }

  @Test
  void deberiaReasignarElAnfitrionCuandoElAnfitrionSale() {
    // given
    Sala sala = givenUnaSalaConTresJugadores();
    Usuario nuevoHost = sala.getJugadores().get(1).getUsuario();

    // when
    whenElHostSale(sala);

    // then
    thenLaCantidadDeJugadoresEs(sala, 2);
    thenElHostEs(sala, nuevoHost);
    thenElJugadorEsAnfitrion(sala, nuevoHost);
    thenSeGuardaLaSala(sala);
  }

  @Test
  void deberiaDejarLaSalaSinHostCuandoSaleElUltimoIntegrante() {
    // given
    Sala sala = givenUnaSalaConUnHost();

    // when
    whenElHostSale(sala);

    // then
    thenLaCantidadDeJugadoresEs(sala, 0);
    thenLaSalaNoTieneHost(sala);
    thenSeGuardaLaSala(sala);
  }

  @Test
  void deberiaEncontrarUnaSalaPorSuCodigo() {
    // given
    Sala sala = givenUnaSalaPersistida();

    // when
    Sala resultado = servicioSala.buscarPorCodigo(sala.getCodigo());

    // then
    assertThat(resultado, equalTo(sala));
  }

  @Test
  void deberiaLanzarExcepcionSiLaSalaNoExiste() {
    // given
    when(repositorioSala.obtenerPorCodigoConJugadores("TRV-XXXX")).thenReturn(null);

    // when
    Throwable excepcion = whenBuscarSalaCapturandoExcepcion("TRV-XXXX");

    // then
    thenSeLanza(excepcion, SalaNoEncontradaException.class);
  }

  @Test
  void deberiaCrearYGuardarUnaSalaConSuHostPersistido() {
    // given
    Usuario host = usuario(1L, "Juan");
    givenUsuarioPersistido(host);
    when(repositorioSala.obtenerSalaPorCodigo(anyString())).thenReturn(null);

    // when
    Sala salaCreada = servicioSala.crearSala(
      "Trivia del viernes",
      host,
      4,
      5,
      ModoJuego.TURNO_TODOS,
      false
    );

    // then
    assertThat(salaCreada.getNombre(), equalTo("Trivia del viernes"));
    assertThat(salaCreada.getHost(), equalTo(host));
    assertThat(salaCreada.getJugadores(), hasSize(1));
    assertThat(salaCreada.getJugadores().get(0).getUsuario(), equalTo(host));
    thenSeGuardaLaSala(salaCreada);
  }

  @Test
  void deberiaLanzarExcepcionSiElCodigoDeInvitacionEsNulo() {
    // when
    Throwable excepcion = whenUnirseCapturandoExcepcion(null, 2L);

    // then
    thenSeLanza(excepcion, IllegalArgumentException.class);
    thenNoSeGuardaLaSala();
  }

  @Test
  void deberiaLanzarExcepcionSiLaSalaNoExisteAlUnirse() {
    // given
    when(repositorioSala.obtenerPorCodigoConJugadores("NO-EXISTE")).thenReturn(null);

    // when
    Throwable excepcion = whenUnirseCapturandoExcepcion("NO-EXISTE", 2L);

    // then
    thenSeLanza(excepcion, SalaNoEncontradaException.class);
    thenNoSeGuardaLaSala();
  }

  @Test
  void deberiaLanzarExcepcionSiElUsuarioNoEstaPersistidoAlUnirse() {
    // given
    Sala sala = givenUnaSalaConUnHost();
    sala.setMaxJugadores(4);
    when(repositorioUsuario.buscarPorId(2L)).thenReturn(null);

    // when
    Throwable excepcion = whenUnirseCapturandoExcepcion("TRV-1234", 2L);

    // then
    thenSeLanza(excepcion, IllegalArgumentException.class);
    thenLaCantidadDeJugadoresEs(sala, 1);
    thenNoSeGuardaLaSala();
  }

  @Test
  void deberiaReconectarAlJugadorQueYaPerteneceALaSala() {
    // given
    Usuario host = usuario(1L, "Juan");
    Usuario ana = usuario(2L, "Ana");
    Sala sala = nuevaSalaConHost(host);

    SalaJugador participacionAna = new SalaJugador(sala, EstadoJugador.DESCONECTADO, false, ana);
    sala.getJugadores().add(participacionAna);

    when(repositorioSala.obtenerPorCodigoConJugadores("TRV-1234")).thenReturn(sala);

    // when
    Sala resultado = servicioSala.unirse("TRV-1234", ana.getId());

    // then
    assertThat(resultado, equalTo(sala));
    assertThat(participacionAna.getEstadoJugador(), equalTo(EstadoJugador.CONECTADO));
    thenLaCantidadDeJugadoresEs(sala, 2);
    thenSeGuardaLaSala(sala);
  }

  @Test
  void deberiaLanzarExcepcionSiSolicitanteNoEsHostAlCambiarHost() {
    // given
    Sala sala = givenUnaSalaConTresJugadores();
    Usuario solicitante = sala.getJugadores().get(1).getUsuario();
    Usuario nuevoHost = sala.getJugadores().get(2).getUsuario();
    Usuario hostAnterior = sala.getHost();

    // when
    Throwable excepcion = whenCambiarHostCapturandoExcepcion(sala, solicitante, nuevoHost);

    // then
    thenSeLanza(excepcion, UsuarioNoEsHostException.class);
    thenElHostEs(sala, hostAnterior);
    thenNoSeGuardaLaSala();
  }

  @Test
  void deberiaLanzarExcepcionSiElNuevoHostNoPerteneceALaSala() {
    // given
    Sala sala = givenUnaSalaConUnHost();
    Usuario host = sala.getHost();
    Usuario usuarioAjeno = usuario(9L, "Pedro");
    givenUsuarioPersistido(usuarioAjeno);

    // when
    Throwable excepcion = whenCambiarHostCapturandoExcepcion(sala, host, usuarioAjeno);

    // then
    thenSeLanza(excepcion, UsuarioNoPerteneceASalaException.class);
    thenElHostEs(sala, host);
    thenNoSeGuardaLaSala();
  }

  @Test
  void deberiaCambiarElHostYActualizarLasParticipaciones() {
    // given
    Sala sala = givenUnaSalaConTresJugadores();
    Usuario hostAnterior = sala.getHost();
    Usuario nuevoHost = sala.getJugadores().get(1).getUsuario();

    // when
    servicioSala.cambiarHost(sala, hostAnterior, nuevoHost);

    // then
    thenElHostEs(sala, nuevoHost);
    thenElJugadorEsAnfitrion(sala, nuevoHost);
    thenElJugadorNoEsAnfitrion(sala, hostAnterior);
    thenSeGuardaLaSala(sala);
  }

  // Given: preparación

  private Sala givenUnaSalaLlena() {
    Usuario host = usuario(1L, "Juan");
    Sala sala = nuevaSalaConHost(host);
    sala.setMaxJugadores(1);

    when(repositorioSala.obtenerPorCodigoConJugadores("TRV-1234")).thenReturn(sala);

    return sala;
  }

  private Sala givenUnaSalaConUnHost() {
    Usuario host = usuario(1L, "Juan");
    Sala sala = nuevaSalaConHost(host);

    when(repositorioSala.obtenerPorCodigoConJugadores("TRV-1234")).thenReturn(sala);
    givenUsuarioPersistido(host);

    return sala;
  }

  private Sala givenUnaSalaConTresJugadores() {
    Usuario host = usuario(1L, "Juan");
    Usuario ana = usuario(2L, "Ana");
    Usuario pedro = usuario(3L, "Pedro");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);
    sala.setJugadores(new ArrayList<>());
    sala.getJugadores().add(new SalaJugador(sala, EstadoJugador.CONECTADO, true, host));
    sala.getJugadores().add(new SalaJugador(sala, EstadoJugador.CONECTADO, false, ana));
    sala.getJugadores().add(new SalaJugador(sala, EstadoJugador.CONECTADO, false, pedro));

    when(repositorioSala.obtenerPorCodigoConJugadores("TRV-1234")).thenReturn(sala);

    givenUsuarioPersistido(host);
    givenUsuarioPersistido(ana);
    givenUsuarioPersistido(pedro);

    return sala;
  }

  private Sala givenUnaSalaPersistida() {
    Usuario host = usuario(1L, "Juan");
    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);
    sala.setJugadores(new ArrayList<>());

    when(repositorioSala.obtenerPorCodigoConJugadores("TRV-1234")).thenReturn(sala);

    return sala;
  }

  private Sala nuevaSalaConHost(Usuario host) {
    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);
    sala.setJugadores(new ArrayList<>());
    sala.getJugadores().add(new SalaJugador(sala, EstadoJugador.CONECTADO, true, host));
    return sala;
  }

  private void givenUsuarioPersistido(Usuario usuario) {
    when(repositorioUsuario.buscarPorId(usuario.getId())).thenReturn(usuario);
  }

  // When: ejecución

  private Throwable whenUnirseCapturandoExcepcion(String codigo, Long usuarioId) {
    try {
      servicioSala.unirse(codigo, usuarioId);
      return null;
    } catch (RuntimeException excepcion) {
      return excepcion;
    }
  }

  private Throwable whenSalirCapturandoExcepcion(Sala sala, Usuario usuario) {
    try {
      servicioSala.salir(sala, usuario);
      return null;
    } catch (RuntimeException excepcion) {
      return excepcion;
    }
  }

  private void whenElHostSale(Sala sala) {
    servicioSala.salir(sala, sala.getHost());
  }

  private Throwable whenBuscarSalaCapturandoExcepcion(String codigo) {
    try {
      servicioSala.buscarPorCodigo(codigo);
      return null;
    } catch (RuntimeException excepcion) {
      return excepcion;
    }
  }

  private Throwable whenCambiarHostCapturandoExcepcion(
    Sala sala,
    Usuario solicitante,
    Usuario nuevoHost
  ) {
    try {
      servicioSala.cambiarHost(sala, solicitante, nuevoHost);
      return null;
    } catch (RuntimeException excepcion) {
      return excepcion;
    }
  }

  // Then: validación

  private void thenSeLanza(Throwable excepcion, Class<?> tipoEsperado) {
    assertThat(excepcion, instanceOf(tipoEsperado));
  }

  private void thenLaCantidadDeJugadoresEs(Sala sala, int cantidad) {
    assertThat(sala.getJugadores(), hasSize(cantidad));
  }

  private void thenElHostEs(Sala sala, Usuario hostEsperado) {
    assertThat(sala.getHost(), equalTo(hostEsperado));
  }

  private void thenLaSalaNoTieneHost(Sala sala) {
    assertThat(sala.getHost(), nullValue());
  }

  private void thenElJugadorEsAnfitrion(Sala sala, Usuario usuario) {
    SalaJugador participacion = buscarParticipacion(sala, usuario);
    assertThat(participacion, notNullValue());
    assertThat(participacion.isEsAnfitrion(), is(true));
  }

  private void thenElJugadorNoEsAnfitrion(Sala sala, Usuario usuario) {
    SalaJugador participacion = buscarParticipacion(sala, usuario);
    assertThat(participacion, notNullValue());
    assertThat(participacion.isEsAnfitrion(), is(false));
  }

  private SalaJugador buscarParticipacion(Sala sala, Usuario usuario) {
    for (SalaJugador participacion : sala.getJugadores()) {
      if (participacion.getUsuario().getId().equals(usuario.getId())) {
        return participacion;
      }
    }

    return null;
  }

  private void thenSeGuardaLaSala(Sala sala) {
    verify(repositorioSala).guardar(sala);
  }

  private void thenNoSeGuardaLaSala() {
    verify(repositorioSala, never()).guardar(any(Sala.class));
  }

  private Usuario usuario(Long id, String username) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setUsername(username);
    return usuario;
  }
}
