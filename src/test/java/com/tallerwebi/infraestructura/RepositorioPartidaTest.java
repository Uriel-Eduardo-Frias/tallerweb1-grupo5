package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

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
public class RepositorioPartidaTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioPartidaImpl repositorioPartida;

  @BeforeEach
  void inicializar() {
    repositorioPartida = new RepositorioPartidaImpl();

    ReflectionTestUtils.setField(repositorioPartida, "sessionFactory", sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  void deberiaGuardarYBuscarPartidaPorId() {
    // given
    Partida partida = givenUnaPartidaPersistida("TRV-2001");

    // when
    Partida encontrada = repositorioPartida.obtenerPorId(partida.getId());

    // then
    assertThat(encontrada.getId(), equalTo(partida.getId()));
    assertThat(encontrada.getEstado(), equalTo(EstadoPartida.EN_CURSO));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverNullSiNoEncuentraPartidaPorId() {
    // when
    Partida partida = repositorioPartida.obtenerPorId(-1L);

    // then
    assertThat(partida, nullValue());
  }

  @Test
  @Transactional
  @Rollback
  void deberiaBuscarPartidaPorCodigoDeSala() {
    // given
    Partida partida = givenUnaPartidaPersistida("TRV-2002");

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    // when
    Partida encontrada = repositorioPartida.obtenerPorCodigoSala("TRV-2002");

    // then
    assertThat(encontrada.getId(), equalTo(partida.getId()));
    assertThat(encontrada.getSala().getCodigo(), equalTo("TRV-2002"));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverNullSiNoEncuentraPartidaPorCodigoDeSala() {
    // when
    Partida partida = repositorioPartida.obtenerPorCodigoSala("NO-EXISTE");

    // then
    assertThat(partida, nullValue());
  }

  private Partida givenUnaPartidaPersistida(String codigoSala) {
    Usuario host = new Usuario();
    host.setUsername("host-" + codigoSala);
    host.setEmail(codigoSala + "@test.com");
    host.setPassword("123");
    host.setRol("USER");
    host.setActivo(true);

    sessionFactory.getCurrentSession().persist(host);

    Sala sala = new Sala(codigoSala, "Sala de prueba", host);
    sala.setEstado(EstadoSala.EN_CURSO);
    sala.setModoJuego(ModoJuego.TURNO_TODOS);

    sessionFactory.getCurrentSession().persist(sala);

    Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
    repositorioPartida.guardar(partida);

    return partida;
  }
}
