package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.enumeration.EstadoRonda;
import com.tallerwebi.dominio.enumeration.TipoComodin;
import com.tallerwebi.dominio.excepcion.PrimerIntentoFallidoDobleChanceException;
import com.tallerwebi.dominio.excepcion.RespuestaDuplicadaException;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.servicio.ServicioComodin;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.ServicioPartidaPregunta;
import com.tallerwebi.dominio.servicio.ServicioRespuestaJugador;
import com.tallerwebi.dominio.servicio.ServicioResultadoPartida;
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

  private static final Long USUARIO_ID = 1L;
  private static final Long PARTIDA_ID = 10L;
  private static final String CODIGO_SALA = "TRV-1001";

  private ServicioPartida servicioPartida;
  private ServicioUsuario servicioUsuario;
  private NotificadorSala notificadorSala;
  private ServicioPartidaPregunta servicioPartidaPregunta;
  private ServicioRespuestaJugador servicioRespuestaJugador;
  private ServicioResultadoPartida servicioResultadoPartida;
  private ServicioComodin servicioComodin;
  private PartidaController controlador;

  @BeforeEach
  void inicializar() {
    servicioPartida = mock(ServicioPartida.class);
    servicioUsuario = mock(ServicioUsuario.class);
    notificadorSala = mock(NotificadorSala.class);
    servicioPartidaPregunta = mock(ServicioPartidaPregunta.class);
    servicioRespuestaJugador = mock(ServicioRespuestaJugador.class);
    servicioResultadoPartida = mock(ServicioResultadoPartida.class);
    servicioComodin = mock(ServicioComodin.class);

    controlador =
      new PartidaController(
        servicioPartida,
        servicioUsuario,
        notificadorSala,
        servicioPartidaPregunta,
        servicioRespuestaJugador,
        servicioResultadoPartida,
        servicioComodin
      );
  }

  // ---------------------------------------------------------------------
  // INICIO / FIN DE PARTIDA
  // ---------------------------------------------------------------------

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlIniciarPartida() {
    // given
    HttpSession sessionInvalida = new MockHttpSession();
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    // when
    ModelAndView resultado = controlador.iniciarPartida(
      CODIGO_SALA,
      sessionInvalida,
      redirectAttributes
    );

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void deberiaIniciarPartidaNotificarYRedirigirAlRouter() {
    // given
    Usuario usuario = crearUsuario(USUARIO_ID);
    Partida partida = mock(Partida.class);
    HttpSession session = crearSession(USUARIO_ID);
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    when(servicioUsuario.buscarUsuarioPorId(USUARIO_ID)).thenReturn(usuario);
    when(partida.getId()).thenReturn(PARTIDA_ID);
    when(servicioPartida.iniciarPartida(CODIGO_SALA, usuario)).thenReturn(partida);

    // when
    ModelAndView resultado = controlador.iniciarPartida(CODIGO_SALA, session, redirectAttributes);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/ir"));
    verify(notificadorSala).partidaIniciada(CODIGO_SALA, PARTIDA_ID);
  }

  @Test
  void deberiaRedirigirASalaSiFallaIniciarPartida() {
    // given
    Usuario usuario = crearUsuario(USUARIO_ID);
    HttpSession session = crearSession(USUARIO_ID);
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    when(servicioUsuario.buscarUsuarioPorId(USUARIO_ID)).thenReturn(usuario);
    when(servicioPartida.iniciarPartida(CODIGO_SALA, usuario))
      .thenThrow(new IllegalStateException("Error de prueba"));

    // when
    ModelAndView resultado = controlador.iniciarPartida(CODIGO_SALA, session, redirectAttributes);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/salas/TRV-1001"));
    verify(redirectAttributes).addFlashAttribute("error", "Error de prueba");
    verify(notificadorSala, never()).partidaIniciada(anyString(), anyLong());
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlFinalizarPartida() {
    // when
    ModelAndView resultado = controlador.finalizarPartida(CODIGO_SALA, new MockHttpSession());

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void deberiaFinalizarPartidaYRedirigirAFin() {
    // given
    Usuario usuario = crearUsuario(USUARIO_ID);
    Partida partida = mock(Partida.class);
    HttpSession session = crearSession(USUARIO_ID);

    when(servicioUsuario.buscarUsuarioPorId(USUARIO_ID)).thenReturn(usuario);
    when(partida.getId()).thenReturn(PARTIDA_ID);
    when(servicioPartida.finalizarPartida(CODIGO_SALA, usuario)).thenReturn(partida);

    // when
    ModelAndView resultado = controlador.finalizarPartida(CODIGO_SALA, session);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/fin"));
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlMostrarFin() {
    // when
    ModelAndView resultado = controlador.mostrarFin(PARTIDA_ID, new MockHttpSession());

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void deberiaMostrarFinConElResultadoDeLaPartida() {
    // given
    HttpSession session = crearSession(USUARIO_ID);
    ResultadoPartida resultadoPartida = mock(ResultadoPartida.class);

    when(servicioResultadoPartida.obtenerResultado(PARTIDA_ID, USUARIO_ID))
      .thenReturn(resultadoPartida);

    // when
    ModelAndView resultado = controlador.mostrarFin(PARTIDA_ID, session);

    // then
    assertThat(resultado.getViewName(), equalTo("partida-fin"));
    assertThat(resultado.getModel().get("resultado"), equalTo(resultadoPartida));
  }

  @Test
  void deberiaRedirigirAlRouterSiLaPartidaTodaviaNoFinalizoAlMostrarFin() {
    // given
    HttpSession session = crearSession(USUARIO_ID);

    when(servicioResultadoPartida.obtenerResultado(PARTIDA_ID, USUARIO_ID))
      .thenThrow(new IllegalStateException("La partida no finalizó"));

    // when
    ModelAndView resultado = controlador.mostrarFin(PARTIDA_ID, session);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/ir"));
  }

  @Test
  void deberiaRedirigirASalasSiElUsuarioNoParticipoAlMostrarFin() {
    // given
    HttpSession session = crearSession(USUARIO_ID);

    when(servicioResultadoPartida.obtenerResultado(PARTIDA_ID, USUARIO_ID))
      .thenThrow(new IllegalArgumentException("No participó"));

    // when
    ModelAndView resultado = controlador.mostrarFin(PARTIDA_ID, session);

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/salas"));
  }

  // ---------------------------------------------------------------------
  // ROUTER
  // ---------------------------------------------------------------------

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionEnElRouter() {
    // when
    String resultado = controlador.irSegunEstado(PARTIDA_ID, new MockHttpSession());

    // then
    assertThat(resultado, equalTo("redirect:/login"));
  }

  @Test
  void deberiaRedirigirAFinSiLaPartidaEstaFinalizada() {
    // given
    Partida partida = mock(Partida.class);

    when(partida.getEstado()).thenReturn(EstadoPartida.FINALIZADA);
    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);

    // when
    String resultado = controlador.irSegunEstado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/fin"));
  }

  @Test
  void deberiaRedirigirASalasSiLaPartidaNoTieneRondaActual() {
    // given
    Partida partida = mock(Partida.class);

    when(partida.getEstado()).thenReturn(EstadoPartida.EN_CURSO);
    when(partida.obtenerRondaActual()).thenReturn(null);
    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);

    // when
    String resultado = controlador.irSegunEstado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado, equalTo("redirect:/salas"));
  }

  @Test
  void deberiaRedirigirAVotacionCategoriaSiLaRondaEstaEnVotacion() {
    // given
    Partida partida = crearPartidaConRondaEnEstado(EstadoRonda.VOTACION_CATEGORIA);

    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);

    // when
    String resultado = controlador.irSegunEstado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/votacion-categoria"));
  }

  @Test
  void deberiaRedirigirAResultadoSiLaRondaEstaEnResultadoPregunta() {
    // given
    Partida partida = crearPartidaConRondaEnEstado(EstadoRonda.RESULTADO_PREGUNTA);

    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);

    // when
    String resultado = controlador.irSegunEstado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/resultado"));
  }

  @Test
  void deberiaIntentarCerrarLaRondaYRedirigirAPreguntaSiSigueEnCurso() {
    // given
    PartidaRonda ronda = crearRonda(EstadoRonda.PREGUNTA_EN_CURSO);
    Partida partida = crearPartidaConRonda(ronda);

    when(ronda.getPregunta()).thenReturn(mock(Pregunta.class));
    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);

    // when
    String resultado = controlador.irSegunEstado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/pregunta"));
    verify(servicioPartida).finalizarRondaSiTodosRespondieron(PARTIDA_ID);
  }

  @Test
  void deberiaRedirigirAResultadoSiAlCerrarLaRondaTodosYaHabianRespondido() {
    // given
    PartidaRonda rondaEnCurso = crearRonda(EstadoRonda.PREGUNTA_EN_CURSO);
    Partida partidaAntes = crearPartidaConRonda(rondaEnCurso);
    Partida partidaDespues = crearPartidaConRondaEnEstado(EstadoRonda.RESULTADO_PREGUNTA);

    when(rondaEnCurso.getPregunta()).thenReturn(mock(Pregunta.class));
    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partidaAntes, partidaDespues);

    // when
    String resultado = controlador.irSegunEstado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/resultado"));
    verify(servicioPartida).finalizarRondaSiTodosRespondieron(PARTIDA_ID);
  }

  @Test
  void deberiaRedirigirAPreguntaSinCerrarLaRondaSiTodaviaNoTienePregunta() {
    // given
    PartidaRonda ronda = crearRonda(EstadoRonda.PREGUNTA_EN_CURSO);
    Partida partida = crearPartidaConRonda(ronda);

    when(ronda.getPregunta()).thenReturn(null);
    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);

    // when
    String resultado = controlador.irSegunEstado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/pregunta"));
    verify(servicioPartida, never()).finalizarRondaSiTodosRespondieron(anyLong());
  }

  // ---------------------------------------------------------------------
  // VOTACIÓN DE CATEGORÍA
  // ---------------------------------------------------------------------

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlMostrarVotacionCategoria() {
    // when
    ModelAndView resultado = controlador.mostrarVotacionCategoria(
      PARTIDA_ID,
      new MockHttpSession()
    );

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void deberiaRedirigirAlRouterSiLaRondaNoEstaEnVotacion() {
    // given
    Partida partida = crearPartidaConRondaEnEstado(EstadoRonda.PREGUNTA_EN_CURSO);

    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);

    // when
    ModelAndView resultado = controlador.mostrarVotacionCategoria(
      PARTIDA_ID,
      crearSession(USUARIO_ID)
    );

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/ir"));
  }

  @Test
  void deberiaMostrarVotacionCategoria() {
    // given
    Partida partida = crearPartidaConRondaEnEstado(EstadoRonda.VOTACION_CATEGORIA);

    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);
    when(servicioPartidaPregunta.obtenerCategorias()).thenReturn(Collections.emptyMap());
    when(servicioPartida.usuarioYaVoto(PARTIDA_ID, USUARIO_ID)).thenReturn(true);

    // when
    ModelAndView resultado = controlador.mostrarVotacionCategoria(
      PARTIDA_ID,
      crearSession(USUARIO_ID)
    );

    // then
    assertThat(resultado.getViewName(), equalTo("partida-votacion"));
    assertThat(resultado.getModel().get("partida"), equalTo(partida));
    assertThat(resultado.getModel().get("mapaDeCategorias"), equalTo(Collections.emptyMap()));
    assertThat(resultado.getModel().get("yaVoto"), equalTo(true));
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlVotarCategoria() {
    // when
    ModelAndView resultado = controlador.votarCategoria(PARTIDA_ID, 5L, new MockHttpSession());

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
    verify(servicioPartida, never()).registrarVotoCategoria(anyLong(), anyLong(), anyLong());
  }

  @Test
  void deberiaRegistrarElVotoYRedirigirAlRouter() {
    // given
    Long categoriaId = 5L;

    // when
    ModelAndView resultado = controlador.votarCategoria(
      PARTIDA_ID,
      categoriaId,
      crearSession(USUARIO_ID)
    );

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/ir"));
    verify(servicioPartida).registrarVotoCategoria(PARTIDA_ID, USUARIO_ID, categoriaId);
  }

  @Test
  void deberiaRedirigirAlRouterSiElVotoEsDuplicadoOLaVotacionYaCerro() {
    // given
    Long categoriaId = 5L;

    when(servicioPartida.registrarVotoCategoria(PARTIDA_ID, USUARIO_ID, categoriaId))
      .thenThrow(new IllegalStateException("El usuario ya votó una categoría"));

    // when
    ModelAndView resultado = controlador.votarCategoria(
      PARTIDA_ID,
      categoriaId,
      crearSession(USUARIO_ID)
    );

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/ir"));
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlFinalizarVotacion() {
    // when
    ModelAndView resultado = controlador.finalizarVotacion(PARTIDA_ID, new MockHttpSession());

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
    verify(servicioPartida, never()).finalizarVotacionCategoria(anyLong(), anyLong());
  }

  @Test
  void deberiaFinalizarVotacionYRedirigirAlRouter() {
    // when
    ModelAndView resultado = controlador.finalizarVotacion(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/ir"));
    verify(servicioPartida).finalizarVotacionCategoria(PARTIDA_ID, USUARIO_ID);
  }

  // ---------------------------------------------------------------------
  // PREGUNTA Y RESPUESTA
  // ---------------------------------------------------------------------

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlMostrarPregunta() {
    // when
    ModelAndView resultado = controlador.mostrarPregunta(PARTIDA_ID, new MockHttpSession());

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void deberiaRedirigirAlRouterSiLaRondaNoEstaEnPregunta() {
    // given
    Partida partida = crearPartidaConRondaEnEstado(EstadoRonda.VOTACION_CATEGORIA);

    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);

    // when
    ModelAndView resultado = controlador.mostrarPregunta(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/ir"));
    verify(servicioPartida, never()).finalizarRondaSiTodosRespondieron(anyLong());
  }

  @Test
  void deberiaMostrarPreguntaConComodinesYEstadoDeRespuesta() {
    // given
    PartidaRonda ronda = crearRonda(EstadoRonda.PREGUNTA_EN_CURSO);
    Partida partida = crearPartidaConRonda(ronda);
    Pregunta pregunta = mock(Pregunta.class);

    when(ronda.getPregunta()).thenReturn(pregunta);
    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);
    when(servicioRespuestaJugador.yaRespondio(PARTIDA_ID, USUARIO_ID)).thenReturn(true);
    when(
      servicioComodin.tieneComodinDisponible(
        PARTIDA_ID,
        USUARIO_ID,
        TipoComodin.CINCUENTA_CINCUENTA
      )
    )
      .thenReturn(true);
    when(servicioComodin.tieneComodinDisponible(PARTIDA_ID, USUARIO_ID, TipoComodin.TIEMPO_EXTRA))
      .thenReturn(false);
    when(servicioComodin.tieneComodinDisponible(PARTIDA_ID, USUARIO_ID, TipoComodin.DOBLE_CHANCE))
      .thenReturn(true);

    // when
    ModelAndView resultado = controlador.mostrarPregunta(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado.getViewName(), equalTo("partida-pregunta"));
    assertThat(resultado.getModel().get("partida"), equalTo(partida));
    assertThat(resultado.getModel().get("ronda"), equalTo(ronda));
    assertThat(resultado.getModel().get("pregunta"), equalTo(pregunta));
    assertThat(resultado.getModel().get("yaRespondio"), equalTo(true));
    assertThat(resultado.getModel().get("puede5050"), equalTo(true));
    assertThat(resultado.getModel().get("puedeTiempo"), equalTo(false));
    assertThat(resultado.getModel().get("puedeDobleChance"), equalTo(true));
    verify(servicioPartida).finalizarRondaSiTodosRespondieron(PARTIDA_ID);
  }

  @Test
  void deberiaRedirigirAlRouterSiAlCerrarLaRondaYaNoEstaEnPregunta() {
    // given
    Partida partidaAntes = crearPartidaConRondaEnEstado(EstadoRonda.PREGUNTA_EN_CURSO);
    Partida partidaDespues = crearPartidaConRondaEnEstado(EstadoRonda.RESULTADO_PREGUNTA);

    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partidaAntes, partidaDespues);

    // when
    ModelAndView resultado = controlador.mostrarPregunta(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/ir"));
    verify(servicioPartida).finalizarRondaSiTodosRespondieron(PARTIDA_ID);
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlResponder() {
    // given
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    // when
    String resultado = controlador.responder(
      PARTIDA_ID,
      3L,
      new MockHttpSession(),
      redirectAttributes
    );

    // then
    assertThat(resultado, equalTo("redirect:/login"));
  }

  @Test
  void deberiaRegistrarLaRespuestaYRedirigirAlRouter() {
    // given
    Long opcionId = 3L;
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    // when
    String resultado = controlador.responder(
      PARTIDA_ID,
      opcionId,
      crearSession(USUARIO_ID),
      redirectAttributes
    );

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/ir"));
    verify(servicioRespuestaJugador).registrarRespuesta(PARTIDA_ID, USUARIO_ID, opcionId);
  }

  @Test
  void deberiaManejarRespuestaDuplicadaRedirigiendoAlRouter() {
    // given
    Long opcionId = 3L;
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    doThrow(mock(RespuestaDuplicadaException.class))
      .when(servicioRespuestaJugador)
      .registrarRespuesta(PARTIDA_ID, USUARIO_ID, opcionId);

    // when
    String resultado = controlador.responder(
      PARTIDA_ID,
      opcionId,
      crearSession(USUARIO_ID),
      redirectAttributes
    );

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/ir"));
  }

  @Test
  void deberiaVolverALaPreguntaSiFallaElPrimerIntentoDeDobleChance() {
    // given
    Long opcionId = 3L;
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
    PrimerIntentoFallidoDobleChanceException excepcion = mock(
      PrimerIntentoFallidoDobleChanceException.class
    );

    when(excepcion.getOpcionIncorrectaId()).thenReturn(opcionId);
    doThrow(excepcion)
      .when(servicioRespuestaJugador)
      .registrarRespuesta(PARTIDA_ID, USUARIO_ID, opcionId);

    // when
    String resultado = controlador.responder(
      PARTIDA_ID,
      opcionId,
      crearSession(USUARIO_ID),
      redirectAttributes
    );

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/pregunta"));
    verify(redirectAttributes).addFlashAttribute("falloPrimerIntentoDobleChance", Boolean.TRUE);
    verify(redirectAttributes).addFlashAttribute("opcionFalladaId", opcionId);
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlAgotarseElTiempo() {
    // when
    String resultado = controlador.tiempoAgotado(PARTIDA_ID, new MockHttpSession());

    // then
    assertThat(resultado, equalTo("redirect:/login"));
    verify(servicioRespuestaJugador, never()).registrarTiempoAgotado(anyLong(), anyLong());
  }

  @Test
  void deberiaRegistrarTiempoAgotadoYRedirigirAlRouter() {
    // when
    String resultado = controlador.tiempoAgotado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/ir"));
    verify(servicioRespuestaJugador).registrarTiempoAgotado(PARTIDA_ID, USUARIO_ID);
  }

  @Test
  void deberiaIgnorarTiempoAgotadoDuplicadoYRedirigirAlRouter() {
    // given
    doThrow(mock(RespuestaDuplicadaException.class))
      .when(servicioRespuestaJugador)
      .registrarTiempoAgotado(PARTIDA_ID, USUARIO_ID);

    // when
    String resultado = controlador.tiempoAgotado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/ir"));
  }

  // ---------------------------------------------------------------------
  // RESULTADO DE LA PREGUNTA Y AVANCE DE RONDA
  // ---------------------------------------------------------------------

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlMostrarResultado() {
    // when
    ModelAndView resultado = controlador.mostrarResultado(PARTIDA_ID, new MockHttpSession());

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void deberiaRedirigirAlRouterSiLaRondaNoEstaEnResultado() {
    // given
    Partida partida = crearPartidaConRondaEnEstado(EstadoRonda.PREGUNTA_EN_CURSO);

    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);

    // when
    ModelAndView resultado = controlador.mostrarResultado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado.getViewName(), equalTo("redirect:/partida/10/ir"));
  }

  @Test
  void deberiaMostrarResultadoConTextoCorrectoYDatosDelJugador() {
    // given
    PartidaRonda ronda = crearRonda(EstadoRonda.RESULTADO_PREGUNTA);
    Partida partida = crearPartidaConRonda(ronda);
    Pregunta pregunta = crearPreguntaConOpcionCorrecta("Texto de prueba");

    when(ronda.getPregunta()).thenReturn(pregunta);
    when(partida.getRondaActual()).thenReturn(2);
    when(partida.getTotalRondas()).thenReturn(5);
    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);
    when(servicioPartida.esHost(PARTIDA_ID, USUARIO_ID)).thenReturn(true);
    when(servicioRespuestaJugador.obtenerResultado(PARTIDA_ID, USUARIO_ID)).thenReturn(true);
    when(servicioRespuestaJugador.fuePorTiempo(PARTIDA_ID, USUARIO_ID)).thenReturn(false);

    // when
    ModelAndView resultado = controlador.mostrarResultado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado.getViewName(), equalTo("partida-resultado"));
    assertThat(resultado.getModel().get("partida"), equalTo(partida));
    assertThat(resultado.getModel().get("esCorrecta"), equalTo(true));
    assertThat(resultado.getModel().get("fuePorTiempo"), equalTo(false));
    assertThat(resultado.getModel().get("textoCorrecto"), equalTo("Texto de prueba"));
    assertThat(resultado.getModel().get("esHost"), equalTo(true));
    assertThat(resultado.getModel().get("esUltimaRonda"), equalTo(false));
  }

  @Test
  void deberiaIndicarQueEsLaUltimaRondaCuandoSeLlegoAlTotal() {
    // given
    PartidaRonda ronda = crearRonda(EstadoRonda.RESULTADO_PREGUNTA);
    Partida partida = crearPartidaConRonda(ronda);
    Pregunta pregunta = crearPreguntaConOpcionCorrecta("Texto");

    when(ronda.getPregunta()).thenReturn(pregunta);
    when(partida.getRondaActual()).thenReturn(5);
    when(partida.getTotalRondas()).thenReturn(5);
    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);

    // when
    ModelAndView resultado = controlador.mostrarResultado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado.getModel().get("esUltimaRonda"), equalTo(true));
  }

  @Test
  void deberiaMostrarResultadoIncorrectoPorTiempoAgotado() {
    // given
    PartidaRonda ronda = crearRonda(EstadoRonda.RESULTADO_PREGUNTA);
    Partida partida = crearPartidaConRonda(ronda);
    Pregunta pregunta = crearPreguntaConOpcionCorrecta("Texto");

    when(ronda.getPregunta()).thenReturn(pregunta);
    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partida);
    when(servicioRespuestaJugador.obtenerResultado(PARTIDA_ID, USUARIO_ID)).thenReturn(false);
    when(servicioRespuestaJugador.fuePorTiempo(PARTIDA_ID, USUARIO_ID)).thenReturn(true);

    // when
    ModelAndView resultado = controlador.mostrarResultado(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado.getModel().get("esCorrecta"), equalTo(false));
    assertThat(resultado.getModel().get("fuePorTiempo"), equalTo(true));
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesionAlAvanzarRonda() {
    // when
    String resultado = controlador.siguienteRonda(PARTIDA_ID, new MockHttpSession());

    // then
    assertThat(resultado, equalTo("redirect:/login"));
    verify(servicioPartida, never()).avanzarRonda(anyLong(), anyLong());
  }

  @Test
  void deberiaAvanzarRondaYRedirigirAlRouter() {
    // when
    String resultado = controlador.siguienteRonda(PARTIDA_ID, crearSession(USUARIO_ID));

    // then
    assertThat(resultado, equalTo("redirect:/partida/10/ir"));
    verify(servicioPartida).avanzarRonda(PARTIDA_ID, USUARIO_ID);
  }

  // ---------------------------------------------------------------------
  // HELPERS
  // ---------------------------------------------------------------------

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

  private PartidaRonda crearRonda(EstadoRonda estado) {
    PartidaRonda ronda = mock(PartidaRonda.class);
    when(ronda.getEstado()).thenReturn(estado);
    return ronda;
  }

  private Partida crearPartidaConRonda(PartidaRonda ronda) {
    Partida partida = mock(Partida.class);
    when(partida.obtenerRondaActual()).thenReturn(ronda);
    return partida;
  }

  private Partida crearPartidaConRondaEnEstado(EstadoRonda estado) {
    return crearPartidaConRonda(crearRonda(estado));
  }

  private Pregunta crearPreguntaConOpcionCorrecta(String texto) {
    Pregunta pregunta = mock(Pregunta.class);
    Opcion opcion = mock(Opcion.class);

    when(opcion.getEsCorrecta()).thenReturn(true);
    when(opcion.getTexto()).thenReturn(texto);
    when(pregunta.getOpciones()).thenReturn(List.of(opcion));

    return pregunta;
  }
}
