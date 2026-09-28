package com.tallerwebi.dominio;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ServicioPartidaPreguntaImpl implements ServicioPartidaPregunta {

  private final Map<Long, Categoria> tablaCategorias = new HashMap<>();
  private final Map<Long, Pregunta> tablaPreguntas = new HashMap<>();

  public ServicioPartidaPreguntaImpl() {
    inicializarDatos();
  }

  private void inicializarDatos() {
    // 1. Historia
    Categoria historia = new Categoria("Historia");
    historia.setId(1L);
    tablaCategorias.put(1L, historia);

    Pregunta pregHistoria = new Pregunta("¿En qué año se descubrió América?", historia);
    pregHistoria.setIdentificador(101L);
    pregHistoria.agregarOpcion(new Opcion("1492", true));
    pregHistoria.agregarOpcion(new Opcion("1810", false));
    pregHistoria.agregarOpcion(new Opcion("1776", false));
    pregHistoria.agregarOpcion(new Opcion("1914", false));
    tablaPreguntas.put(1L, pregHistoria);

    // 2. Ciencia
    Categoria ciencia = new Categoria("Ciencia");
    ciencia.setId(2L);
    tablaCategorias.put(2L, ciencia);

    Pregunta pregCiencia = new Pregunta("¿Cuál es el símbolo químico del agua?", ciencia);
    pregCiencia.setIdentificador(102L);
    pregCiencia.agregarOpcion(new Opcion("H2O", true));
    pregCiencia.agregarOpcion(new Opcion("CO2", false));
    pregCiencia.agregarOpcion(new Opcion("O2", false));
    pregCiencia.agregarOpcion(new Opcion("NaCl", false));
    tablaPreguntas.put(2L, pregCiencia);

    // 3. Geografía
    Categoria geografia = new Categoria("Geografía");
    geografia.setId(3L);
    tablaCategorias.put(3L, geografia);

    Pregunta pregGeografia = new Pregunta("¿Cuál es el río más largo del mundo?", geografia);
    pregGeografia.setIdentificador(103L);
    pregGeografia.agregarOpcion(new Opcion("Amazonas", true));
    pregGeografia.agregarOpcion(new Opcion("Nilo", false));
    pregGeografia.agregarOpcion(new Opcion("Misisipi", false));
    pregGeografia.agregarOpcion(new Opcion("Danubio", false));
    tablaPreguntas.put(3L, pregGeografia);

    // 4. Deportes
    Categoria deportes = new Categoria("Deportes");
    deportes.setId(4L);
    tablaCategorias.put(4L, deportes);

    Pregunta pregDeportes = new Pregunta(
      "¿Cada cuántos años se celebran los Juegos Olímpicos?",
      deportes
    );
    pregDeportes.setIdentificador(104L);
    pregDeportes.agregarOpcion(new Opcion("4 años", true));
    pregDeportes.agregarOpcion(new Opcion("2 años", false));
    pregDeportes.agregarOpcion(new Opcion("6 años", false));
    pregDeportes.agregarOpcion(new Opcion("5 años", false));
    tablaPreguntas.put(4L, pregDeportes);

    // 5. Arte
    Categoria arte = new Categoria("Arte");
    arte.setId(5L);
    tablaCategorias.put(5L, arte);

    Pregunta pregArte = new Pregunta("¿Quién pintó la 'Mona Lisa'?", arte);
    pregArte.setIdentificador(105L);
    pregArte.agregarOpcion(new Opcion("Leonardo da Vinci", true));
    pregArte.agregarOpcion(new Opcion("Pablo Picasso", false));
    pregArte.agregarOpcion(new Opcion("Vincent van Gogh", false));
    pregArte.agregarOpcion(new Opcion("Miguel Ángel", false));
    tablaPreguntas.put(5L, pregArte);

    // 6. Entretenimiento
    Categoria entretenimiento = new Categoria("Entretenimiento");
    entretenimiento.setId(6L);
    tablaCategorias.put(6L, entretenimiento);

    Pregunta pregEntretenimiento = new Pregunta(
      "¿Cómo se llama el hobbit protagonista de 'El Señor de los Anillos'?",
      entretenimiento
    );
    pregEntretenimiento.setIdentificador(106L);
    pregEntretenimiento.agregarOpcion(new Opcion("Frodo Bolsón", true));
    pregEntretenimiento.agregarOpcion(new Opcion("Bilbo Bolsón", false));
    pregEntretenimiento.agregarOpcion(new Opcion("Sam Gamyi", false));
    pregEntretenimiento.agregarOpcion(new Opcion("Pippin", false));
    tablaPreguntas.put(6L, pregEntretenimiento);
  }

  @Override
  public Map<Long, Categoria> obtenerCategorias() {
    return tablaCategorias;
  }

  @Override
  public Pregunta obtenerPreguntaPorCategoria(Long identificadorCategoria) {
    return tablaPreguntas.get(identificadorCategoria);
  }
}
