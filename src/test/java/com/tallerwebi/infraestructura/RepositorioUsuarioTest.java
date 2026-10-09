package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
class RepositorioUsuarioTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioUsuario repositorioUsuario;

  @BeforeEach
  void inicializar() {
    repositorioUsuario = new RepositorioUsuarioImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  void deberiaGuardarUnNuevoUsuario() {
    // given
    Usuario usuario = givenUnUsuario("nuevo.usuario@test.com", "nuevoUsuario", "1234", "USER");

    // when
    repositorioUsuario.guardar(usuario);
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Usuario obtenido = repositorioUsuario.buscar(usuario.getEmail());

    // then
    thenElUsuarioEsCorrecto(obtenido, usuario);
  }

  @Test
  @Transactional
  @Rollback
  void deberiaBuscarUsuarioPorEmailYPassword() {
    // given
    Usuario esperado = givenUnUsuarioPersistido(
      "email.password@test.com",
      "jugadorEmail",
      "123",
      "USER"
    );

    // when
    Usuario obtenido = repositorioUsuario.buscarUsuario(esperado.getEmail(), "123");

    // then
    thenElUsuarioEsCorrecto(obtenido, esperado);
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverNullSiEmailYPasswordNoCoinciden() {
    // given
    givenUnUsuarioPersistido("email.incorrecto@test.com", "jugadorEmail", "123", "USER");

    // when
    Usuario obtenido = repositorioUsuario.buscarUsuario("email.incorrecto@test.com", "otra");

    // then
    assertThat(obtenido, is(nullValue()));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaBuscarUsuarioPorEmail() {
    // given
    Usuario esperado = givenUnUsuarioPersistido(
      "buscar.email@test.com",
      "jugadorEmail",
      "123",
      "USER"
    );

    // when
    Usuario obtenido = repositorioUsuario.buscar(esperado.getEmail());

    // then
    thenElUsuarioEsCorrecto(obtenido, esperado);
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverNullSiNoEncuentraUsuarioPorEmail() {
    // when
    Usuario obtenido = repositorioUsuario.buscar("inexistente@test.com");

    // then
    assertThat(obtenido, is(nullValue()));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaBuscarUsuarioPorId() {
    // given
    Usuario esperado = givenUnUsuarioPersistido(
      "buscar.id@test.com",
      "jugadorPorId",
      "123",
      "USER"
    );

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    // when
    Usuario obtenido = repositorioUsuario.buscarPorId(esperado.getId());

    // then
    thenElUsuarioEsCorrecto(obtenido, esperado);
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverNullSiNoEncuentraUsuarioPorId() {
    // when
    Usuario obtenido = repositorioUsuario.buscarPorId(-1L);

    // then
    assertThat(obtenido, is(nullValue()));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaBuscarUsuarioPorUsername() {
    // given
    Usuario esperado = givenUnUsuarioPersistido(
      "buscar.username@test.com",
      "jugadorPorUsername",
      "123",
      "USER"
    );

    // when
    Usuario obtenido = repositorioUsuario.buscarPorUsername("jugadorPorUsername");

    // then
    thenElUsuarioEsCorrecto(obtenido, esperado);
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverNullSiNoEncuentraUsuarioPorUsername() {
    // when
    Usuario obtenido = repositorioUsuario.buscarPorUsername("noExiste");

    // then
    assertThat(obtenido, is(nullValue()));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaBuscarUsuarioPorUsernameYPassword() {
    // given
    Usuario esperado = givenUnUsuarioPersistido(
      "buscar.login@test.com",
      "jugadorLogin",
      "clave123",
      "USER"
    );

    // when
    Usuario obtenido = repositorioUsuario.buscarPorUsernameYPassword("jugadorLogin", "clave123");

    // then
    thenElUsuarioEsCorrecto(obtenido, esperado);
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverNullSiUsernameOPasswordNoCoinciden() {
    // given
    givenUnUsuarioPersistido("login.incorrecto@test.com", "jugadorLogin", "clave123", "USER");

    // when
    Usuario obtenido = repositorioUsuario.buscarPorUsernameYPassword("jugadorLogin", "incorrecta");

    // then
    assertThat(obtenido, is(nullValue()));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaBuscarUsuariosPorCoincidenciaParcialIgnorandoMayusculas() {
    // given
    Usuario sofia = givenUnUsuarioPersistido("sofia@test.com", "JugadorSofia", "123", "USER");

    givenUnUsuarioPersistido("juan@test.com", "JugadorJuan", "123", "USER");

    // when
    List<Usuario> obtenidos = repositorioUsuario.buscarUsuariosPorNombre("sofia");

    // then
    assertThat(obtenidos, hasSize(1));
    assertThat(obtenidos.get(0).getId(), equalTo(sofia.getId()));
    assertThat(obtenidos.get(0).getUsername(), equalTo("JugadorSofia"));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaDevolverListaVaciaSiNoHayCoincidenciasPorUsername() {
    // given
    givenUnUsuarioPersistido("otro.jugador@test.com", "JugadorJuan", "123", "USER");

    // when
    List<Usuario> obtenidos = repositorioUsuario.buscarUsuariosPorNombre("noExiste");

    // then
    assertThat(obtenidos, hasSize(0));
  }

  @Test
  @Transactional
  @Rollback
  void deberiaModificarUnUsuarioExistente() {
    // given
    Usuario usuario = givenUnUsuarioPersistido(
      "modificar@test.com",
      "jugadorModificar",
      "123",
      "USER"
    );

    usuario.setPassword("4567");
    usuario.setActivo(true);
    usuario.setRol("ADMIN");

    // when
    repositorioUsuario.modificar(usuario);

    // then
    Usuario obtenido = repositorioUsuario.buscar(usuario.getEmail());
    thenElUsuarioEsCorrecto(obtenido, usuario);
  }

  @Test
  @Transactional
  @Rollback
  void deberiaLanzarExcepcionAlModificarUsuarioInexistente() {
    // given
    Usuario usuario = givenUnUsuario("noexiste@test.com", "usuarioInexistente", "123", "USER");

    // when / then
    assertThrows(UsuarioNoEncontrado.class, () -> repositorioUsuario.modificar(usuario));
  }

  // Helpers Given

  private Usuario givenUnUsuarioPersistido(
    String email,
    String username,
    String password,
    String rol
  ) {
    Usuario usuario = givenUnUsuario(email, username, password, rol);
    sessionFactory.getCurrentSession().persist(usuario);
    return usuario;
  }

  private Usuario givenUnUsuario(String email, String username, String password, String rol) {
    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setUsername(username);
    usuario.setPassword(password);
    usuario.setRol(rol);
    usuario.setActivo(false);
    return usuario;
  }

  // Helper Then

  private void thenElUsuarioEsCorrecto(Usuario obtenido, Usuario esperado) {
    assertThat(obtenido.getId(), equalTo(esperado.getId()));
    assertThat(obtenido.getEmail(), equalTo(esperado.getEmail()));
    assertThat(obtenido.getUsername(), equalTo(esperado.getUsername()));
    assertThat(obtenido.getPassword(), equalTo(esperado.getPassword()));
    assertThat(obtenido.getActivo(), equalTo(esperado.getActivo()));
    assertThat(obtenido.getRol(), equalTo(esperado.getRol()));
  }
}
