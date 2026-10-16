package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.ServicioPartidaPregunta;
import com.tallerwebi.dominio.servicio.ServicioRespuestaJugador;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class PartidaControllerTest {

  private ServicioPartida servicioPartida;
  private ServicioUsuario servicioUsuario;
  private NotificadorSala notificadorSala;
  private ServicioPartidaPregunta servicioPartidaPregunta;
  private ServicioRespuestaJugador servicioRespuestaJugador;
  private PartidaController controlador;

  @BeforeEach
  void inicializar() {
    servicioPartida = mock(ServicioPartida.class);
    servicioUsuario = mock(ServicioUsuario.class);
    notificadorSala = mock(NotificadorSala.class);
    servicioPartidaPregunta = mock(ServicioPartidaPregunta.class);
    servicioRespuestaJugador = mock(ServicioRespuestaJugador.class);

    controlador =
      new PartidaController(
        servicioPartida,
        servicioUsuario,
        notificadorSala,
        servicioPartidaPregunta,
        servicioRespuestaJugador
      );
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlIniciarPartida() {
    // given
    String codigoSala = "TRV-1001";
    HttpSession sessionInvalida = new MockHttpSession();
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    // when
    ModelAndView resultado = controlador.iniciarPartida(
      codigoSala,
      sessionInvalida,
      redirectAttributes
    );

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void deberiaIniciarPartidaYRedirigirAVotacionCategoria() {
    // given
    Long usuarioId = 1L;
    String codigoSala = "TRV-1001";
    Usuario usuario = crearUsuario(usuarioId);
    Partida partida = mock(Partida.class);
    HttpSession session = crearSession(usuarioId);
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    when(servicioUsuario.buscarUsuarioPorId(usuarioId)).thenReturn(usuario);
    when(partida.getId()).thenReturn(10L);
    when(servicioPartida.iniciarPartida(codigoSala, usuario)).thenReturn(partida);

    // when
    ModelAndView resultado = controlador.iniciarPartida(codigoSala, session, redirectAttributes);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/votacion-categoria"));
    verify(notificadorSala).partidaIniciada(codigoSala, 10L);
  }

  @Test
  void deberiaRedirigirASalaSiFallaIniciarPartida() {
    // given
    Long usuarioId = 1L;
    String codigoSala = "TRV-1001";
    Usuario usuario = crearUsuario(usuarioId);
    HttpSession session = crearSession(usuarioId);
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    when(servicioUsuario.buscarUsuarioPorId(usuarioId)).thenReturn(usuario);
    when(servicioPartida.iniciarPartida(codigoSala, usuario))
      .thenThrow(new IllegalStateException("Error de prueba"));

    // when
    ModelAndView resultado = controlador.iniciarPartida(codigoSala, session, redirectAttributes);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/salas/TRV-1001"));
    verify(redirectAttributes).addFlashAttribute("error", "Error de prueba");
  }

  @Test
  void deberiaFinalizarPartidaYMostrarResultado() {
    // given
    Long usuarioId = 1L;
    String codigoSala = "TRV-1001";
    Usuario usuario = crearUsuario(usuarioId);
    Partida partida = mock(Partida.class);
    HttpSession session = crearSession(usuarioId);

    when(servicioUsuario.buscarUsuarioPorId(usuarioId)).thenReturn(usuario);
    when(servicioPartida.finalizarPartida(codigoSala, usuario)).thenReturn(partida);

    // when
    ModelAndView resultado = controlador.finalizarPartida(codigoSala, session);

    // then
    assertThat(resultado.getViewName(), equalTo("resultado"));
    assertThat(resultado.getModel().get("partida"), equalTo(partida));
  }

  @Test
  void deberiaMostrarVotacionCategoria() {
    // given
    Long usuarioId = 1L;
    Long partidaId = 10L;
    Partida partida = mock(Partida.class);
    HttpSession session = crearSession(usuarioId);

    when(servicioPartida.buscarPartidaPorId(partidaId)).thenReturn(partida);
    when(servicioPartidaPregunta.obtenerCategorias()).thenReturn(Collections.emptyMap());

    // when
    ModelAndView resultado = controlador.mostrarVotacionCategoria(partidaId, session);

    // then
    assertThat(resultado.getViewName(), equalTo("partida-votacion"));
    assertThat(resultado.getModel().get("partida"), equalTo(partida));
  }

  @Test
  void deberiaVotarCategoriaYRedirigirAVotacionSiFaltanVotos() {
    // given
    Long usuarioId = 1L;
    Long partidaId = 10L;
    Long categoriaId = 5L;
    HttpSession session = crearSession(usuarioId);

    when(servicioPartida.registrarVotoCategoria(partidaId, usuarioId, categoriaId))
      .thenReturn(false);

    // when
    ModelAndView resultado = controlador.votarCategoria(partidaId, categoriaId, session);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/votacion-categoria"));
  }

  @Test
  void deberiaVotarCategoriaYRedirigirAPreguntaSiTodosVotaron() {
    // given
    Long usuarioId = 1L;
    Long partidaId = 10L;
    Long categoriaId = 5L;
    HttpSession session = crearSession(usuarioId);

    when(servicioPartida.registrarVotoCategoria(partidaId, usuarioId, categoriaId))
      .thenReturn(true);

    // when
    ModelAndView resultado = controlador.votarCategoria(partidaId, categoriaId, session);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/pregunta"));
  }

  @Test
  void deberiaObtenerResultadoVotacionYRedirigirAPregunta() {
    // given
    Long usuarioId = 1L;
    Long partidaId = 10L;
    HttpSession session = crearSession(usuarioId);

    // when
    ModelAndView resultado = controlador.obtenerResultadoVotacion(partidaId, session);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/pregunta"));
    verify(servicioPartida).finalizarVotacionCategoria(partidaId, usuarioId);
  }

  @Test
  void deberiaMarcarListoYRedirigirARonda() {
    // given
    Long usuarioId = 1L;
    Long partidaId = 10L;
    Usuario usuario = crearUsuario(usuarioId);
    HttpSession session = crearSession(usuarioId);

    when(servicioUsuario.buscarUsuarioPorId(usuarioId)).thenReturn(usuario);

    // when
    ModelAndView resultado = controlador.marcarListo(partidaId, session);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/ronda"));
    verify(servicioPartida).marcarJugadorListo(partidaId, usuario);
  }

  @Test
  void deberiaMostrarPregunta() {
    // given
    Long usuarioId = 1L;
    Long partidaId = 10L;
    Partida partida = mock(Partida.class);
    PartidaRonda ronda = mock(PartidaRonda.class);
    Pregunta pregunta = mock(Pregunta.class);
    HttpSession session = crearSession(usuarioId);

    when(servicioPartida.buscarPartidaPorId(partidaId)).thenReturn(partida);
    when(partida.obtenerRondaActual()).thenReturn(ronda);
    when(ronda.getPregunta()).thenReturn(pregunta);

    // when
    ModelAndView resultado = controlador.mostrarPregunta(partidaId, session);

    // then
    assertThat(resultado.getViewName(), equalTo("partida-pregunta"));
    assertThat(resultado.getModel().get("partida"), equalTo(partida));
    assertThat(resultado.getModel().get("ronda"), equalTo(ronda));
    assertThat(resultado.getModel().get("pregunta"), equalTo(pregunta));
  }

  @Test
  void deberiaResponderCorrectamente() {
    // given
    Long usuarioId = 1L;
    Long partidaId = 10L;
    Long opcionId = 3L;
    HttpSession session = crearSession(usuarioId);
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    Partida partida = mock(Partida.class);
    PartidaRonda ronda = mock(PartidaRonda.class);
    Pregunta pregunta = mock(Pregunta.class);
    Opcion opcion = mock(Opcion.class);

    when(servicioPartida.buscarPartidaPorId(partidaId)).thenReturn(partida);
    when(partida.obtenerRondaActual()).thenReturn(ronda);
    when(ronda.getPregunta()).thenReturn(pregunta);
    when(pregunta.getOpciones()).thenReturn(List.of(opcion));
    when(opcion.getEsCorrecta()).thenReturn(true);
    when(opcion.getTexto()).thenReturn("Texto de prueba");
    when(partida.getEstado()).thenReturn(EstadoPartida.EN_CURSO);
    when(servicioRespuestaJugador.registrarRespuesta(partidaId, usuarioId, opcionId))
      .thenReturn(true);

    // when
    String resultado = controlador.responder(partidaId, opcionId, session, redirectAttributes);

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/pregunta"));
    verify(redirectAttributes).addFlashAttribute("esCorrecta", true);
    verify(redirectAttributes).addFlashAttribute("textoCorrecto", "Texto de prueba");
  }

  @Test
  void deberiaManejarRespuestaDuplicada() {
    // given
    Long usuarioId = 1L;
    Long partidaId = 10L;
    Long opcionId = 3L;
    HttpSession session = crearSession(usuarioId);
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    Partida partida = mock(Partida.class);
    PartidaRonda ronda = mock(PartidaRonda.class);
    Pregunta pregunta = mock(Pregunta.class);
    Opcion opcion = mock(Opcion.class);

    when(servicioPartida.buscarPartidaPorId(partidaId)).thenReturn(partida);
    when(partida.obtenerRondaActual()).thenReturn(ronda);
    when(ronda.getPregunta()).thenReturn(pregunta);
    when(pregunta.getOpciones()).thenReturn(List.of(opcion));
    when(partida.getEstado()).thenReturn(EstadoPartida.EN_CURSO);
    when(servicioRespuestaJugador.registrarRespuesta(partidaId, usuarioId, opcionId))
      .thenThrow(new IllegalStateException("Ya respondiste esta pregunta."));

    // when
    String resultado = controlador.responder(partidaId, opcionId, session, redirectAttributes);

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/pregunta"));
    verify(redirectAttributes).addFlashAttribute("respuestaYaRegistrada", true);
  }

  private Usuario crearUsuario(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    return usuario;
  }

  private HttpSession crearSession(Long usuarioId) {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("ID_USUARIO", usuarioId);
    return session;
  }
}
