package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import java.util.ArrayList;
import java.util.List;

import com.tallerwebi.dominio.modelo.PerfilUsuario;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.impl.ServicioUsuarioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioUsuarioTest {

  private RepositorioUsuario repositorioUsuario;
  private ServicioUsuarioImpl servicioUsuario;

  private String terminoBusqueda;
  private List<Usuario> resultadoBusqueda;

  private Long idUsuarioActualizar;
  private String nuevaBiografia;
  private String nuevoAvatar;
  private Usuario usuarioActualizado;

  @BeforeEach
  void inicializar() {
    repositorioUsuario = mock(RepositorioUsuario.class);
    servicioUsuario = new ServicioUsuarioImpl(repositorioUsuario);
  }

  // ---------- búsqueda por nombre ----------

  @Test
  void siElTerminoDeBusquedaEstaVacioDevuelveUnaListaVacia() {
    // given
    givenTengoUnTerminoDeBusqueda("");

    // when
    whenBuscoUsuariosPorNombre();

    // then
    thenLaListaDeResultadosEstaVacia();
    verifyNoInteractions(repositorioUsuario);
  }

  @Test
  void siElTerminoDeBusquedaEsNuloODeSoloEspaciosDevuelveUnaListaVacia() {
    // given
    givenTengoUnTerminoDeBusqueda(null);

    // when
    whenBuscoUsuariosPorNombre();

    // then
    thenLaListaDeResultadosEstaVacia();

    // given
    givenTengoUnTerminoDeBusqueda("   ");

    // when
    whenBuscoUsuariosPorNombre();

    // then
    thenLaListaDeResultadosEstaVacia();
    verifyNoInteractions(repositorioUsuario);
  }

  @Test
  void siBuscaUnJugadorExistenteLoEncuentra() {
    // given
    Usuario pepito = givenUnUsuario("pepito");
    List<Usuario> usuarios = givenUnaListaCon(pepito);
    givenTengoUnTerminoDeBusqueda("pepi");
    givenElRepositorioDevuelveUsuarios("pepi", usuarios);

    // when
    whenBuscoUsuariosPorNombre();

    // then
    thenEncuentroAlJugadorEsperado(1, "pepito");
  }

  @Test
  void laBusquedaIgnoraMayusculasYMinusculas() {
    // given
    Usuario sofia = givenUnUsuario("sofia");
    List<Usuario> usuarios = givenUnaListaCon(sofia);
    givenTengoUnTerminoDeBusqueda("Sofi");
    givenElRepositorioDevuelveUsuarios("Sofi", usuarios);

    // when
    whenBuscoUsuariosPorNombre();

    // then
    thenEncuentroAlJugadorEsperado(1, "sofia");
  }

  @Test
  void laBusquedaQuitaLosEspaciosDelTermino() {
    // given
    Usuario ana = givenUnUsuario("ana");
    List<Usuario> usuarios = givenUnaListaCon(ana);
    givenTengoUnTerminoDeBusqueda("  ana  ");
    givenElRepositorioDevuelveUsuarios("ana", usuarios);

    // when
    whenBuscoUsuariosPorNombre();

    // then
    thenEncuentroAlJugadorEsperado(1, "ana");
  }

  @Test
  void siElJugadorNoExisteDevuelveUnaListaVacia() {
    // given
    givenTengoUnTerminoDeBusqueda("jugadorquenoexiste");
    givenElRepositorioDevuelveUsuarios("jugadorquenoexiste", new ArrayList<>());

    // when
    whenBuscoUsuariosPorNombre();

    // then
    thenLaListaDeResultadosEstaVacia();
  }

  // ---------- registrar usuario ----------

  @Test
  void deberiaRegistrarUnUsuarioNuevoConSuPerfil() throws UsuarioExistente {
    // given
    // El repositorio no encuentra ni el username ni el email (devuelve null).

    // when
    Usuario registrado = servicioUsuario.registrarUsuario(
      "ana",
      "ana@test.com",
      "1234",
      "Ana Perez"
    );

    // then
    assertThat(registrado.getUsername(), equalTo("ana"));
    assertThat(registrado.getEmail(), equalTo("ana@test.com"));
    assertThat(registrado.getPassword(), equalTo("1234"));
    assertThat(registrado.getNombreCompleto(), equalTo("Ana Perez"));
    assertThat(registrado.getPerfil(), is(notNullValue()));
    verify(repositorioUsuario).guardar(registrado);
  }

  @Test
  void noDeberiaRegistrarSiElUsernameYaExiste() {
    // given
    when(repositorioUsuario.buscarPorUsername("ana")).thenReturn(givenUnUsuario("ana"));

    // when / then
    assertThrows(
      UsuarioExistente.class,
      () -> servicioUsuario.registrarUsuario("ana", "ana@test.com", "1234", "Ana")
    );
  }

  @Test
  void noDeberiaRegistrarSiElEmailYaExiste() {
    // given
    when(repositorioUsuario.buscarPorUsername("ana")).thenReturn(null);
    when(repositorioUsuario.buscar("ana@test.com")).thenReturn(givenUnUsuario("otra"));

    // when / then
    assertThrows(
      UsuarioExistente.class,
      () -> servicioUsuario.registrarUsuario("ana", "ana@test.com", "1234", "Ana")
    );
  }

  // ---------- autenticación y búsquedas puntuales ----------

  @Test
  void autenticarConUsernameOPasswordNulosDevuelveNull() {
    assertThat(servicioUsuario.autenticarUsuario(null, "1234"), is(nullValue()));
    assertThat(servicioUsuario.autenticarUsuario("ana", null), is(nullValue()));
    verifyNoInteractions(repositorioUsuario);
  }

  @Test
  void autenticarConCredencialesValidasDevuelveElUsuario() {
    // given
    Usuario ana = givenUnUsuario("ana");
    when(repositorioUsuario.buscarPorUsernameYPassword("ana", "1234")).thenReturn(ana);

    // when
    Usuario autenticado = servicioUsuario.autenticarUsuario("ana", "1234");

    // then
    assertThat(autenticado, sameInstance(ana));
  }

  @Test
  void buscarUsuarioPorIdNuloDevuelveNull() {
    assertThat(servicioUsuario.buscarUsuarioPorId(null), is(nullValue()));
    verifyNoInteractions(repositorioUsuario);
  }

  @Test
  void buscarUsuarioPorIdDevuelveElUsuarioDelRepositorio() {
    // given
    Usuario ana = givenUnUsuarioConPerfil(4L);
    when(repositorioUsuario.buscarPorId(4L)).thenReturn(ana);

    // when
    Usuario encontrado = servicioUsuario.buscarUsuarioPorId(4L);

    // then
    assertThat(encontrado, sameInstance(ana));
  }

  @Test
  void buscarUsuarioPorUsernameNuloDevuelveNull() {
    assertThat(servicioUsuario.buscarUsuarioPorUsername(null), is(nullValue()));
    verifyNoInteractions(repositorioUsuario);
  }

  @Test
  void buscarUsuarioPorUsernameDevuelveElUsuarioDelRepositorio() {
    // given
    Usuario ana = givenUnUsuario("ana");
    when(repositorioUsuario.buscarPorUsername("ana")).thenReturn(ana);

    // when
    Usuario encontrado = servicioUsuario.buscarUsuarioPorUsername("ana");

    // then
    assertThat(encontrado, sameInstance(ana));
  }

  // ---------- actualizar perfil ----------

  @Test
  void actualizaLaBiografiaYElAvatarDelPerfil() {
    // given
    Usuario usuario = givenUnUsuarioConPerfil(2L);
    givenDatosParaActualizarElPerfil(2L, "Esta es mi nueva biografía", "fotocualquiera");
    givenElRepositorioDevuelveUsuario(2L, usuario);

    // when
    whenActualizoElPerfil();

    // then
    thenElPerfilSeActualizaCorrectamente();
  }

  @Test
  void siElAvatarEsVacioLoGuardaComoNull() {
    // given
    Usuario usuario = givenUnUsuarioConPerfil(3L);
    givenDatosParaActualizarElPerfil(3L, "Esta es mi nueva biografía", "");
    givenElRepositorioDevuelveUsuario(3L, usuario);

    // when
    whenActualizoElPerfil();

    // then
    thenElAvatarSeGuardaVacio();
  }

  @Test
  void siElAvatarEsNuloOSoloEspaciosLoGuardaComoNull() {
    // given
    Usuario usuario = givenUnUsuarioConPerfil(5L);
    givenDatosParaActualizarElPerfil(5L, "bio", null);
    givenElRepositorioDevuelveUsuario(5L, usuario);

    // when
    whenActualizoElPerfil();

    // then
    thenElAvatarSeGuardaVacio();

    // given
    givenDatosParaActualizarElPerfil(5L, "bio", "   ");

    // when
    whenActualizoElPerfil();

    // then
    thenElAvatarSeGuardaVacio();
  }

  @Test
  void siElUsuarioNoExisteNoActualizaNada() {
    // given
    when(repositorioUsuario.buscarPorId(99L)).thenReturn(null);

    // when
    servicioUsuario.actualizarPerfil(99L, "bio", "avatar");

    // then
    verify(repositorioUsuario).buscarPorId(99L);
  }

  @Test
  void siElUsuarioNoTienePerfilNoActualizaNada() {
    // given
    Usuario sinPerfil = givenUnUsuario("sinperfil");
    sinPerfil.setPerfil(null);
    when(repositorioUsuario.buscarPorId(6L)).thenReturn(sinPerfil);

    // when
    servicioUsuario.actualizarPerfil(6L, "bio", "avatar");

    // then
    assertThat(sinPerfil.getPerfil(), is(nullValue()));
  }

  // ---------- Given: preparación ----------

  private void givenTengoUnTerminoDeBusqueda(String termino) {
    terminoBusqueda = termino;
  }

  private Usuario givenUnUsuario(String username) {
    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    return usuario;
  }

  private Usuario givenUnUsuarioConPerfil(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setUsername("usuario" + id);

    PerfilUsuario perfil = new PerfilUsuario();
    perfil.setUsuario(usuario);
    usuario.setPerfil(perfil);

    return usuario;
  }

  private List<Usuario> givenUnaListaCon(Usuario usuario) {
    List<Usuario> usuarios = new ArrayList<>();
    usuarios.add(usuario);
    return usuarios;
  }

  private void givenElRepositorioDevuelveUsuarios(String termino, List<Usuario> usuarios) {
    when(repositorioUsuario.buscarUsuariosPorNombre(termino)).thenReturn(usuarios);
  }

  private void givenDatosParaActualizarElPerfil(Long id, String biografia, String avatar) {
    idUsuarioActualizar = id;
    nuevaBiografia = biografia;
    nuevoAvatar = avatar;
  }

  private void givenElRepositorioDevuelveUsuario(Long id, Usuario usuario) {
    when(repositorioUsuario.buscarPorId(id)).thenReturn(usuario);
  }

  // ---------- When: ejecución ----------

  private void whenBuscoUsuariosPorNombre() {
    resultadoBusqueda = servicioUsuario.buscarUsuariosPorNombre(terminoBusqueda);
  }

  private void whenActualizoElPerfil() {
    servicioUsuario.actualizarPerfil(idUsuarioActualizar, nuevaBiografia, nuevoAvatar);
    usuarioActualizado = servicioUsuario.buscarUsuarioPorId(idUsuarioActualizar);
  }

  // ---------- Then: validación ----------

  private void thenLaListaDeResultadosEstaVacia() {
    assertTrue(resultadoBusqueda.isEmpty());
  }

  private void thenEncuentroAlJugadorEsperado(int cantidadEsperada, String nombreEsperado) {
    assertThat(resultadoBusqueda.size(), equalTo(cantidadEsperada));
    assertThat(resultadoBusqueda.get(0).getUsername(), equalTo(nombreEsperado));
  }

  private void thenElPerfilSeActualizaCorrectamente() {
    assertThat(usuarioActualizado.getPerfil().getBiografia(), equalTo(nuevaBiografia));
    assertThat(usuarioActualizado.getPerfil().getAvatarUrl(), equalTo(nuevoAvatar));
  }

  private void thenElAvatarSeGuardaVacio() {
    assertThat(usuarioActualizado.getPerfil().getAvatarUrl(), is((String) null));
  }
}
