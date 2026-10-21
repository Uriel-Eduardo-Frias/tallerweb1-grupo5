package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorHomeTest {

  private ControladorHome controladorHome;
  private ModelAndView mav;

  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private ServicioEstadistica servicioEstadisticaMock;

  @BeforeEach
  public void init() {
    this.servicioEstadisticaMock = mock(ServicioEstadistica.class);
    this.controladorHome = new ControladorHome(this.servicioEstadisticaMock);

    this.requestMock = mock(HttpServletRequest.class);
    this.sessionMock = mock(HttpSession.class);
    when(this.requestMock.getSession()).thenReturn(this.sessionMock);
  }

  @Test
  public void queLaRutaRaizRedirijaAlHome() {
    whenElUsuarioEntraALaRaiz();
    thenSeMuestraLaVista("redirect:/home");
  }

  @Test
  public void queAlIrAlHomeDevuelvaLaVistaDelHomeParaCualquierUsuario() {
    whenElUsuarioVaAlHome();
    thenSeMuestraLaVista("home");
  }

  // when
  private void whenElUsuarioEntraALaRaiz() {
    this.mav = this.controladorHome.inicio();
  }

  private void whenElUsuarioVaAlHome() {
    this.mav = this.controladorHome.irAlHome();
  }

  // then
  private void thenSeMuestraLaVista(String vistaEsperada) {
    assertThat(this.mav.getViewName(), equalTo(vistaEsperada));
  }
}
