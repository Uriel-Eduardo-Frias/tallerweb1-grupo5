package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Categoria;
import com.tallerwebi.dominio.Pregunta;
import com.tallerwebi.dominio.ServicioPartidaPregunta;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPartidaPreguntaTest {

  private ServicioPartidaPregunta servicioMock;
  private ControladorPartidaPregunta controlador;

  @BeforeEach
  public void init() {
    this.servicioMock = mock(ServicioPartidaPregunta.class);
    this.controlador = new ControladorPartidaPregunta(this.servicioMock);
  }

  // Comprueba que el controlador sabe preparar la pantalla de inicio cargando el listado de categorías en el modelo
  @Test
  public void deberiaRetornarVistaVotacionConElMapaDeCategorias() {
    // given
    Map<Long, Categoria> categoriasSimuladas = givenExistenCategorias();

    // when
    ModelAndView mav = this.controlador.mostrarPantalla();

    // then
    thenLaVistaEsVotacionConSusCategorias(mav, categoriasSimuladas);
  }

  private Map<Long, Categoria> givenExistenCategorias() {
    Map<Long, Categoria> mapa = new HashMap<>();
    mapa.put(1L, new Categoria());
    when(this.servicioMock.obtenerCategorias()).thenReturn(mapa);
    return mapa;
  }

  private void thenLaVistaEsVotacionConSusCategorias(
    ModelAndView mav,
    Map<Long, Categoria> esperadas
  ) {
    assertThat(mav.getViewName(), equalTo("votacion"));
    assertThat(mav.getModel().get("mapaDeCategorias"), equalTo(esperadas));
    verify(this.servicioMock, times(1)).obtenerCategorias();
  }

  // Comprueba que el controlador sabe iniciar una partida buscando la pregunta de la categoría elegida y configurando el estado inicial
  @Test
  public void deberiaRetornarVistaPreguntaConPreguntaYEstadoInicial() {
    // given
    Long categoriaId = 1L;
    Pregunta preguntaSimulada = givenExistePreguntaParaCategoria(categoriaId);

    // when
    ModelAndView mav = this.controlador.iniciarPregunta(categoriaId);

    // then
    thenLaVistaEsPreguntaSinResponder(mav, preguntaSimulada, categoriaId);
  }

  private Pregunta givenExistePreguntaParaCategoria(Long categoriaId) {
    Pregunta pregunta = new Pregunta();
    when(this.servicioMock.obtenerPreguntaPorCategoria(categoriaId)).thenReturn(pregunta);
    return pregunta;
  }

  private void thenLaVistaEsPreguntaSinResponder(
    ModelAndView mav,
    Pregunta preguntaEsperada,
    Long categoriaId
  ) {
    assertThat(mav.getViewName(), equalTo("pregunta"));
    assertThat(mav.getModel().get("pregunta"), equalTo(preguntaEsperada));
    assertThat(mav.getModel().get("yaRespondio"), equalTo(false));
    verify(this.servicioMock, times(1)).obtenerPreguntaPorCategoria(categoriaId);
  }

  // Comprueba que el controlador sabe procesar un acierto actualizando la pantalla con el veredicto positivo y la solución
  @Test
  public void deberiaMostrarResultadoPositivoCuandoLaOpcionEsCorrecta() {
    // given
    Long opcionId = 10L;
    String textoRespuesta = "1492";
    givenElServicioVerificaRespuesta(opcionId, true, textoRespuesta);

    // when
    ModelAndView mav = this.controlador.procesarRespuesta(opcionId);

    // then
    thenLaVistaMuestraResultadoDeRespuesta(mav, opcionId, true, textoRespuesta);
  }

  // Comprueba que el controlador sabe procesar un fallo mostrando el veredicto negativo junto con la respuesta que era correcta
  @Test
  public void deberiaMostrarResultadoNegativoCuandoLaOpcionEsIncorrecta() {
    // given
    Long opcionId = 11L;
    String textoRespuesta = "1492";
    givenElServicioVerificaRespuesta(opcionId, false, textoRespuesta);

    // when
    ModelAndView mav = this.controlador.procesarRespuesta(opcionId);

    // then
    thenLaVistaMuestraResultadoDeRespuesta(mav, opcionId, false, textoRespuesta);
  }

  private void givenElServicioVerificaRespuesta(
    Long opcionId,
    boolean esCorrecta,
    String textoCorrecto
  ) {
    when(this.servicioMock.verificarRespuesta(opcionId)).thenReturn(esCorrecta);
    when(this.servicioMock.obtenerTextoRespuestaCorrecta(opcionId)).thenReturn(textoCorrecto);
  }

  private void thenLaVistaMuestraResultadoDeRespuesta(
    ModelAndView mav,
    Long opcionId,
    boolean resultadoEsperado,
    String textoEsperado
  ) {
    assertThat(mav.getViewName(), equalTo("pregunta"));
    assertThat(mav.getModel().get("yaRespondio"), equalTo(true));
    assertThat(mav.getModel().get("esCorrecta"), equalTo(resultadoEsperado));
    assertThat(mav.getModel().get("textoCorrecto"), equalTo(textoEsperado));

    verify(this.servicioMock, times(1)).verificarRespuesta(opcionId);
    verify(this.servicioMock, times(1)).obtenerTextoRespuestaCorrecta(opcionId);
  }
}
