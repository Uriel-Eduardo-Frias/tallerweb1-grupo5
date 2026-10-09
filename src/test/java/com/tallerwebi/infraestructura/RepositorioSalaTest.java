package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.*;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HibernateInfraestructuraTestConfig.class)
public class RepositorioSalaTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioSalaImpl repositorioSala;

  @BeforeEach
  void inicializar() {
    repositorioSala = new RepositorioSalaImpl();

    ReflectionTestUtils.setField(repositorioSala, "sessionFactory", sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  void deberiaGuardarYBuscarSalaPorCodigo() {
    // given
    Sala sala = givenUnaSalaPersistida("TRV-1001");

    // when
    Sala encontrada = repositorioSala.obtenerSalaPorCodigo("TRV-1001");

    // then
    assertThat(encontrada.getCodigo(), equalTo(sala.getCodigo()));
    assertThat(encontrada.getNombre(), equalTo("Sala de prueba"));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverNullSiNoEncuentraSalaPorCodigo() {
    // when
    Sala encontrada = repositorioSala.obtenerSalaPorCodigo("NO-EXISTE");

    // then
    assertThat(encontrada, nullValue());
  }

  @Test
  @Transactional
  @Rollback
  void deberiaListarLasSalasGuardadas() {
    // given
    givenUnaSalaPersistida("TRV-1002");

    // when
    var salas = repositorioSala.listarSalas();

    // then
    assertThat(salas, hasSize(1));
    assertThat(salas.get(0).getCodigo(), equalTo("TRV-1002"));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaObtenerSalaConHostYJugadores() {
    // given
    givenUnaSalaPersistidaConJugador("TRV-1003");

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    // when
    Sala sala = repositorioSala.obtenerPorCodigoConJugadores("TRV-1003");

    // then
    assertThat(sala.getHost().getUsername(), equalTo("juan-TRV-1003"));
    assertThat(sala.getJugadores(), hasSize(1));
    assertThat(sala.getJugadores().get(0).getUsuario().getUsername(), equalTo("juan-TRV-1003"));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverNullSiNoExisteSalaConJugadores() {
    // when
    Sala sala = repositorioSala.obtenerPorCodigoConJugadores("NO-EXISTE");

    // then
    assertThat(sala, nullValue());
  }

  private Sala givenUnaSalaPersistida(String codigo) {
    Usuario host = givenUnUsuario("juan-" + codigo);
    sessionFactory.getCurrentSession().persist(host);

    Sala sala = new Sala(codigo, "Sala de prueba", host);
    sala.setEstado(EstadoSala.EN_ESPERA);
    sala.setModoJuego(ModoJuego.TURNO_TODOS);

    repositorioSala.guardar(sala);
    return sala;
  }

  private Sala givenUnaSalaPersistidaConJugador(String codigo) {
    Usuario host = givenUnUsuario("juan-" + codigo);
    sessionFactory.getCurrentSession().persist(host);

    Sala sala = new Sala(codigo, "Sala de prueba", host);
    sala.setEstado(EstadoSala.EN_ESPERA);
    sala.setModoJuego(ModoJuego.TURNO_TODOS);

    SalaJugador jugador = new SalaJugador(sala, EstadoJugador.CONECTADO, true, host);
    sala.getJugadores().add(jugador);

    repositorioSala.guardar(sala);
    return sala;
  }

  private Usuario givenUnUsuario(String username) {
    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    usuario.setEmail(username + "@test.com");
    usuario.setPassword("123");
    usuario.setRol("USER");
    usuario.setActivo(true);
    return usuario;
  }
}
