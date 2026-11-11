package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tallerwebi.dominio.excepcion.DesafioDiarioException;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.servicio.ServicioDesafioDiario;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.servlet.ModelAndView;

public class ControladorDesafioDiarioTest {

  private ControladorDesafioDiario controladorDesafio;
  private ServicioDesafioDiario servicioDesafioMock;
  private HttpSession sessionMock;

  private ModelAndView mav;
  private Long idUsuarioEnSesion;

  @BeforeEach
  public void init() {
    this.servicioDesafioMock = mock(ServicioDesafioDiario.class);
    this.sessionMock = mock(HttpSession.class);
    this.controladorDesafio = new ControladorDesafioDiario(this.servicioDesafioMock);
  }

  @Test
  public void siUsuarioNoEstaLogueadoAlMostrarDesafioQueLoRedirijaAlLogin() {
    // preparacion
    givenUnUsuarioNoLogueado();

    // ejecucion
    whenElUsuarioIntentaVerElDesafio();

    // validacion
    thenSeMuestraLaVista("redirect:/login");
  }

  @Test
  public void siUnUsuarioEstaLogueadoYElIntentoEstaFinalizadoQueMuestreElResultado() {
    // preparacion
    givenUnUsuarioLogueadoConId(1L);
    givenUnIntentoFinalizado();

    // ejecucion
    whenElUsuarioIntentaVerElDesafio();

    // validacion
    thenSeMuestraLaVista("desafio-diario-resultado");
    thenElControladorIniciaOContinuaElIntento();
  }

  @Test
  public void siUnUsuarioEstaLogueadoYFaltanPreguntasQueMuestreLaPregunta() {
    // preparacion
    givenUnUsuarioLogueadoConId(1L);
    givenUnIntentoConPreguntasPendientes();

    // ejecucion
    whenElUsuarioIntentaVerElDesafio();

    // validacion
    thenSeMuestraLaVista("desafio-diario");
  }

  @Test
  public void siUsuarioNoEstaLogueadoAlResponderQueLoRedirijaAlLogin() {
    // preparacion
    givenUnUsuarioNoLogueado();

    // ejecucion
    whenElUsuarioIntentaResponder(1L, 2L);

    // validacion
    thenSeMuestraLaVista("redirect:/login");
  }

  @Test
  public void siElUsuarioRespondeYElIntentoNoFinalizaQueRedirijaAlDesafio()
    throws DesafioDiarioException {
    // preparacion
    givenUnUsuarioLogueadoConId(1L);
    givenUnaRespuestaQueNoFinalizaElIntento(1L, 2L);

    // ejecucion
    whenElUsuarioIntentaResponder(1L, 2L);

    // validacion
    thenSeMuestraLaVista("redirect:/desafio-diario");
  }

  @Test
  public void siFallaAlResponderPorExcepcionQueMuestreLaPreguntaConError()
    throws DesafioDiarioException {
    // preparacion
    givenUnUsuarioLogueadoConId(1L);
    givenUnIntentoConPreguntasPendientes();
    givenUnaExcepcionAlResponder(1L, 2L, "Error al responder");

    // ejecucion
    whenElUsuarioIntentaResponder(1L, 2L);

    // validacion
    thenSeMuestraLaVista("desafio-diario");
    thenElModeloTieneElAtributo("error", "Error al responder");
  }

  // given
  private void givenUnUsuarioNoLogueado() {
    when(this.sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);
  }

  private void givenUnUsuarioLogueadoConId(Long id) {
    this.idUsuarioEnSesion = id;
    when(this.sessionMock.getAttribute("ID_USUARIO")).thenReturn(this.idUsuarioEnSesion);
  }

  private void givenUnIntentoFinalizado() {
    DesafioDiario desafioMock = mock(DesafioDiario.class);
    IntentoDesafio intentoMock = mock(IntentoDesafio.class);

    when(intentoMock.estaFinalizado()).thenReturn(true);
    when(this.servicioDesafioMock.obtenerDesafioDeHoy()).thenReturn(desafioMock);
    when(this.servicioDesafioMock.iniciarOContinuarIntento(this.idUsuarioEnSesion))
      .thenReturn(intentoMock);
  }

  private void givenUnIntentoConPreguntasPendientes() {
    DesafioDiario desafioMock = mock(DesafioDiario.class);
    IntentoDesafio intentoMock = mock(IntentoDesafio.class);
    PreguntaDesafio preguntaDesafioMock = mock(PreguntaDesafio.class);
    Pregunta preguntaMock = mock(Pregunta.class);

    when(preguntaMock.getIdentificador()).thenReturn(1L);
    when(preguntaDesafioMock.getPregunta()).thenReturn(preguntaMock);
    when(desafioMock.getPreguntas()).thenReturn(Collections.singletonList(preguntaDesafioMock));

    when(intentoMock.estaFinalizado()).thenReturn(false);
    when(intentoMock.getRespuestas()).thenReturn(Collections.emptyList()); // Sin respuestas previas

    when(this.servicioDesafioMock.obtenerDesafioDeHoy()).thenReturn(desafioMock);
    when(this.servicioDesafioMock.iniciarOContinuarIntento(this.idUsuarioEnSesion))
      .thenReturn(intentoMock);
  }

  private void givenUnaRespuestaQueNoFinalizaElIntento(Long preguntaId, Long opcionId)
    throws DesafioDiarioException {
    RespuestaDesafio respuestaMock = mock(RespuestaDesafio.class);
    IntentoDesafio intentoMock = mock(IntentoDesafio.class);

    when(intentoMock.estaFinalizado()).thenReturn(false);
    when(respuestaMock.getIntento()).thenReturn(intentoMock);
    when(this.servicioDesafioMock.responder(this.idUsuarioEnSesion, preguntaId, opcionId))
      .thenReturn(respuestaMock);
  }

  private void givenUnaExcepcionAlResponder(Long preguntaId, Long opcionId, String mensajeError)
    throws DesafioDiarioException {
    when(this.servicioDesafioMock.responder(this.idUsuarioEnSesion, preguntaId, opcionId))
      .thenThrow(new DesafioDiarioException(mensajeError));
  }

  // when
  private void whenElUsuarioIntentaVerElDesafio() {
    this.mav = this.controladorDesafio.mostrarDesafio(this.sessionMock);
  }

  private void whenElUsuarioIntentaResponder(Long preguntaId, Long opcionId) {
    this.mav = this.controladorDesafio.responder(preguntaId, opcionId, this.sessionMock);
  }

  // then
  private void thenSeMuestraLaVista(String vistaEsperada) {
    assertThat(this.mav.getViewName(), equalTo(vistaEsperada));
  }

  private void thenElControladorIniciaOContinuaElIntento() {
    verify(this.servicioDesafioMock, times(1)).iniciarOContinuarIntento(this.idUsuarioEnSesion);
  }

  private void thenElModeloTieneElAtributo(String atributo, String valorEsperado) {
    assertThat(this.mav.getModel().get(atributo), equalTo(valorEsperado));
  }
}
