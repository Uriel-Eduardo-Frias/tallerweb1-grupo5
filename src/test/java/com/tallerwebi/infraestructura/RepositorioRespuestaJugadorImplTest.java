package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.modelo.Categoria;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.Pregunta;
import com.tallerwebi.dominio.modelo.RespuestaJugador;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioRespuestaJugador;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import java.time.LocalDateTime;
import java.util.UUID;
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
public class RepositorioRespuestaJugadorImplTest {

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private RepositorioRespuestaJugador repositorio;

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnaRespuestaJugador() {
    RespuestaJugador respuesta = givenTengoUnaRespuesta();

    whenGuardoLaRespuesta(respuesta);

    thenLaRespuestaExisteEnLaBaseDeDatos(respuesta);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaIndicarQueExisteUnaRespuesta() {
    RespuestaJugador respuesta = givenTengoUnaRespuesta();
    whenGuardoLaRespuesta(respuesta);

    Long preguntaId = respuesta.getPregunta().getIdentificador();
    Long jugadorId = respuesta.getPartidaJugador().getId();

    limpiarCacheDeHibernate();

    boolean resultado = whenVerificoSiExisteRespuesta(preguntaId, jugadorId);

    thenObtengoResultado(resultado, true);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaIndicarQueNoExisteUnaRespuesta() {
    boolean resultado = whenVerificoSiExisteRespuesta(-10L, -20L);

    thenObtengoResultado(resultado, false);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerLasRespuestasDeUnaPregunta() {
    RespuestaJugador respuesta = givenTengoUnaRespuesta();
    whenGuardoLaRespuesta(respuesta);

    Long preguntaId = respuesta.getPregunta().getIdentificador();

    limpiarCacheDeHibernate();

    var resultado = whenObtengoRespuestasPorPregunta(preguntaId);

    thenObtengoUnaListaDeRespuestas(resultado, 1);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaRetornarListaVaciaCuandoNoHayRespuestas() {
    var resultado = whenObtengoRespuestasPorPregunta(-10L);

    thenObtengoUnaListaVacia(resultado);
  }

  // GIVEN

  private RespuestaJugador givenTengoUnaRespuesta() {
    Usuario usuario = givenTengoUnUsuario();
    Sala sala = givenTengoUnaSala(usuario);
    Partida partida = givenTengoUnaPartida(sala);
    PartidaJugador jugador = givenTengoUnPartidaJugador(partida, usuario);
    Pregunta pregunta = givenTengoUnaPregunta();
    Opcion opcion = givenTengoUnaOpcion(pregunta);

    return new RespuestaJugador(jugador, pregunta, opcion, false, LocalDateTime.now());
  }

  private Usuario givenTengoUnUsuario() {
    String identificador = UUID.randomUUID().toString();

    Usuario usuario = new Usuario();
    usuario.setUsername("usuario-" + identificador);
    usuario.setEmail(identificador + "@test.com");

    sessionFactory.getCurrentSession().persist(usuario);
    sessionFactory.getCurrentSession().flush();

    return usuario;
  }

  private Sala givenTengoUnaSala(Usuario host) {
    String codigo = "S" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);

    Sala sala = new Sala(codigo, "Sala de prueba", host);

    sessionFactory.getCurrentSession().persist(sala);
    sessionFactory.getCurrentSession().flush();

    return sala;
  }

  private Partida givenTengoUnaPartida(Sala sala) {
    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    sessionFactory.getCurrentSession().persist(partida);
    sessionFactory.getCurrentSession().flush();

    return partida;
  }

  private PartidaJugador givenTengoUnPartidaJugador(Partida partida, Usuario usuario) {
    PartidaJugador jugador = new PartidaJugador(partida, usuario);

    sessionFactory.getCurrentSession().persist(jugador);
    sessionFactory.getCurrentSession().flush();

    return jugador;
  }

  private Pregunta givenTengoUnaPregunta() {
    Categoria categoria = new Categoria("Categoria-" + UUID.randomUUID());

    sessionFactory.getCurrentSession().persist(categoria);
    sessionFactory.getCurrentSession().flush();

    Pregunta pregunta = new Pregunta("Pregunta de prueba", categoria);

    sessionFactory.getCurrentSession().persist(pregunta);
    sessionFactory.getCurrentSession().flush();

    return pregunta;
  }

  private Opcion givenTengoUnaOpcion(Pregunta pregunta) {
    Opcion opcion = new Opcion("Opcion de prueba", false);
    opcion.setPregunta(pregunta);

    pregunta.getOpciones().add(opcion);

    sessionFactory.getCurrentSession().persist(opcion);
    sessionFactory.getCurrentSession().flush();

    return opcion;
  }

  // WHEN

  private void whenGuardoLaRespuesta(RespuestaJugador respuesta) {
    repositorio.guardar(respuesta);
    sessionFactory.getCurrentSession().flush();
  }

  private boolean whenVerificoSiExisteRespuesta(Long preguntaId, Long partidaJugadorId) {
    return repositorio.existeRespuesta(preguntaId, partidaJugadorId);
  }

  private java.util.List<RespuestaJugador> whenObtengoRespuestasPorPregunta(Long preguntaId) {
    return repositorio.obtenerPorPregunta(preguntaId);
  }

  // THEN

  private void thenLaRespuestaExisteEnLaBaseDeDatos(RespuestaJugador respuesta) {
    assertThat(respuesta.getId(), is(notNullValue()));

    limpiarCacheDeHibernate();

    RespuestaJugador buscada = sessionFactory
      .getCurrentSession()
      .get(RespuestaJugador.class, respuesta.getId());

    assertThat(buscada, is(notNullValue()));
    assertThat(buscada.getId(), is(equalTo(respuesta.getId())));
  }

  private void thenObtengoResultado(boolean resultado, boolean resultadoEsperado) {
    assertThat(resultado, is(equalTo(resultadoEsperado)));
  }

  private void thenObtengoUnaListaDeRespuestas(
    java.util.List<RespuestaJugador> respuestas,
    int cantidadEsperada
  ) {
    assertThat(respuestas, is(notNullValue()));
    assertThat(respuestas.size(), is(equalTo(cantidadEsperada)));
  }

  private void thenObtengoUnaListaVacia(java.util.List<RespuestaJugador> respuestas) {
    assertThat(respuestas, is(notNullValue()));
    assertThat(respuestas.isEmpty(), is(true));
  }

  private void limpiarCacheDeHibernate() {
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();
  }
}
