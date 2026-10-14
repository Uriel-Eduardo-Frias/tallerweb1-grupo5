package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import java.util.List;
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
public class RepositorioPartidaJugadorImplTest {

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private RepositorioPartidaJugador repositorio;

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerJugadorPorPartidaYUsuario() {
    Partida partida = givenTengoUnaPartida();
    Usuario usuario = givenTengoUnUsuario();
    PartidaJugador jugador = givenTengoUnJugador(partida, usuario);

    limpiarCacheDeHibernate();

    PartidaJugador resultado = whenBuscoJugadorPorPartidaYUsuario(partida.getId(), usuario.getId());

    thenObtengoElJugador(resultado, jugador.getId());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaRetornarNullCuandoNoExisteJugadorPorPartidaYUsuario() {
    Partida partida = givenTengoUnaPartida();
    Usuario usuario = givenTengoUnUsuario();

    limpiarCacheDeHibernate();

    PartidaJugador resultado = whenBuscoJugadorPorPartidaYUsuario(partida.getId(), usuario.getId());

    thenNoObtengoJugador(resultado);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnJugador() {
    Partida partida = givenTengoUnaPartida();
    Usuario usuario = givenTengoUnUsuario();
    PartidaJugador jugador = givenCreoUnJugador(partida, usuario);

    whenGuardoJugador(jugador);

    thenElJugadorQuedaPersistido(jugador);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerJugadoresPorPartida() {
    Partida partida = givenTengoUnaPartida();
    Usuario usuarioUno = givenTengoUnUsuario();
    Usuario usuarioDos = givenTengoOtroUsuario();

    givenTengoUnJugador(partida, usuarioUno);
    givenTengoUnJugador(partida, usuarioDos);

    limpiarCacheDeHibernate();

    List<PartidaJugador> resultado = whenBuscoJugadoresPorPartida(partida.getId());

    thenObtengoCantidadDeJugadores(resultado, 2);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaRetornarListaVaciaCuandoLaPartidaNoTieneJugadores() {
    Partida partida = givenTengoUnaPartida();

    limpiarCacheDeHibernate();

    List<PartidaJugador> resultado = whenBuscoJugadoresPorPartida(partida.getId());

    thenObtengoListaVacia(resultado);
  }

  // GIVEN: datos de prueba

  private Partida givenTengoUnaPartida() {
    Usuario host = givenTengoUnUsuario();

    Sala sala = new Sala(generarCodigoSala(), "Sala de prueba", host);

    sessionFactory.getCurrentSession().persist(sala);
    sessionFactory.getCurrentSession().flush();

    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);

    sessionFactory.getCurrentSession().persist(partida);
    sessionFactory.getCurrentSession().flush();

    return partida;
  }

  private Usuario givenTengoUnUsuario() {
    return givenCreoUnUsuario();
  }

  private Usuario givenTengoOtroUsuario() {
    return givenCreoUnUsuario();
  }

  private Usuario givenCreoUnUsuario() {
    String identificador = UUID.randomUUID().toString();

    Usuario usuario = new Usuario();
    usuario.setUsername("usuario-" + identificador);
    usuario.setEmail(identificador + "@test.com");

    sessionFactory.getCurrentSession().persist(usuario);
    sessionFactory.getCurrentSession().flush();

    return usuario;
  }

  private String generarCodigoSala() {
    return "S" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
  }

  private PartidaJugador givenTengoUnJugador(Partida partida, Usuario usuario) {
    PartidaJugador jugador = givenCreoUnJugador(partida, usuario);

    sessionFactory.getCurrentSession().persist(jugador);
    sessionFactory.getCurrentSession().flush();

    return jugador;
  }

  private PartidaJugador givenCreoUnJugador(Partida partida, Usuario usuario) {
    return new PartidaJugador(partida, usuario);
  }

  // WHEN: operaciones del repositorio

  private PartidaJugador whenBuscoJugadorPorPartidaYUsuario(Long partidaId, Long usuarioId) {
    return repositorio.obtenerPorPartidaYUsuario(partidaId, usuarioId);
  }

  private void whenGuardoJugador(PartidaJugador jugador) {
    repositorio.guardar(jugador);
    sessionFactory.getCurrentSession().flush();
  }

  private List<PartidaJugador> whenBuscoJugadoresPorPartida(Long partidaId) {
    return repositorio.obtenerPorPartida(partidaId);
  }

  // THEN: verificaciones

  private void thenObtengoElJugador(PartidaJugador resultado, Long idEsperado) {
    assertThat(resultado, is(notNullValue()));
    assertThat(resultado.getId(), is(equalTo(idEsperado)));
  }

  private void thenNoObtengoJugador(PartidaJugador resultado) {
    assertThat(resultado, is(nullValue()));
  }

  private void thenElJugadorQuedaPersistido(PartidaJugador jugador) {
    assertThat(jugador.getId(), is(notNullValue()));

    sessionFactory.getCurrentSession().clear();

    PartidaJugador buscado = sessionFactory
      .getCurrentSession()
      .get(PartidaJugador.class, jugador.getId());

    assertThat(buscado, is(notNullValue()));
  }

  private void thenObtengoCantidadDeJugadores(
    List<PartidaJugador> jugadores,
    int cantidadEsperada
  ) {
    assertThat(jugadores, is(notNullValue()));
    assertThat(jugadores.size(), is(equalTo(cantidadEsperada)));
  }

  private void thenObtengoListaVacia(List<PartidaJugador> jugadores) {
    assertThat(jugadores, is(notNullValue()));
    assertThat(jugadores.isEmpty(), is(true));
  }

  private void limpiarCacheDeHibernate() {
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();
  }
}
