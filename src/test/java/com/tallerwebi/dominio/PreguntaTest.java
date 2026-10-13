package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.modelo.Categoria;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Pregunta;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class PreguntaTest {

  @Test
  void dadoQueCreoUnaPreguntaCuandoUsoElConstructorVacioEntoncesInicializaLasOpciones() {
    Pregunta pregunta = new Pregunta();

    assertNotNull(pregunta.getOpciones());
    assertTrue(pregunta.getOpciones().isEmpty());
  }

  @Test
  void dadoQueCreoUnaPreguntaCuandoIndicoDescripcionYCategoriaEntoncesLasAsigna() {
    Categoria categoria = new Categoria("Historia");

    Pregunta pregunta = new Pregunta("¿En qué año?", categoria);

    assertEquals("¿En qué año?", pregunta.getDescripcion());
    assertSame(categoria, pregunta.getCategoria());
  }

  @Test
  void dadoQueTengoUnaPreguntaCuandoModificoSusDatosEntoncesDevuelveLosValoresActualizados() {
    Pregunta pregunta = new Pregunta();
    Categoria categoria = new Categoria("Ciencia");
    Opcion opcion = new Opcion("Respuesta", true);
    ArrayList<Opcion> opciones = new ArrayList<>();
    opciones.add(opcion);

    pregunta.setIdentificador(15L);
    pregunta.setDescripcion("¿Cuál es la respuesta?");
    pregunta.setCategoria(categoria);
    pregunta.setOpciones(opciones);

    assertEquals(15L, pregunta.getIdentificador());
    assertEquals("¿Cuál es la respuesta?", pregunta.getDescripcion());
    assertSame(categoria, pregunta.getCategoria());
    assertSame(opciones, pregunta.getOpciones());
  }
}
