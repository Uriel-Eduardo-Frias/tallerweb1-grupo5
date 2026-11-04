package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.tallerwebi.dominio.enumeration.TipoLogro;
import com.tallerwebi.dominio.modelo.ProgresoUsuario;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.modelo.UsuarioLogro;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HibernateInfraestructuraTestConfig.class)
public class RepositorioLogroTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioLogroImpl repositorioLogro;

  @BeforeEach
  void init() {
    this.repositorioLogro = new RepositorioLogroImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  void deberiaGuardarUnLogroYObtenerloPorElIdDelUsuario() {
    // given
    Usuario usuario = givenUnUsuarioPersistido("juan_logros");
    UsuarioLogro logro = givenUnLogroParaElUsuario(usuario);

    // when
    repositorioLogro.guardarLogro(logro);
    List<UsuarioLogro> logrosObtenidos = repositorioLogro.obtenerLogroPorUsuario(usuario.getId());

    // then
    assertThat(logrosObtenidos, notNullValue());
    assertThat(logrosObtenidos, hasSize(1));
    assertThat(logrosObtenidos.get(0).getUsuario().getId(), equalTo(usuario.getId()));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverUnaListaVaciaSiElUsuarioNoTieneLogros() {
    // given
    Usuario usuario = givenUnUsuarioPersistido("pedro_sin_logros");

    // when
    List<UsuarioLogro> logrosObtenidos = repositorioLogro.obtenerLogroPorUsuario(usuario.getId());

    // then
    assertThat(logrosObtenidos, notNullValue());
    assertThat(logrosObtenidos, hasSize(0));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaGuardarElProgresoYObtenerloPorElIdDelUsuario() {
    // given
    Usuario usuario = givenUnUsuarioPersistido("maria_progreso");
    ProgresoUsuario progreso = givenUnProgresoParaElUsuario(usuario);

    // when
    repositorioLogro.guardarProgreso(progreso);
    ProgresoUsuario progresoObtenido = repositorioLogro.obtenerProgreso(usuario.getId());

    // then
    assertThat(progresoObtenido, notNullValue());
    assertThat(progresoObtenido.getUsuario().getId(), equalTo(usuario.getId()));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverNullSiElUsuarioNoTieneProgresoRegistrado() {
    // given
    Usuario usuario = givenUnUsuarioPersistido("ana_sin_progreso");

    // when
    ProgresoUsuario progresoObtenido = repositorioLogro.obtenerProgreso(usuario.getId());

    // then
    assertThat(progresoObtenido, nullValue());
  }

  private Usuario givenUnUsuarioPersistido(String username) {
    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    usuario.setEmail(username + "@test.com");
    usuario.setPassword("1234");
    usuario.setRol("USER");
    usuario.setActivo(true);

    sessionFactory.getCurrentSession().persist(usuario);
    return usuario;
  }

  private UsuarioLogro givenUnLogroParaElUsuario(Usuario usuario) {
    UsuarioLogro logro = new UsuarioLogro();
    logro.setUsuario(usuario);
    logro.setFechaDesbloqueo(java.time.LocalDateTime.now());

    logro.setTipoLogro(TipoLogro.CAMPEON_DEBUTANTE);

    return logro;
  }

  private ProgresoUsuario givenUnProgresoParaElUsuario(Usuario usuario) {
    ProgresoUsuario progreso = new ProgresoUsuario();
    progreso.setUsuario(usuario);
    return progreso;
  }
}
