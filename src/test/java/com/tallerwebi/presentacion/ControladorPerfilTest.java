package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.modelo.PerfilUsuario;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import com.tallerwebi.dominio.modelo.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPerfilTest {

  private ControladorPerfil controladorPerfil;
  private ServicioUsuario servicioUsuarioMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  private ModelAndView mav;
  private Long idUsuarioEnSesion;

  @BeforeEach
  public void init() {
    this.servicioUsuarioMock = mock(ServicioUsuario.class);
    this.requestMock = mock(HttpServletRequest.class);
    this.sessionMock = mock(HttpSession.class);

    when(this.requestMock.getSession()).thenReturn(sessionMock);
    this.controladorPerfil = new ControladorPerfil(this.servicioUsuarioMock);
  }

  @Test
  public void siUnUsuarioEstaLogueadoQuePuedaVerSuPerfil() {
    // preparacion
    givenUnUsuarioLogueadoConId(1L);

    // ejecucion
    whenElUsuarioIntentaVerSuPerfil();

    // validacion
    thenSeMuestraLaVista("perfil");
    thenElControladorBuscaLosDatosEnElServicio();
  }

  @Test
  public void siUsuarioNoEstaLogueadoQueLoRedirijaAlLogin() {
    // preparacion
    givenUnUsuarioNoLogueado();

    // ejecucion
    whenElUsuarioIntentaVerSuPerfil();

    // validacion
    thenSeMuestraLaVista("redirect:/login");
  }

  // given
  private void givenUnUsuarioLogueadoConId(Long id) {
    this.idUsuarioEnSesion = id;
    Usuario usuarioSimulado = new Usuario();
    usuarioSimulado.setPerfil(new PerfilUsuario());

    when(this.sessionMock.getAttribute("ID_USUARIO")).thenReturn(this.idUsuarioEnSesion);
    when(this.servicioUsuarioMock.buscarUsuarioPorId(this.idUsuarioEnSesion))
      .thenReturn(usuarioSimulado);
  }

  private void givenUnUsuarioNoLogueado() {
    when(this.sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);
  }

  // when
  private void whenElUsuarioIntentaVerSuPerfil() {
    this.mav = this.controladorPerfil.verMiPerfil(this.requestMock);
  }

  // then
  private void thenSeMuestraLaVista(String vistaEsperada) {
    assertThat(this.mav.getViewName(), equalTo(vistaEsperada));
  }

  private void thenElControladorBuscaLosDatosEnElServicio() {
    verify(this.servicioUsuarioMock, times(1)).buscarUsuarioPorId(this.idUsuarioEnSesion);
  }
}
