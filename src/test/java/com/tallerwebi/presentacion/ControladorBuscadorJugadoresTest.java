package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioUsuario;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorBuscadorJugadoresTest {

  private ControladorBuscadorJugadores controladorBuscador;
  private ServicioUsuario servicioUsuarioMock;

  private ModelAndView mav;
  private String busqueda;

  @BeforeEach
  public void init() {
    this.servicioUsuarioMock = mock(ServicioUsuario.class);
    this.controladorBuscador = new ControladorBuscadorJugadores(servicioUsuarioMock);
  }

  @Test
  public void queCuandoElUsuarioElijaBuscarDevuelvaLaVistaBuscarJugadores() {
    // preparacion
    givenUnTerminoDeBusqueda("pepito");

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

  // when
  private void whenElUsuarioBuscaJugadores() {
    this.mav = this.controladorBuscador.buscarJugadores(this.busqueda);
  }

  // then
  private void thenSeMuestraLaVista(String vistaEsperada) {
    assertThat(this.mav.getViewName(), equalTo(vistaEsperada));
  }

  private void thenElControladorUsaElServicioParaBuscar() {
    verify(this.servicioUsuarioMock, times(1)).buscarUsuariosPorNombre(this.busqueda);
  }
}
