package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
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
  void siElJugadorNoExisteDevuelveUnaListaVacia() {
    // given
    givenTengoUnTerminoDeBusqueda("jugadorquenoexiste");
    givenElRepositorioDevuelveUsuarios("jugadorquenoexiste", new ArrayList<>());

    // when
    whenBuscoUsuariosPorNombre();

    // then
    thenLaListaDeResultadosEstaVacia();
  }

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

  // Given: preparación

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

  // When: ejecución

  private void whenBuscoUsuariosPorNombre() {
    resultadoBusqueda = servicioUsuario.buscarUsuariosPorNombre(terminoBusqueda);
  }

  private void whenActualizoElPerfil() {
    servicioUsuario.actualizarPerfil(idUsuarioActualizar, nuevaBiografia, nuevoAvatar);
    usuarioActualizado = servicioUsuario.buscarUsuarioPorId(idUsuarioActualizar);
  }

  // Then: validación

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
