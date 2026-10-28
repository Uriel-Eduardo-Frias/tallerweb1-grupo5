package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.enumeration.TipoLogro;
import com.tallerwebi.dominio.modelo.Logro;
import com.tallerwebi.dominio.servicio.ServicioLogro;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorLogrosTest {

  private static final Long USUARIO_ID = 10L;

  private ControladorLogros controlador;
  private ServicioLogro servicioLogroMock;

  private ModelAndView mav;
  private List<Logro> logrosEsperados;

  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    this.servicioLogroMock = mock(ServicioLogro.class);
    this.controlador = new ControladorLogros(this.servicioLogroMock);

    this.requestMock = mock(HttpServletRequest.class);
    this.sessionMock = mock(HttpSession.class);
    when(this.requestMock.getSession()).thenReturn(this.sessionMock);
  }

  @Test
  public void queCuandoNoHayUsuarioEnSesionRedirijaAlLogin() {
    // preparacion
    givenUnUsuarioNoLogueado();

    // ejecucion
    whenElUsuarioQuiereVerSusLogros();

    // validacion
    thenSeRedirigeALaVista("redirect:/login");
    thenElServicioDeLogrosNoEsInvocado();
  }

  @Test
  public void queCuandoElIdDeSesionNoEsValidoRedirijaAlLogin() {
    // preparacion
    givenUnUsuarioConIdDeSesionInvalido("10"); // String en lugar de Long

    // ejecucion
    whenElUsuarioQuiereVerSusLogros();

    // validacion
    thenSeRedirigeALaVista("redirect:/login");
    thenElServicioDeLogrosNoEsInvocado();
  }

  @Test
  public void queCuandoHayUsuarioLogueadoMuestreLaVistaLogrosConSusLogros() {
    // preparacion
    givenUnUsuarioLogueado();
    givenQueElUsuarioTieneLogrosDisponibles();

    // ejecucion
    whenElUsuarioQuiereVerSusLogros();

    // validacion
    thenSeMuestraLaVista("logros");
    thenLaVistaContieneLosLogrosDelUsuario();
    thenElControladorBuscaLosLogrosDelUsuario();
  }

  @Test
  public void queCuandoPidaLosLogrosUnicamenteConsulteLosDelUsuarioLogueado() {
    // preparacion
    givenUnUsuarioLogueado();
    givenQueElUsuarioNoTieneLogros();

    // ejecucion
    whenElUsuarioQuiereVerSusLogros();

    // validacion
    thenElControladorBuscaUnicamenteLosLogrosDelUsuario();
  }

  // given
  private void givenUnUsuarioNoLogueado() {
    when(this.sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);
  }

  private void givenUnUsuarioConIdDeSesionInvalido(String idInvalido) {
    when(this.sessionMock.getAttribute("ID_USUARIO")).thenReturn(idInvalido);
  }

  private void givenUnUsuarioLogueado() {
    when(this.sessionMock.getAttribute("ID_USUARIO")).thenReturn(USUARIO_ID);
  }

  private void givenQueElUsuarioTieneLogrosDisponibles() {
    this.logrosEsperados = List.of(new Logro(TipoLogro.EN_RACHA, null));
    when(this.servicioLogroMock.obtenerLogrosDisponibles(USUARIO_ID))
      .thenReturn(this.logrosEsperados);
  }

  private void givenQueElUsuarioNoTieneLogros() {
    this.logrosEsperados = new ArrayList<>();
    when(this.servicioLogroMock.obtenerLogrosDisponibles(USUARIO_ID))
      .thenReturn(this.logrosEsperados);
  }

  // when
  private void whenElUsuarioQuiereVerSusLogros() {
    this.mav = this.controlador.irALogros(this.requestMock);
  }

  // then
  private void thenSeRedirigeALaVista(String vistaEsperada) {
    assertThat(this.mav.getViewName(), equalTo(vistaEsperada));
  }

  private void thenSeMuestraLaVista(String vistaEsperada) {
    assertThat(this.mav.getViewName(), equalTo(vistaEsperada));
  }

  private void thenLaVistaContieneLosLogrosDelUsuario() {
    assertThat(this.mav.getModel().get("logros"), sameInstance(this.logrosEsperados));
  }

  private void thenElServicioDeLogrosNoEsInvocado() {
    verifyNoInteractions(this.servicioLogroMock);
  }

  private void thenElControladorBuscaLosLogrosDelUsuario() {
    verify(this.servicioLogroMock, times(1)).obtenerLogrosDisponibles(USUARIO_ID);
  }

  private void thenElControladorBuscaUnicamenteLosLogrosDelUsuario() {
    verify(this.servicioLogroMock, only()).obtenerLogrosDisponibles(USUARIO_ID);
  }
}
