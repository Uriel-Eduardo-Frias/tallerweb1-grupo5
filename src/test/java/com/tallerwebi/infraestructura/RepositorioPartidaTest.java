package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.enums.EstadoPartida;
import com.tallerwebi.dominio.enums.EstadoSala;
import com.tallerwebi.dominio.enums.ModoJuego;
import com.tallerwebi.dominio.enums.TipoComodin;
import com.tallerwebi.dominio.modelo.Comodin;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HibernateInfraestructuraTestConfig.class)
@Transactional
public class RepositorioPartidaTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioPartidaImpl repositorioPartida;
  private Comodin comodinGuardado;

  @BeforeEach
  void inicializar() {
    repositorioPartida = new RepositorioPartidaImpl();
    ReflectionTestUtils.setField(repositorioPartida, "sessionFactory", sessionFactory);

    comodinGuardado = givenUnComodinEnLaBaseDeDatos(TipoComodin.DOBLE_CHANCE, "Doble Chance");
  }

  @Test
  void deberiaGuardarYBuscarPartidaPorId() {
    Partida partida = givenUnaPartidaPersistida("TRV-2001");

    Partida encontrada = repositorioPartida.obtenerPorId(partida.getId());

    assertThat(encontrada.getId(), equalTo(partida.getId()));
    assertThat(encontrada.getEstado(), equalTo(EstadoPartida.EN_CURSO));
  }

  @Test
  void deberiaDevolverNullSiNoEncuentraPartidaPorId() {
    Partida partida = repositorioPartida.obtenerPorId(-1L);

    assertThat(partida, nullValue());
  }

  @Test
  void deberiaBuscarPartidaPorCodigoDeSala() {
    Partida partida = givenUnaPartidaPersistida("TRV-2002");

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Partida encontrada = repositorioPartida.obtenerPorCodigoSala("TRV-2002");

    assertThat(encontrada.getId(), equalTo(partida.getId()));
    assertThat(encontrada.getSala().getCodigo(), equalTo("TRV-2002"));
  }

  @Test
  void deberiaDevolverNullSiNoEncuentraPartidaPorCodigoDeSala() {
    Partida partida = repositorioPartida.obtenerPorCodigoSala("NO-EXISTE");

    assertThat(partida, nullValue());
  }

  @Test
  void cuandoListarComodinesEntoncesRetornaTodosLosComodines() {
    List<Comodin> comodines = repositorioPartida.listarComodines();

    assertThat(comodines, hasItem(comodinGuardado));
  }

  @Test
  void dadoUnCodigoExistenteCuandoBuscoComodinPorCodigoEntoncesRetornaElComodinCorrecto() {
    Comodin comodinEncontrado = repositorioPartida.buscarComodinPorCodigo(TipoComodin.DOBLE_CHANCE);

    assertThat(comodinEncontrado, notNullValue());
    assertThat(comodinEncontrado.getCodigo(), equalTo(TipoComodin.DOBLE_CHANCE));
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
    partida.setFechaInicio(LocalDateTime.now());

    repositorioPartida.guardar(partida);
    sessionFactory.getCurrentSession().flush();

    return partida;
  }

  private Comodin givenUnComodinEnLaBaseDeDatos(TipoComodin tipo, String nombre) {
    Comodin comodin = new Comodin(tipo, nombre, "Descripción de prueba", "icono.png");
    sessionFactory.getCurrentSession().persist(comodin);
    return comodin;
  }
}
