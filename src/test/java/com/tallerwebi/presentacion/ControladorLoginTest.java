package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.servicio.ServicioUsuario;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorLoginTest {

  private ControladorLogin controladorLogin;
  private Usuario usuarioMock;
  private DatosLogin datosLoginMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  // Variable correcta
  private ServicioUsuario servicioUsuarioMock;

  @BeforeEach
  public void init() {
    datosLoginMock = new DatosLogin("dami99", "123", "dami@unlam.com", "Damian");
    usuarioMock = mock(Usuario.class);
    when(usuarioMock.getEmail()).thenReturn("dami@unlam.com");
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);

    // Mockeamos el servicio correcto
    servicioUsuarioMock = mock(ServicioUsuario.class);
    controladorLogin = new ControladorLogin(servicioUsuarioMock);
  }

  @Test
  public void loginConUsuarioYPasswordInorrectosDeberiaLlevarALoginNuevamente() {
    // Usamos servicioUsuarioMock y autenticar
    when(servicioUsuarioMock.autenticarUsuario(anyString(), anyString())).thenReturn(null);

    // ejecucion
    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Usuario o clave incorrecta")
    );
    verify(sessionMock, times(0)).setAttribute("ROL", "ADMIN");
  }

  @Test
  public void loginConUsuarioYPasswordCorrectosDeberiaLLevarAHome() {
    // preparacion
    Usuario usuarioEncontradoMock = mock(Usuario.class);
    when(usuarioEncontradoMock.getRol()).thenReturn("ADMIN");
    when(usuarioEncontradoMock.getId()).thenReturn(1L); // Agregamos esto porque tu controlador lo guarda en sesión

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(servicioUsuarioMock.autenticarUsuario(anyString(), anyString()))
      .thenReturn(usuarioEncontradoMock);

    // ejecucion
    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/home"));
    verify(sessionMock, times(1)).setAttribute("ROL", usuarioEncontradoMock.getRol());
  }

  @Test
  public void registrameSiUsuarioNoExisteDeberiaCrearUsuarioYVolverAlLogin()
    throws UsuarioExistente {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.registrarme(datosLoginMock);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));

    // Verificamos que llame al método registrarUsuario con los 4 parámetros
    verify(servicioUsuarioMock, times(1))
      .registrarUsuario(
        datosLoginMock.getUsername(),
        datosLoginMock.getEmail(),
        datosLoginMock.getPassword(),
        datosLoginMock.getNombreCompleto()
      );
  }

  @Test
  public void registrarmeSiUsuarioExisteDeberiaVolverAFormularioYMostrarError()
    throws UsuarioExistente {
    // preparacion - le indicamos que simule tirar el error
    doThrow(UsuarioExistente.class)
      .when(servicioUsuarioMock)
      .registrarUsuario(anyString(), anyString(), anyString(), anyString());

    // ejecucion
    ModelAndView modelAndView = controladorLogin.registrarme(datosLoginMock);

    // validacion - comprobamos que vuelva a la vista 'registro'
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("registro"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("El nombre de usuario ya está en uso. Elegí otro.")
    );
  }

  @Test
  public void errorEnRegistrarmeDeberiaVolverAFormularioYMostrarError() throws UsuarioExistente {
    // preparacion - lanzamos un error general
    doThrow(RuntimeException.class)
      .when(servicioUsuarioMock)
      .registrarUsuario(anyString(), anyString(), anyString(), anyString());

    // ejecucion
    ModelAndView modelAndView = controladorLogin.registrarme(datosLoginMock);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("registro"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Error interno: java.lang.RuntimeException")
    );
  }

  @Test
  public void irALoginDeberiaRetornarVistaLoginConDatosLogin() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.irALogin();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
    assertThat(modelAndView.getModel().get("datosLogin"), instanceOf(DatosLogin.class));
  }

  @Test
  public void nuevoUsuarioDeberiaRetornarVistaNuevoUsuarioConUsuarioVacio() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.nuevoUsuario();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("registro"));
    assertThat(modelAndView.getModel().get("datosLogin"), instanceOf(DatosLogin.class));
  }
}
/*@Test
  public void irAHomeDeberiaRetornarVistaHome() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.irAHome();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("home"));
  }

  @Test
  public void inicioDeberiaRedirigirALogin() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.inicio();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }*/
