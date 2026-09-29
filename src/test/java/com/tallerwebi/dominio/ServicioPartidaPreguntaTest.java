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

  // Comprueba que el servicio sabe recuperar el listado completo de categorías disponibles para el juego
  @Test
  public void deberiaObtenerTodasLasCategorias() {
    // preparacion
    givenExisteUnServicioPartidaPregunta();

    // ejecucion
    whenObtengoLasCategorias();

    // validacion
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

  // Comprueba que el servicio sabe buscar y devolver la pregunta asociada al identificador de una categoría
  @Test
  public void deberiaObtenerPreguntaPorCategoriaExistente() {
    // preparacion
    givenExisteUnServicioPartidaPregunta();
    givenTengoElIdentificadorDeCategoria(1L);

    // ejecucion
    whenObtengoLaPreguntaPorCategoria();

    // validacion
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

  // Comprueba que el servicio sabe validar como correcta una opción elegida que tiene la respuesta acertada
  @Test
  public void deberiaVerificarQueLaRespuestaSeleccionadaEsCorrecta() {
    // preparacion
    givenExisteUnServicioPartidaPregunta();
    givenTengoElIdentificadorDeOpcion(1L); // 1L es la opción correcta '1492'

    // ejecucion
    whenVerificoLaRespuesta();

    // validacion
    thenElResultadoDeLaRespuestaEs(true);
  }

  // Comprueba que el servicio sabe identificar y rechazar una opción elegida cuando esta es errónea
  @Test
  public void deberiaVerificarQueLaRespuestaSeleccionadaEsIncorrecta() {
    // preparacion
    givenExisteUnServicioPartidaPregunta();
    givenTengoElIdentificadorDeOpcion(2L); // 2L es la opción incorrecta '1810'

    // ejecucion
    whenVerificoLaRespuesta();

    // validacion
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

  // Comprueba que el servicio sabe encontrar la pregunta de origen y extraer el texto de la respuesta verdadera para dar feedback
  @Test
  public void deberiaDevolverElTextoDeLaRespuestaCorrectaDadaCualquierOpcion() {
    // preparacion
    givenExisteUnServicioPartidaPregunta();
    givenTengoElIdentificadorDeOpcion(2L); // 2L es opción de la pregunta de Colón

    // ejecucion
    whenObtengoElTextoDeLaRespuestaCorrecta();

    // validacion
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
//  @Test
//  public void deberiaLanzarExcepcionSiLaCategoriaNoExiste() {
//    // given
//    givenExisteUnServicioPartidaPregunta();
//    Long idInexistente = 999L;
//
//    // when - then
//    assertThrows(CategoriaNoEncontradaException.class, () -> {
//      this.servicioPartidaPregunta.obtenerPreguntaPorCategoria(idInexistente);
//    });
//  }
