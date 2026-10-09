package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPartidaPreguntaTest {

  private RepositorioPartidaPregunta repositorioPartidaPregunta;
  private ServicioPartidaPregunta servicioPartidaPregunta;

  private Long idCategoriaSeleccionada;
  private Long idOpcionSeleccionada;

  private Map<Long, Categoria> categoriasObtenidas;
  private Pregunta preguntaObtenida;
  private Boolean resultadoVerificacion;
  private String textoCorrectoObtenido;

  @BeforeEach
  public void init() {
    this.repositorioPartidaPregunta = mock(RepositorioPartidaPregunta.class);
    this.servicioPartidaPregunta = new ServicioPartidaPreguntaImpl(this.repositorioPartidaPregunta);
  }

  @Test
  public void deberiaObtenerTodasLasCategorias() {
    givenExistenCategoriasEnElRepositorio();

    whenObtengoLasCategorias();

    thenLasCategoriasNoSonNulasYContienenElementos();
  }

  private void givenExistenCategoriasEnElRepositorio() {
    Categoria historia = new Categoria("Historia");
    historia.setId(1L);
    Categoria ciencia = new Categoria("Ciencia");
    ciencia.setId(2L);

    when(this.repositorioPartidaPregunta.obtenerTodasLasCategorias())
      .thenReturn(List.of(historia, ciencia));
  }

  private void whenObtengoLasCategorias() {
    this.categoriasObtenidas = this.servicioPartidaPregunta.obtenerCategorias();
  }

  private void thenLasCategoriasNoSonNulasYContienenElementos() {
    assertThat(this.categoriasObtenidas, is(notNullValue()));
    assertThat(this.categoriasObtenidas.size() > 0, is(true));
  }

  @Test
  public void deberiaObtenerPreguntaPorCategoriaExistente() {
    givenTengoElIdentificadorDeCategoria(1L);
    givenExisteUnaPreguntaParaLaCategoria(1L);

    whenObtengoLaPreguntaPorCategoria();

    thenLaPreguntaPerteneceALaCategoria(1L);
  }

  private void givenTengoElIdentificadorDeCategoria(Long idCategoria) {
    this.idCategoriaSeleccionada = idCategoria;
  }

  private void givenExisteUnaPreguntaParaLaCategoria(Long idCategoria) {
    Categoria categoria = new Categoria("Historia");
    categoria.setId(idCategoria);

    Pregunta pregunta = new Pregunta("¿En qué año se descubrió América?", categoria);
    pregunta.setIdentificador(101L);

    when(this.repositorioPartidaPregunta.buscarPreguntaPorCategoria(idCategoria))
      .thenReturn(pregunta);
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
  public void deberiaMezclarLasOpcionesSinPerderNingunaAlObtenerPreguntaPorCategoria() {
    givenTengoElIdentificadorDeCategoria(1L);
    givenExisteUnaPreguntaConCuatroOpciones(1L);

    whenObtengoLaPreguntaPorCategoria();

    thenLaPreguntaMantieneSusCuatroOpcionesOriginales();
  }

  private void givenExisteUnaPreguntaConCuatroOpciones(Long idCategoria) {
    Categoria categoria = new Categoria("Historia");
    categoria.setId(idCategoria);

    Pregunta pregunta = new Pregunta("¿En qué año se descubrió América?", categoria);
    pregunta.setIdentificador(101L);

    List<Opcion> opciones = new ArrayList<>();
    opciones.add(new Opcion("1492", true));
    opciones.add(new Opcion("1810", false));
    opciones.add(new Opcion("1776", false));
    opciones.add(new Opcion("1914", false));
    pregunta.setOpciones(opciones);

    when(this.repositorioPartidaPregunta.buscarPreguntaPorCategoria(idCategoria))
      .thenReturn(pregunta);
  }

  private void thenLaPreguntaMantieneSusCuatroOpcionesOriginales() {
    assertThat(this.preguntaObtenida, is(notNullValue()));
    List<Opcion> opciones = this.preguntaObtenida.getOpciones();

    assertThat(opciones.size(), equalTo(4));

    List<String> textos = new ArrayList<>();
    for (Opcion op : opciones) {
      textos.add(op.getTexto());
    }

    assertThat(textos.contains("1492"), is(true));
    assertThat(textos.contains("1810"), is(true));
    assertThat(textos.contains("1776"), is(true));
    assertThat(textos.contains("1914"), is(true));
  }

  @Test
  public void deberiaVerificarQueLaRespuestaSeleccionadaEsCorrecta() {
    givenTengoElIdentificadorDeOpcion(1L);
    givenExisteUnaOpcionEnElRepositorio(1L, "1492", true);

    whenVerificoLaRespuesta();

    thenElResultadoDeLaRespuestaEs(true);
  }

  @Test
  public void deberiaVerificarQueLaRespuestaSeleccionadaEsIncorrecta() {
    givenTengoElIdentificadorDeOpcion(2L);
    givenExisteUnaOpcionEnElRepositorio(2L, "1810", false);

    whenVerificoLaRespuesta();

    thenElResultadoDeLaRespuestaEs(false);
  }

  private void givenTengoElIdentificadorDeOpcion(Long idOpcion) {
    this.idOpcionSeleccionada = idOpcion;
  }

  private void givenExisteUnaOpcionEnElRepositorio(
    Long idOpcion,
    String texto,
    boolean esCorrecta
  ) {
    Opcion opcion = new Opcion(texto, esCorrecta);
    opcion.setId(idOpcion);

    when(this.repositorioPartidaPregunta.buscarOpcionPorId(idOpcion)).thenReturn(opcion);
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
    givenTengoElIdentificadorDeOpcion(2L);
    givenExisteUnaPreguntaConOpcionCorrectaParaLaOpcion(2L, "1492");

    whenObtengoElTextoDeLaRespuestaCorrecta();

    thenElTextoCorrectoEs("1492");
  }

  private void givenExisteUnaPreguntaConOpcionCorrectaParaLaOpcion(
    Long idOpcion,
    String textoCorrecto
  ) {
    Opcion opcionCorrecta = new Opcion(textoCorrecto, true);
    opcionCorrecta.setId(1L);

    Opcion opcionIncorrecta = new Opcion("1810", false);
    opcionIncorrecta.setId(idOpcion);

    Pregunta pregunta = new Pregunta();
    pregunta.setIdentificador(101L);

    List<Opcion> opciones = new ArrayList<>();
    opciones.add(opcionCorrecta);
    opciones.add(opcionIncorrecta);
    pregunta.setOpciones(opciones);

    when(this.repositorioPartidaPregunta.buscarPreguntaPorOpcionId(idOpcion)).thenReturn(pregunta);
  }

  private void whenObtengoElTextoDeLaRespuestaCorrecta() {
    this.textoCorrectoObtenido =
      this.servicioPartidaPregunta.obtenerTextoRespuestaCorrecta(this.idOpcionSeleccionada);
  }

  private void thenElTextoCorrectoEs(String textoEsperado) {
    assertThat(this.textoCorrectoObtenido, equalTo(textoEsperado));
  }
}
