package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.modelo.Categoria;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Pregunta;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaPregunta;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringWebTestConfig.class, HibernateTestConfig.class })
public class RepositorioPartidaPreguntaTest {

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private RepositorioPartidaPregunta repositorio;

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerTodasLasCategorias() {
    givenTengoUnaCategoria("Historia");
    givenTengoUnaCategoria("Ciencia");

    limpiarCacheDeHibernate();

    List<Categoria> categorias = whenObtengoTodasLasCategorias();

    thenObtengoAlMenosXCategorias(categorias, 2);
  }

  private Categoria givenTengoUnaCategoria(String nombre) {
    Categoria categoria = new Categoria(nombre);
    sessionFactory.getCurrentSession().persist(categoria);
    return categoria;
  }

  private List<Categoria> whenObtengoTodasLasCategorias() {
    return repositorio.obtenerTodasLasCategorias();
  }

  private void thenObtengoAlMenosXCategorias(List<Categoria> categorias, int cantidadMinima) {
    assertThat(categorias, is(notNullValue()));
    assertThat(categorias.size() >= cantidadMinima, is(true));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarPreguntaPorCategoriaConSusOpciones() {
    Categoria historia = givenTengoUnaCategoria("Historia");

    Pregunta pregunta = givenTengoUnaPreguntaConCategoria(
      "¿En qué año se descubrió América?",
      historia
    );
    givenTengoUnaOpcionParaPregunta("1492", true, pregunta);
    givenTengoUnaOpcionParaPregunta("1810", false, pregunta);

    limpiarCacheDeHibernate();

    Pregunta buscada = whenBuscoPreguntaPorCategoria(historia.getId());

    thenObtengoLaPreguntaConOpciones(
      buscada,
      "¿En qué año se descubrió América?",
      historia.getId(),
      2
    );
  }

  private Pregunta givenTengoUnaPreguntaConCategoria(String descripcion, Categoria categoria) {
    Pregunta pregunta = new Pregunta(descripcion, categoria);
    sessionFactory.getCurrentSession().persist(pregunta);
    return pregunta;
  }

  private Opcion givenTengoUnaOpcionParaPregunta(
    String texto,
    boolean esCorrecta,
    Pregunta pregunta
  ) {
    Opcion opcion = new Opcion(texto, esCorrecta);
    opcion.setPregunta(pregunta);
    sessionFactory.getCurrentSession().persist(opcion);
    return opcion;
  }

  private Pregunta whenBuscoPreguntaPorCategoria(Long categoriaId) {
    return repositorio.buscarPreguntaPorCategoria(categoriaId);
  }

  private void thenObtengoLaPreguntaConOpciones(
    Pregunta buscada,
    String descripcionEsperada,
    Long categoriaIdEsperada,
    int cantidadOpciones
  ) {
    assertThat(buscada, is(notNullValue()));
    assertThat(buscada.getDescripcion(), equalTo(descripcionEsperada));
    assertThat(buscada.getCategoria().getId(), equalTo(categoriaIdEsperada));
    assertThat(buscada.getOpciones(), is(notNullValue()));
    assertThat(buscada.getOpciones().size(), equalTo(cantidadOpciones));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarOpcionPorId() {
    Opcion opcion = givenTengoUnaOpcionSola("1492", true);

    limpiarCacheDeHibernate();

    Opcion buscada = whenBuscoOpcionPorId(opcion.getId());

    thenObtengoLaOpcion(buscada, "1492", true);
  }

  private Opcion givenTengoUnaOpcionSola(String texto, boolean esCorrecta) {
    Opcion opcion = new Opcion(texto, esCorrecta);
    sessionFactory.getCurrentSession().persist(opcion);
    return opcion;
  }

  private Opcion whenBuscoOpcionPorId(Long opcionId) {
    return repositorio.buscarOpcionPorId(opcionId);
  }

  private void thenObtengoLaOpcion(
    Opcion buscada,
    String textoEsperado,
    boolean esCorrectaEsperada
  ) {
    assertThat(buscada, is(notNullValue()));
    assertThat(buscada.getTexto(), equalTo(textoEsperado));
    assertThat(buscada.getEsCorrecta(), is(esCorrectaEsperada));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarPreguntaPorOpcionId() {
    Categoria geografia = givenTengoUnaCategoria("Geografía");
    Pregunta pregunta = givenTengoUnaPreguntaConCategoria("¿Cuál es el río más largo?", geografia);
    Opcion opcion = givenTengoUnaOpcionParaPregunta("Amazonas", true, pregunta);

    limpiarCacheDeHibernate();

    Pregunta resultado = whenBuscoPreguntaPorOpcionId(opcion.getId());

    thenObtengoLaPreguntaConOpciones(resultado, "¿Cuál es el río más largo?", geografia.getId(), 1);
  }

  private Pregunta whenBuscoPreguntaPorOpcionId(Long opcionId) {
    return repositorio.buscarPreguntaPorOpcionId(opcionId);
  }

  private void limpiarCacheDeHibernate() {
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();
  }
}
