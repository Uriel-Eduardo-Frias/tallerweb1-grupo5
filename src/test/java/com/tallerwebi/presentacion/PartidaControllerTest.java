package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

public class PartidaControllerTest {

  private ServicioPartida servicioPartida;
  private ServicioUsuario servicioUsuario;
  private PartidaController controlador;

  @BeforeEach
  void inicializar() {
    servicioPartida = mock(ServicioPartida.class);
    servicioUsuario = mock(ServicioUsuario.class);
    controlador = new PartidaController(servicioPartida, servicioUsuario);
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlIniciar() {
    // given
    HttpSession session = givenSesionSinUsuario();

    // when
    ModelAndView resultado = controlador.iniciarPartida("TRV-1001", session);

    // then
    thenLaVistaEs(resultado, "redirect:/login");
  }

  @Test
  void deberiaIniciarPartidaYRedirigirALaRonda() {
    // given
    Usuario usuario = givenUsuarioEnSesion(1L);
    Partida partida = new Partida(
      new Sala("TRV-1001", "Sala de prueba", usuario),
      com.tallerwebi.dominio.EstadoPartida.EN_CURSO
    );
    partida.setId(10L);

    when(servicioUsuario.buscarUsuarioPorId(1L)).thenReturn(usuario);
    when(servicioPartida.iniciarPartida("TRV-1001", usuario)).thenReturn(partida);

    // when
    ModelAndView resultado = controlador.iniciarPartida("TRV-1001", sessionDe(usuario));

    // then
    thenLaVistaEs(resultado, "redirect:/partida/10/ronda");
    verify(servicioPartida).iniciarPartida("TRV-1001", usuario);
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlFinalizar() {
    // given
    HttpSession session = givenSesionSinUsuario();

    // when
    ModelAndView resultado = controlador.finalizarPartida("TRV-1001", session);

    // then
    thenLaVistaEs(resultado, "redirect:/login");
  }

  @Test
  void deberiaFinalizarPartidaYMostrarResultado() {
    // given
    Usuario usuario = givenUsuarioEnSesion(1L);
    Partida partida = new Partida(
      new Sala("TRV-1001", "Sala de prueba", usuario),
      com.tallerwebi.dominio.EstadoPartida.FINALIZADA
    );

    when(servicioUsuario.buscarUsuarioPorId(1L)).thenReturn(usuario);
    when(servicioPartida.finalizarPartida("TRV-1001", usuario)).thenReturn(partida);

    // when
    ModelAndView resultado = controlador.finalizarPartida("TRV-1001", sessionDe(usuario));

    // then
    thenLaVistaEs(resultado, "resultado");
    verify(servicioPartida).finalizarPartida("TRV-1001", usuario);
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlMostrarRonda() {
    // given
    HttpSession session = givenSesionSinUsuario();

    // when
    ModelAndView resultado = controlador.mostrarRonda(10L, session);

    // then
    thenLaVistaEs(resultado, "redirect:/login");
  }

  @Test
  void deberiaMostrarLaRondaConElEstadoDeLosJugadores() {
    // given
    Usuario usuario = givenUsuarioEnSesion(1L);
    Partida partida = new Partida(
      new Sala("TRV-1001", "Sala de prueba", usuario),
      com.tallerwebi.dominio.EstadoPartida.EN_CURSO
    );

    when(servicioUsuario.buscarUsuarioPorId(1L)).thenReturn(usuario);
    when(servicioPartida.buscarPartidaPorId(10L)).thenReturn(partida);
    when(servicioPartida.estanTodosListos(10L)).thenReturn(true);

    // when
    ModelAndView resultado = controlador.mostrarRonda(10L, sessionDe(usuario));

    // then
    thenLaVistaEs(resultado, "partida-votacion");
    assertThat(resultado.getModel().get("partida"), equalTo(partida));
    assertThat(resultado.getModel().get("usuarioActual"), equalTo(usuario));
    assertThat(resultado.getModel().get("todosListos"), equalTo(true));
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlMarcarListo() {
    // given
    HttpSession session = givenSesionSinUsuario();

    // when
    ModelAndView resultado = controlador.marcarListo(10L, session);

    // then
    thenLaVistaEs(resultado, "redirect:/login");
  }

  @Test
  void deberiaMarcarListoYRedirigirALaRonda() {
    // given
    Usuario usuario = givenUsuarioEnSesion(1L);
    when(servicioUsuario.buscarUsuarioPorId(1L)).thenReturn(usuario);

    // when
    ModelAndView resultado = controlador.marcarListo(10L, sessionDe(usuario));

    // then
    thenLaVistaEs(resultado, "redirect:/partida/10/ronda");
    verify(servicioPartida).marcarJugadorListo(10L, usuario);
  }

  private HttpSession givenSesionSinUsuario() {
    return new MockHttpSession();
  }

  private Usuario givenUsuarioEnSesion(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setUsername("Juan");
    return usuario;
  }

  private HttpSession sessionDe(Usuario usuario) {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("ID_USUARIO", usuario.getId());
    return session;
  }

  private void thenLaVistaEs(ModelAndView resultado, String vistaEsperada) {
    assertThat(resultado.getViewName(), equalTo(vistaEsperada));
  }
}
