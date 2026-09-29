package com.tallerwebi.dominio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ServicioPartidaPreguntaImpl implements ServicioPartidaPregunta {

  private final Map<Long, Categoria> tablaCategorias = new HashMap<>();
  private final Map<Long, Pregunta> tablaPreguntasPorCategoria = new HashMap<>();
  private final Map<Long, Opcion> tablaOpcionesPorId = new HashMap<>();
  private final Map<Long, Pregunta> tablaPreguntasPorOpcionId = new HashMap<>();

  private Long secuenciaOpciones = 1L;

  // Se encarga de inicializar y precargar en memoria las categorías, preguntas y opciones disponibles al instanciar el servicio
  public ServicioPartidaPreguntaImpl() {
    inicializarDatos();
  }

  // Define la carga inicial estructurada vinculando cada categoría temática con su respectiva pregunta y alternativas
  private void inicializarDatos() {
    registrarCategoriaConPregunta(
      1L,
      "Historia",
      101L,
      "¿En qué año se descubrió América?",
      List.of(
        crearOpcion("1492", true),
        crearOpcion("1810", false),
        crearOpcion("1776", false),
        crearOpcion("1914", false)
      )
    );

    registrarCategoriaConPregunta(
      2L,
      "Ciencia",
      102L,
      "¿Cuál es el símbolo químico del agua?",
      List.of(
        crearOpcion("H2O", true),
        crearOpcion("CO2", false),
        crearOpcion("O2", false),
        crearOpcion("NaCl", false)
      )
    );

    registrarCategoriaConPregunta(
      3L,
      "Geografía",
      103L,
      "¿Cuál es el río más largo del mundo?",
      List.of(
        crearOpcion("Amazonas", true),
        crearOpcion("Nilo", false),
        crearOpcion("Misisipi", false),
        crearOpcion("Danubio", false)
      )
    );

    registrarCategoriaConPregunta(
      4L,
      "Deportes",
      104L,
      "¿Cada cuántos años se celebran los Juegos Olímpicos?",
      List.of(
        crearOpcion("4 años", true),
        crearOpcion("2 años", false),
        crearOpcion("6 años", false),
        crearOpcion("5 años", false)
      )
    );

    registrarCategoriaConPregunta(
      5L,
      "Arte",
      105L,
      "¿Quién pintó la 'Mona Lisa'?",
      List.of(
        crearOpcion("Leonardo da Vinci", true),
        crearOpcion("Pablo Picasso", false),
        crearOpcion("Vincent van Gogh", false),
        crearOpcion("Miguel Ángel", false)
      )
    );

    registrarCategoriaConPregunta(
      6L,
      "Entretenimiento",
      106L,
      "¿Cómo se llama el hobbit protagonista de 'El Señor de los Anillos'?",
      List.of(
        crearOpcion("Frodo Bolsón", true),
        crearOpcion("Bilbo Bolsón", false),
        crearOpcion("Sam Gamyi", false),
        crearOpcion("Pippin", false)
      )
    );
  }

  // Centraliza la creación de entidades e indexa las opciones y preguntas en mapas clave-valor para permitir búsquedas directas
  private void registrarCategoriaConPregunta(
    Long categoriaId,
    String nombreCategoria,
    Long preguntaId,
    String textoPregunta,
    List<Opcion> opciones
  ) {
    Categoria categoria = new Categoria(nombreCategoria);
    categoria.setId(categoriaId);
    this.tablaCategorias.put(categoriaId, categoria);

    Pregunta pregunta = new Pregunta(textoPregunta, categoria);
    pregunta.setIdentificador(preguntaId);

    for (Opcion opcion : opciones) {
      pregunta.agregarOpcion(opcion);
      this.tablaOpcionesPorId.put(opcion.getId(), opcion);
      this.tablaPreguntasPorOpcionId.put(opcion.getId(), pregunta);
    }

    this.tablaPreguntasPorCategoria.put(categoriaId, pregunta);
  }

  // Fabrica una nueva opción asignándole un identificador autoincremental único antes de registrarla
  private Opcion crearOpcion(String texto, boolean esCorrecta) {
    Opcion opcion = new Opcion(texto, esCorrecta);
    opcion.setId(this.secuenciaOpciones);
    this.secuenciaOpciones = this.secuenciaOpciones + 1L;
    return opcion;
  }

  // Devuelve el mapa completo con todas las categorías existentes para que la vista pueda listarlas
  @Override
  public Map<Long, Categoria> obtenerCategorias() {
    return this.tablaCategorias;
  }

  // Recupera de manera directa la pregunta vinculada al identificador de una categoría seleccionada
  @Override
  public Pregunta obtenerPreguntaPorCategoria(Long identificadorCategoria) {
    return this.tablaPreguntasPorCategoria.get(identificadorCategoria);
  }

  // Determina si el identificador de opción enviado por el usuario corresponde a la respuesta verdadera
  @Override
  public Boolean verificarRespuesta(Long opcionId) {
    if (opcionId == null) {
      return false;
    }
    Opcion opcion = this.tablaOpcionesPorId.get(opcionId);
    if (opcion != null && Boolean.TRUE.equals(opcion.getEsCorrecta())) {
      return true;
    }
    return false;
  }

  // Localiza la pregunta asociada a la opción seleccionada y rescata el texto de su alternativa correcta para retroalimentar la vista
  @Override
  public String obtenerTextoRespuestaCorrecta(Long opcionId) {
    if (opcionId == null) {
      return "";
    }

    Pregunta pregunta = this.tablaPreguntasPorOpcionId.get(opcionId);
    if (pregunta != null) {
      for (Opcion opcion : pregunta.getOpciones()) {
        if (Boolean.TRUE.equals(opcion.getEsCorrecta())) {
          return opcion.getTexto();
        }
      }
    }
    return "";
  }
}
