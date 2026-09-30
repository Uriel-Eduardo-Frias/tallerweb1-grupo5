package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

public class ServicioPartidaPreguntaTest {

  private ServicioPartidaPregunta servicioPartidaPregunta;
  private Long idCategoriaSeleccionada;
  private Long idOpcionSeleccionada;

  private Map<Long, Categoria> categoriasObtenidas;
  private Pregunta preguntaObtenida;
  private Boolean resultadoVerificacion;
  private String textoCorrectoObtenido;

  @Test
  public void deberiaObtenerTodasLasCategorias() {
    givenExisteUnServicioPartidaPregunta();

    whenObtengoLasCategorias();

    thenLasCategoriasNoSonNulasYContienenElementos();
  }

  private void givenExisteUnServicioPartidaPregunta() {
    this.servicioPartidaPregunta = new ServicioPartidaPreguntaImpl();
  }

  private void whenObtengoLasCategorias() {
    this.categoriasObtenidas = this.servicioPartidaPregunta.obtenerCategorias();
  }

  private void thenLasCategoriasNoSonNulasYContienenElementos() {
    assertThat(this.categoriasObtenidas, is(notNullValue()));
    assertThat(this.categoriasObtenidas.size(), is(greaterThan(0)));
  }

  @Test
  public void deberiaObtenerPreguntaPorCategoriaExistente() {
    givenExisteUnServicioPartidaPregunta();
    givenTengoElIdentificadorDeCategoria(1L);

    whenObtengoLaPreguntaPorCategoria();

    thenLaPreguntaPerteneceALaCategoria(1L);
  }

  private void givenTengoElIdentificadorDeCategoria(Long idCategoria) {
    this.idCategoriaSeleccionada = idCategoria;
  }

  private void whenObtengoLaPreguntaPorCategoria() {
    this.preguntaObtenida =
      this.servicioPartidaPregunta.obtenerPreguntaPorCategoria(this.idCategoriaSeleccionada);
  }

  private void thenLaPreguntaPerteneceALaCategoria(Long idEsperado) {
    assertThat(this.preguntaObtenida, is(notNullValue()));
    assertThat(this.preguntaObtenida.getCategoria(), is(notNullValue()));
    assertThat(this.preguntaObtenida.getCategoria().getId(), equalTo(idEsperado));
  }

  @Test
  public void deberiaVerificarQueLaRespuestaSeleccionadaEsCorrecta() {
    givenExisteUnServicioPartidaPregunta();
    givenTengoElIdentificadorDeOpcion(1L); // 1L es la opción correcta '1492'

    whenVerificoLaRespuesta();

    thenElResultadoDeLaRespuestaEs(true);
  }

  @Test
  public void deberiaVerificarQueLaRespuestaSeleccionadaEsIncorrecta() {
    givenExisteUnServicioPartidaPregunta();
    givenTengoElIdentificadorDeOpcion(2L); // 2L es la opción incorrecta '1810'

    whenVerificoLaRespuesta();

    thenElResultadoDeLaRespuestaEs(false);
  }

  private void givenTengoElIdentificadorDeOpcion(Long idOpcion) {
    this.idOpcionSeleccionada = idOpcion;
  }

  private void whenVerificoLaRespuesta() {
    this.resultadoVerificacion =
      this.servicioPartidaPregunta.verificarRespuesta(this.idOpcionSeleccionada);
  }

  private void thenElResultadoDeLaRespuestaEs(Boolean esperado) {
    assertThat(this.resultadoVerificacion, equalTo(esperado));
  }

  @Test
  public void deberiaDevolverElTextoDeLaRespuestaCorrectaDadaCualquierOpcion() {
    givenExisteUnServicioPartidaPregunta();
    givenTengoElIdentificadorDeOpcion(2L); // 2L es opción de la pregunta de Colón

    whenObtengoElTextoDeLaRespuestaCorrecta();

    thenElTextoCorrectoEs("1492");
  }

  private void whenObtengoElTextoDeLaRespuestaCorrecta() {
    this.textoCorrectoObtenido =
      this.servicioPartidaPregunta.obtenerTextoRespuestaCorrecta(this.idOpcionSeleccionada);
  }

  private void thenElTextoCorrectoEs(String textoEsperado) {
    assertThat(this.textoCorrectoObtenido, equalTo(textoEsperado));
  }
}
