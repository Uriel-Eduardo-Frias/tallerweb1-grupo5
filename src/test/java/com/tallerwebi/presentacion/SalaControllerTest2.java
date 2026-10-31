package com.tallerwebi.presentacion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.tallerwebi.presentacion.dto.CrearSalaDTO;
import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.ModoJuego;
import com.tallerwebi.dominio.excepcion.SalaLlenaException;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.SalaJugador;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioSala;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

@RunWith(MockitoJUnitRunner.class)
public class SalaControllerTest2 {

  @Mock
  private ServicioSala servicioSala;

  @Mock
  private ServicioUsuario servicioUsuario;

  @Mock
  private NotificadorSala notificadorSala;

  @Mock
  private HttpSession session;

  @Mock
  private BindingResult bindingResult;

  @Mock
  private CrearSalaDTO form;

  @Mock
  private Usuario usuario;

  @Mock
  private Sala sala;

  private SalaController controlador;

  @Before
  public void init() {
    controlador = new SalaController(servicioSala, servicioUsuario, notificadorSala);
  }

  @Test
  public void dadoQueHaySalasCuandoLasListoObtengoLaVistaDeSalas() {
    when(servicioSala.listarSalas()).thenReturn(Collections.singletonList(sala));

    ModelAndView resultado = controlador.listarSalas();

    assertEquals("salas-lista", resultado.getViewName());
    assertSame(servicioSala.listarSalas(), resultado.getModel().get("salas"));
  }

  @Test
  public void dadoQueNoHaySesionCuandoMuestroElFormularioRedirijoAlLogin() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView resultado = controlador.mostrarFormularioCrearSala(session);

    assertEquals("redirect:/login", resultado.getViewName());
  }

  @Test
  public void dadoQueHaySesionCuandoMuestroElFormularioObtengoLaVistaCrearSala() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(1L);

    ModelAndView resultado = controlador.mostrarFormularioCrearSala(session);

    assertEquals("crear-sala", resultado.getViewName());
    assertTrue(resultado.getModel().containsKey("crearSalaDTO"));
    assertTrue(resultado.getModel().containsKey("modos"));
  }

  @Test
  public void dadoQueNoHaySesionCuandoCreoUnaSalaRedirijoAlLogin() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView resultado = controlador.crearSala(form, bindingResult, session);

    assertEquals("redirect:/login", resultado.getViewName());
    verifyNoInteractions(servicioSala);
  }

  @Test
  public void dadoQueElUsuarioNoExisteCuandoCreoUnaSalaRedirijoAlLogin() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(1L);
    when(servicioUsuario.buscarUsuarioPorId(1L)).thenReturn(null);

    ModelAndView resultado = controlador.crearSala(form, bindingResult, session);

    assertEquals("redirect:/login", resultado.getViewName());
    verifyNoInteractions(servicioSala);
  }

  @Test
  public void dadoQueElFormularioTieneErroresCuandoCreoUnaSalaVuelvoAlFormulario() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(1L);
    when(servicioUsuario.buscarUsuarioPorId(1L)).thenReturn(usuario);
    when(bindingResult.hasErrors()).thenReturn(true);

    ModelAndView resultado = controlador.crearSala(form, bindingResult, session);

    assertEquals("crear-sala", resultado.getViewName());
    assertSame(form, resultado.getModel().get("crearSalaDTO"));
    verify(servicioSala, never())
      .crearSala(
        anyString(),
        any(Usuario.class),
        anyInt(),
        anyInt(),
        any(ModoJuego.class),
        anyBoolean()
      );
  }

  @Test
  public void dadoQueLosDatosSonValidosCuandoCreoUnaSalaRedirijoALaSala() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(1L);
    when(servicioUsuario.buscarUsuarioPorId(1L)).thenReturn(usuario);
    when(bindingResult.hasErrors()).thenReturn(false);
    when(
      servicioSala.crearSala(
        form.getNombre(),
        usuario,
        form.getMaxJugadores(),
        form.getTotalRondas(),
        form.getModoJuego(),
        form.isEsPrivada()
      )
    )
      .thenReturn(sala);
    when(sala.getCodigo()).thenReturn("ABC123");

    ModelAndView resultado = controlador.crearSala(form, bindingResult, session);

    assertEquals("redirect:/salas/ABC123", resultado.getViewName());
  }

  @Test
  public void dadoQueElServicioFallaCuandoCreoUnaSalaVuelvoAlFormularioConError() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(1L);
    when(servicioUsuario.buscarUsuarioPorId(1L)).thenReturn(usuario);
    when(bindingResult.hasErrors()).thenReturn(false);
    when(
      servicioSala.crearSala(
        form.getNombre(),
        usuario,
        form.getMaxJugadores(),
        form.getTotalRondas(),
        form.getModoJuego(),
        form.isEsPrivada()
      )
    )
      .thenThrow(new IllegalArgumentException("Datos inválidos"));

    ModelAndView resultado = controlador.crearSala(form, bindingResult, session);

    assertEquals("crear-sala", resultado.getViewName());
    assertEquals("Datos inválidos", resultado.getModel().get("error"));
    assertSame(form, resultado.getModel().get("crearSalaDTO"));
  }

  @Test
  public void dadoQueNoHaySesionCuandoVeoUnaSalaRedirijoAlLogin() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView resultado = controlador.verSala("ABC123", session);

    assertEquals("redirect:/login", resultado.getViewName());
    verifyNoInteractions(servicioSala);
  }

  @Test
  public void dadoQueSoyElHostCuandoVeoLaSalaObtengoElDetalle() {
    SalaJugador jugadorHost = mock(SalaJugador.class);

    when(session.getAttribute("ID_USUARIO")).thenReturn(1L);
    when(servicioSala.buscarPorCodigo("ABC123")).thenReturn(sala);
    when(sala.getHost()).thenReturn(usuario);
    when(usuario.getId()).thenReturn(1L);
    when(sala.getJugadores()).thenReturn(Collections.singletonList(jugadorHost));
    when(jugadorHost.getUsuario()).thenReturn(usuario);

    ModelAndView resultado = controlador.verSala("ABC123", session);

    assertEquals("sala-detalle", resultado.getViewName());
    assertEquals(true, resultado.getModel().get("esHost"));
    assertEquals(false, resultado.getModel().get("todosListos"));
  }

  @Test
  public void dadoQueHayUnInvitadoListoCuandoVeoLaSalaTodosEstanListos() {
    Usuario host = mock(Usuario.class);
    Usuario invitado = mock(Usuario.class);
    SalaJugador jugadorHost = mock(SalaJugador.class);
    SalaJugador jugadorInvitado = mock(SalaJugador.class);

    when(session.getAttribute("ID_USUARIO")).thenReturn(1L);
    when(servicioSala.buscarPorCodigo("ABC123")).thenReturn(sala);
    when(sala.getHost()).thenReturn(host);
    when(host.getId()).thenReturn(1L);
    when(invitado.getId()).thenReturn(2L);
    when(sala.getJugadores()).thenReturn(Arrays.asList(jugadorHost, jugadorInvitado));
    when(jugadorHost.getUsuario()).thenReturn(host);
    when(jugadorInvitado.getUsuario()).thenReturn(invitado);
    when(jugadorInvitado.isListo()).thenReturn(true);

    ModelAndView resultado = controlador.verSala("ABC123", session);

    assertEquals(true, resultado.getModel().get("esHost"));
    assertEquals(true, resultado.getModel().get("todosListos"));
    assertEquals(false, resultado.getModel().get("yaUnido"));
  }

  @Test
  public void dadoQueElInvitadoNoEstaListoCuandoVeoLaSalaTodosListosEsFalso() {
    Usuario host = mock(Usuario.class);
    Usuario invitado = mock(Usuario.class);
    SalaJugador jugadorHost = mock(SalaJugador.class);
    SalaJugador jugadorInvitado = mock(SalaJugador.class);

    when(session.getAttribute("ID_USUARIO")).thenReturn(2L);
    when(servicioSala.buscarPorCodigo("ABC123")).thenReturn(sala);
    when(sala.getHost()).thenReturn(host);
    when(host.getId()).thenReturn(1L);
    when(invitado.getId()).thenReturn(2L);
    when(sala.getJugadores()).thenReturn(Arrays.asList(jugadorHost, jugadorInvitado));
    when(jugadorHost.getUsuario()).thenReturn(host);
    when(jugadorInvitado.getUsuario()).thenReturn(invitado);
    when(jugadorInvitado.isListo()).thenReturn(false);

    ModelAndView resultado = controlador.verSala("ABC123", session);

    assertEquals(false, resultado.getModel().get("esHost"));
    assertEquals(true, resultado.getModel().get("yaUnido"));
    assertEquals(false, resultado.getModel().get("todosListos"));
  }

  @Test
  public void dadoQueNoHaySesionCuandoMeUnoAUnaSalaRedirijoAlLogin() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView resultado = controlador.unirse("ABC123", session);

    assertEquals("redirect:/login", resultado.getViewName());
    verifyNoInteractions(notificadorSala);
  }

  @Test
  public void dadoQueMeUnoCorrectamenteCuandoMeUnoAUnaSalaNotificoElIngreso() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(2L);
    when(servicioSala.unirse("ABC123", 2L)).thenReturn(sala);
    when(sala.getCodigo()).thenReturn("ABC123");

    ModelAndView resultado = controlador.unirse("ABC123", session);

    assertEquals("redirect:/salas/ABC123", resultado.getViewName());
    verify(notificadorSala).jugadorSeUnio(sala);
  }

  @Test
  public void dadoQueLaSalaEstaLlenaCuandoMeUnoObtengoLaVistaDeError() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(2L);
    when(servicioSala.unirse("ABC123", 2L)).thenThrow(new SalaLlenaException("sala llena"));

    when(servicioSala.listarSalas()).thenReturn(Collections.emptyList());

    ModelAndView resultado = controlador.unirse("ABC123", session);

    assertEquals("error-unirse", resultado.getViewName());
    assertEquals("Sala llena", resultado.getModel().get("mensaje"));
  }

  @Test
  public void dadoQueLaSalaNoPermiteUnirseCuandoMeUnoObtengoLaVistaDeError() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(2L);
    when(servicioSala.unirse("ABC123", 2L))
      .thenThrow(new IllegalStateException("La sala está cerrada"));
    when(servicioSala.listarSalas()).thenReturn(Collections.emptyList());

    ModelAndView resultado = controlador.unirse("ABC123", session);

    assertEquals("error-unirse", resultado.getViewName());
    assertEquals("La sala está cerrada", resultado.getModel().get("mensaje"));
  }

  @Test
  public void dadoQueNoHaySesionCuandoMarcoListoRedirijoAlLogin() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView resultado = controlador.marcarComoListo("ABC123", session);

    assertEquals("redirect:/login", resultado.getViewName());
    verifyNoInteractions(notificadorSala);
  }

  @Test
  public void dadoQueEstoyEnUnaSalaCuandoMarcoListoNotificoElCambio() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(2L);
    when(servicioSala.marcarListo("ABC123", 2L)).thenReturn(sala);

    ModelAndView resultado = controlador.marcarComoListo("ABC123", session);

    assertEquals("redirect:/salas/ABC123", resultado.getViewName());
    verify(notificadorSala).jugadorListo(sala);
  }

  @Test
  public void dadoQueIngresoDesdeCodigoCuandoMeUnoUtilizoElCodigoIngresado() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(2L);
    when(servicioSala.unirse("XYZ789", 2L)).thenReturn(sala);
    when(sala.getCodigo()).thenReturn("XYZ789");

    ModelAndView resultado = controlador.unirseDesdeCodigo("XYZ789", session);

    assertEquals("redirect:/salas/XYZ789", resultado.getViewName());
    verify(servicioSala).unirse("XYZ789", 2L);
  }
}
