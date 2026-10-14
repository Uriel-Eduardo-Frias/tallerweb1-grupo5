package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.enumeration.TipoComodin;
import com.tallerwebi.dominio.modelo.Comodin;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
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
public class RepositorioPartidaImplTest {

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private RepositorioPartida repositorio;

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarPartidaPorId() {
    Partida partida = givenTengoUnaPartida();

    limpiarCacheDeHibernate();

    Partida resultado = whenBuscoPartidaPorId(partida.getId());

    thenObtengoLaPartida(resultado, partida.getId());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarPartidaPorCodigoSala() {
    String codigoSala = "ABC123";
    Partida partida = givenTengoUnaPartidaConCodigoSala(codigoSala);

    limpiarCacheDeHibernate();

    Partida resultado = whenBuscoPartidaPorCodigoSala(codigoSala);

    thenObtengoLaPartida(resultado, partida.getId());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnaPartida() {
    Sala sala = givenTengoUnaSalaConCodigo("GUARDAR1");
    Partida partida = givenCreoUnaPartida(sala);

    whenGuardoPartida(partida);

    thenLaPartidaQuedaPersistida(partida);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaListarComodines() {
    givenTengoUnComodin();
    givenTengoOtroComodin();

    limpiarCacheDeHibernate();

    List<Comodin> resultado = whenListoComodines();

    thenObtengoAlMenosXCantidad(resultado, 2);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaRetornarNullCuandoElCodigoDelComodinEsNull() {
    Comodin resultado = whenBuscoComodinPorCodigo(null);

    thenNoObtengoComodin(resultado);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarComodinPorCodigo() {
    TipoComodin codigo = TipoComodin.values()[0];
    givenTengoUnComodinConCodigo(codigo);

    limpiarCacheDeHibernate();

    Comodin resultado = whenBuscoComodinPorCodigo(codigo);

    thenObtengoUnComodin(resultado, codigo);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaListarPartidasDeUnUsuario() {
    Usuario usuario = givenTengoUnUsuario();
    givenTengoPartidasDeUnUsuario(usuario, 2);

    limpiarCacheDeHibernate();

    List<PartidaJugador> resultado = whenListoPartidasDeUsuario(usuario.getId());

    thenObtengoAlMenosXCantidad(resultado, 2);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaRetornarListaVaciaCuandoElUsuarioNoTienePartidas() {
    Long usuarioId = -999L;

    List<PartidaJugador> resultado = whenListoPartidasDeUsuario(usuarioId);

    thenObtengoListaVacia(resultado);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarPartidaPorIdConRondas() {
    Partida partida = givenTengoUnaPartida();

    limpiarCacheDeHibernate();

    Partida resultado = whenBuscoPartidaPorIdConRondas(partida.getId());

    thenObtengoLaPartida(resultado, partida.getId());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaRetornarNullCuandoNoExistePartidaConRondas() {
    Partida resultado = whenBuscoPartidaPorIdConRondas(-999L);

    thenNoObtengoPartida(resultado);
  }

  // GIVEN: preparación de los datos

  private Partida givenTengoUnaPartida() {
    String codigoSala = generarCodigoSala();
    Sala sala = givenTengoUnaSalaConCodigo(codigoSala);

    return givenTengoUnaPartida(sala);
  }

  private Partida givenTengoUnaPartida(Sala sala) {
    Partida partida = givenCreoUnaPartida(sala);

    sessionFactory.getCurrentSession().persist(partida);
    sessionFactory.getCurrentSession().flush();

    return partida;
  }

  private Partida givenTengoUnaPartidaConCodigoSala(String codigoSala) {
    Sala sala = givenTengoUnaSalaConCodigo(codigoSala);

    return givenTengoUnaPartida(sala);
  }

  private Partida givenCreoUnaPartida(Sala sala) {
    return new Partida(sala, EstadoPartida.EN_CURSO);
  }

  private Sala givenTengoUnaSalaConCodigo(String codigoSala) {
    Usuario host = givenTengoUnUsuario();

    Sala sala = new Sala(codigoSala, "Sala de prueba", host);

    sessionFactory.getCurrentSession().persist(sala);
    sessionFactory.getCurrentSession().flush();

    return sala;
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

  private void givenTengoPartidasDeUnUsuario(Usuario usuario, int cantidad) {
    for (int i = 0; i < cantidad; i++) {
      Sala sala = givenTengoUnaSalaConCodigo(generarCodigoSala());
      Partida partida = givenTengoUnaPartida(sala);

      PartidaJugador jugador = new PartidaJugador(partida, usuario);

      sessionFactory.getCurrentSession().persist(jugador);
    }

    sessionFactory.getCurrentSession().flush();
  }

  private String generarCodigoSala() {
    return "S" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
  }

  private void givenTengoUnComodin() {
    Comodin comodin = new Comodin(
      TipoComodin.values()[0],
      "Comodin de prueba 1",
      "Descripcion del comodin 1",
      null
    );

    sessionFactory.getCurrentSession().persist(comodin);
    sessionFactory.getCurrentSession().flush();
  }

  private void givenTengoOtroComodin() {
    if (TipoComodin.values().length < 2) {
      throw new IllegalStateException("Se necesitan al menos dos valores de TipoComodin");
    }

    Comodin comodin = new Comodin(
      TipoComodin.values()[1],
      "Comodin de prueba 2",
      "Descripcion del comodin 2",
      null
    );

    sessionFactory.getCurrentSession().persist(comodin);
    sessionFactory.getCurrentSession().flush();
  }

  private void givenTengoUnComodinConCodigo(TipoComodin codigo) {
    Comodin comodin = new Comodin(
      codigo,
      "Comodin de prueba",
      "Descripcion del comodin de prueba",
      null
    );

    sessionFactory.getCurrentSession().persist(comodin);
    sessionFactory.getCurrentSession().flush();
  }

  // WHEN: operaciones del repositorio

  private void whenGuardoPartida(Partida partida) {
    repositorio.guardar(partida);
    sessionFactory.getCurrentSession().flush();
  }

  private Partida whenBuscoPartidaPorId(Long id) {
    return repositorio.obtenerPorId(id);
  }

  private Partida whenBuscoPartidaPorCodigoSala(String codigoSala) {
    return repositorio.obtenerPorCodigoSala(codigoSala);
  }

  private Partida whenBuscoPartidaPorIdConRondas(Long id) {
    return repositorio.obtenerPorIdConRondas(id);
  }

  private Comodin whenBuscoComodinPorCodigo(TipoComodin codigo) {
    return repositorio.buscarComodinPorCodigo(codigo);
  }

  private List<Comodin> whenListoComodines() {
    return repositorio.listarComodines();
  }

  private List<PartidaJugador> whenListoPartidasDeUsuario(Long usuarioId) {
    return repositorio.listarPartidasDeUsuario(usuarioId);
  }

  // THEN: verificaciones

  private void thenObtengoLaPartida(Partida resultado, Long idEsperado) {
    assertThat(resultado, is(notNullValue()));
    assertThat(resultado.getId(), is(equalTo(idEsperado)));
  }

  private void thenLaPartidaQuedaPersistida(Partida partida) {
    assertThat(partida.getId(), is(notNullValue()));

    limpiarCacheDeHibernate();

    Partida buscada = sessionFactory.getCurrentSession().get(Partida.class, partida.getId());

    assertThat(buscada, is(notNullValue()));
  }

  private void thenObtengoUnComodin(Comodin resultado, TipoComodin codigoEsperado) {
    assertThat(resultado, is(notNullValue()));
    assertThat(resultado.getCodigo(), is(equalTo(codigoEsperado)));
  }

  private void thenNoObtengoComodin(Comodin resultado) {
    assertThat(resultado, is(nullValue()));
  }

  private void thenObtengoAlMenosXCantidad(List<?> resultados, int cantidadMinima) {
    assertThat(resultados, is(notNullValue()));
    assertThat(resultados.size() >= cantidadMinima, is(true));
  }

  private void thenObtengoListaVacia(List<?> resultados) {
    assertThat(resultados, is(notNullValue()));
    assertThat(resultados.isEmpty(), is(true));
  }

  private void thenNoObtengoPartida(Partida resultado) {
    assertThat(resultado, is(nullValue()));
  }

  private void limpiarCacheDeHibernate() {
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();
  }
}
