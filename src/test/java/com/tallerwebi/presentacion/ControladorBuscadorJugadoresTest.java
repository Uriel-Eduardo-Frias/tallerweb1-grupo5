package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorBuscadorJugadoresTest {

  private ControladorBuscadorJugadores controladorBuscador;
  private ServicioUsuario servicioUsuarioMock;
  private ServicioEstadistica servicioEstadisticaMock;

  private ModelAndView mav;
  private String busqueda;

  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    this.servicioUsuarioMock = mock(ServicioUsuario.class);
    this.servicioEstadisticaMock = mock(ServicioEstadistica.class);
    this.controladorBuscador =
      new ControladorBuscadorJugadores(servicioUsuarioMock, servicioEstadisticaMock);

    this.requestMock = mock(HttpServletRequest.class);
    this.sessionMock = mock(HttpSession.class);
    when(this.requestMock.getSession()).thenReturn(this.sessionMock);
  }

  @Test
  public void queCuandoElUsuarioElijaBuscarDevuelvaLaVistaBuscarJugadores() {
    // preparacion
    givenUnTerminoDeBusqueda("pepito");
    givenUnUsuarioLogueado();

    // ejecucion
    whenElUsuarioBuscaJugadores();

    // validacion
    thenSeMuestraLaVista("buscar-jugadores");
    thenElControladorUsaElServicioParaBuscar();
  }

  // given
  private void givenUnTerminoDeBusqueda(String busqueda) {
    this.busqueda = busqueda;
    when(this.servicioUsuarioMock.buscarUsuariosPorNombre(this.busqueda))
      .thenReturn(new ArrayList<>());
  }

  private void givenUnUsuarioLogueado() {
    when(this.sessionMock.getAttribute("ID_USUARIO")).thenReturn(1L);
  }

  // when
  private void whenElUsuarioBuscaJugadores() {
    this.mav = this.controladorBuscador.buscarJugadores(this.busqueda, this.requestMock);
  }

  // then
  private void thenSeMuestraLaVista(String vistaEsperada) {
    assertThat(this.mav.getViewName(), equalTo(vistaEsperada));
  }

  private void thenElControladorUsaElServicioParaBuscar() {
    verify(this.servicioUsuarioMock, times(1)).buscarUsuariosPorNombre(this.busqueda);
  }
}
