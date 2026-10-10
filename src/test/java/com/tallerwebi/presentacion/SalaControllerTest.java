package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.enums.ModoJuego;
import com.tallerwebi.dominio.excepcion.SalaLlenaException;
import com.tallerwebi.dominio.excepcion.SalaNoEncontradaException;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioSala;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SalaControllerTest {

  private static final String ID_USUARIO = "ID_USUARIO";

  @Mock
  private ServicioUsuario servicioUsuario;

  @Mock
  private ServicioSala servicioSala;

  @Mock
  private NotificadorSala notificadorSala;

  private SalaController salaController;
  private AutoCloseable mocks;

  @BeforeEach
  void inicializar() {
    mocks = MockitoAnnotations.openMocks(this);
    salaController = new SalaController(servicioSala, servicioUsuario, notificadorSala);
  }

  @AfterEach
  void cerrarMocks() throws Exception {
    mocks.close();
  }

  @Test
  void deberiaMostrarLasSalasEnLaLista() {
    // given
    Usuario host = givenUnUsuario(1L, "Juan");
    Sala sala = givenUnaSala("TRV-1234", "Trivia del viernes", host);
    givenUnaListaDeSalas(sala);

    // when
    ModelAndView resultado = whenListarSalas();

    // then
    thenLaVistaEs(resultado, "salas-lista");

    List<Sala> salas = (List<Sala>) resultado.getModel().get("salas");
    assertThat(salas, hasSize(1));
    assertThat(salas.get(0).getCodigo(), equalTo("TRV-1234"));
    assertThat(salas.get(0).getNombre(), equalTo("Trivia del viernes"));
    assertThat(salas.get(0).getHost().getUsername(), equalTo("Juan"));
  }

  @Test
  void deberiaMostrarErrorCuandoLaSalaNoEstaEnEspera() {
    // given
    Usuario invitado = givenUnUsuario(2L, "Ana");

    when(servicioSala.unirse("TRV-1234", invitado.getId()))
      .thenThrow(
        new IllegalStateException("La sala ya no se encuentra en fase de espera o ya comenzó")
      );

    // when
    ModelAndView resultado = whenUnirse("TRV-1234", sessionDe(invitado));

    // then
    thenLaVistaEs(resultado, "error-unirse");
    thenElMensajeEs(resultado, "La sala ya no se encuentra en fase de espera o ya comenzó");
  }

  @Test
  void deberiaRedirigirCuandoElInvitadoSeUneCorrectamente() {
    // given
    Usuario host = givenUnUsuario(1L, "Juan");
    Usuario invitado = givenUnUsuario(2L, "Ana");
    Sala sala = givenUnaSala("TRV-1234", "Trivia del viernes", host);

    givenUsuarioEncontrado(invitado);
    givenSalaAlUnirse("TRV-1234", sala);

    // when
    ModelAndView resultado = whenUnirse(sala.getCodigo(), sessionDe(invitado));

    // then
    thenLaVistaEs(resultado, "redirect:/salas/TRV-1234");
    verify(notificadorSala).jugadorSeUnio(sala);
  }

  @Test
  void deberiaMostrarElFormularioParaCrearUnaSala() {
    // given
    HttpSession session = sessionDe(givenUnUsuario(1L, "Juan"));

    // when
    ModelAndView resultado = whenMostrarFormulario(session);

    // then
    thenLaVistaEs(resultado, "crear-sala");
    assertThat(resultado.getModel().get("crearSalaDTO"), notNullValue());
  }

  @Test
  void deberiaCrearUnaSalaYRedirigirAlLobby() {
    // given
    Usuario host = givenUnUsuario(1L, "Juan");
    CrearSalaDTO formulario = givenUnFormularioValido();
    givenUsuarioEncontrado(host);

    Sala salaCreada = givenServicioCreaSala("Trivia del viernes", host);

    // when
    ModelAndView resultado = whenCrearSala(formulario, sessionDe(host));

    // then
    thenLaVistaEs(resultado, "redirect:/salas/" + salaCreada.getCodigo());
    verify(servicioSala).crearSala("Trivia del viernes", host, 4, 5, ModoJuego.TURNO_TODOS, false);
  }

  @Test
  void deberiaMostrarElDetalleDeLaSalaBuscadaPorCodigo() {
    // given
    Usuario host = givenUnUsuario(1L, "Juan");
    Sala sala = givenUnaSala("TRV-1234", "Trivia del viernes", host);
    when(servicioSala.buscarPorCodigo("TRV-1234")).thenReturn(sala);

    // when
    ModelAndView resultado = whenVerSala("TRV-1234");

    // then
    thenLaVistaEs(resultado, "sala-detalle");
    assertThat(resultado.getModel().get("sala"), equalTo(sala));
  }

  @Test
  void deberiaMostrarErrorCuandoLaSalaEstaLlena() {
    // given
    Usuario host = givenUnUsuario(1L, "Juan");
    Usuario invitado = givenUnUsuario(2L, "Ana");
    Sala sala = givenUnaSala("TRV-1234", "Trivia del viernes", host);

    givenUsuarioEncontrado(invitado);
    givenSalaLlenaAlUnirse("TRV-1234");

    // when
    ModelAndView resultado = whenUnirse(sala.getCodigo(), sessionDe(invitado));

    // then
    thenLaVistaEs(resultado, "error-unirse");
    thenElMensajeEs(resultado, "Sala llena");
  }

  @Test
  void deberiaLanzarExcepcionAlVerUnaSalaInexistente() {
    // given
    when(servicioSala.buscarPorCodigo("TRV-XXXX"))
      .thenThrow(new SalaNoEncontradaException("No existe la sala"));

    // when / then
    assertThrows(SalaNoEncontradaException.class, () -> whenVerSala("TRV-XXXX"));
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlUnirse() {
    // given
    HttpSession session = new MockHttpSession();

    // when
    ModelAndView resultado = salaController.unirse("TRV-1234", session);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioSala, notificadorSala);
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlMostrarFormulario() {
    // given
    HttpSession session = new MockHttpSession();

    // when
    ModelAndView resultado = salaController.mostrarFormularioCrearSala(session);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void deberiaRedirigirAlLoginSiElUsuarioDeSesionNoExisteAlCrearSala() {
    // given
    Usuario usuario = givenUnUsuario(10L, "Juan");
    when(servicioUsuario.buscarUsuarioPorId(10L)).thenReturn(null);

    // when
    ModelAndView resultado = salaController.crearSala(
      givenUnFormularioValido(),
      bindingResultValido(givenUnFormularioValido()),
      sessionDe(usuario)
    );

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void deberiaVolverAlFormularioSiHayErroresDeValidacion() {
    // given
    Usuario usuario = givenUnUsuario(10L, "Juan");
    CrearSalaDTO formulario = givenUnFormularioValido();
    givenUsuarioEncontrado(usuario);

    BindingResult errores = new BeanPropertyBindingResult(formulario, "crearSalaDTO");
    errores.reject("nombre", "Nombre inválido");

    // when
    ModelAndView resultado = salaController.crearSala(formulario, errores, sessionDe(usuario));

    // then
    assertThat(resultado.getViewName(), equalTo("crear-sala"));
    assertThat(resultado.getModel().get("crearSalaDTO"), equalTo(formulario));
    verifyNoInteractions(servicioSala);
  }

  /*  helpers para el given */
  private Usuario givenUnUsuario(Long id, String username) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setUsername(username);
    return usuario;
  }

  private Sala givenUnaSala(String codigo, String nombre, Usuario host) {
    Sala sala = new Sala(codigo, nombre, host);
    sala.setJugadores(new ArrayList<>());
    return sala;
  }

  private void givenUnaListaDeSalas(Sala sala) {
    List<Sala> salas = new ArrayList<>();
    salas.add(sala);
    when(servicioSala.listarSalas()).thenReturn(salas);
  }

  private void givenUsuarioEncontrado(Usuario usuario) {
    when(servicioUsuario.buscarUsuarioPorId(usuario.getId())).thenReturn(usuario);
  }

  private void givenSalaAlUnirse(String codigo, Sala sala) {
    when(servicioSala.unirse(codigo, sala.getHost().getId())).thenReturn(sala);
    when(servicioSala.unirse(codigo, 2L)).thenReturn(sala);
  }

  private void givenSalaLlenaAlUnirse(String codigo) {
    when(servicioSala.unirse(codigo, 2L)).thenThrow(new SalaLlenaException("Sala llena"));
  }

  private CrearSalaDTO givenUnFormularioValido() {
    CrearSalaDTO formulario = new CrearSalaDTO();
    formulario.setNombre("Trivia del viernes");
    formulario.setMaxJugadores(4);
    formulario.setTotalRondas(5);
    formulario.setModoJuego(ModoJuego.TURNO_TODOS);
    formulario.setEsPrivada(false);
    return formulario;
  }

  private Sala givenServicioCreaSala(String nombre, Usuario host) {
    Sala sala = givenUnaSala("TRV-1234", nombre, host);

    when(servicioSala.crearSala(nombre, host, 4, 5, ModoJuego.TURNO_TODOS, false)).thenReturn(sala);

    return sala;
  }

  /*  helpers para otros usos */
  private HttpSession sessionDe(Usuario usuario) {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(ID_USUARIO, usuario.getId());
    return session;
  }

  private BindingResult bindingResultValido(CrearSalaDTO formulario) {
    return new BeanPropertyBindingResult(formulario, "crearSalaDTO");
  }

  /* helpers para ejecución */
  private ModelAndView whenListarSalas() {
    return salaController.listarSalas();
  }

  private ModelAndView whenUnirse(String codigo, HttpSession session) {
    return salaController.unirse(codigo, session);
  }

  private ModelAndView whenMostrarFormulario(HttpSession session) {
    return salaController.mostrarFormularioCrearSala(session);
  }

  private ModelAndView whenCrearSala(CrearSalaDTO formulario, HttpSession session) {
    return salaController.crearSala(formulario, bindingResultValido(formulario), session);
  }

  private ModelAndView whenVerSala(String codigo) {
    return salaController.verSala(codigo);
  }

  /* helpers para validación */
  private void thenLaVistaEs(ModelAndView resultado, String vistaEsperada) {
    assertThat(resultado.getViewName(), equalTo(vistaEsperada));
  }

  private void thenElMensajeEs(ModelAndView resultado, String mensajeEsperado) {
    assertThat(resultado.getModel().get("mensaje"), equalTo(mensajeEsperado));
  }
}
