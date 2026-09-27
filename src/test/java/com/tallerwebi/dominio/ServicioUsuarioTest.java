package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioUsuarioTest {

  private ServicioUsuarioImpl servicioUsuario;
  private String busqueda;
  private List<Usuario> resultadoBusqueda;

  private Long idUsuarioActualizar;
  private String nuevaBiografia;
  private String nuevoAvatar;
  private Usuario usuarioActualizado;

  @BeforeEach
  public void init() {
    servicioUsuario = new ServicioUsuarioImpl();
  }

  @Test
  public void queSiElUsuarioDejaVacioElCampoBuscarDevuelvaUnaListaVacia() {
    // preparacion given)
    givenTengoUnTerminoDeBusquedaVacio();

    // ejecucion (when)
    whenBuscoUsuariosPorNombre();

    // verificacion (then)
    thenLaListaDeResultadosEstaVacia();
  }

  @Test
  public void queElUsuarioBuscaUnJugadorExistenteLoPuedaEncontrar() {
    // prepacion
    givenTengoUnTerminoDeBusquedaValido("pepi");

    // ejecucion
    whenBuscoUsuariosPorNombre();

    // validacion
    thenEncuentroAlJugadorEsperado(1, "pepito");
  }

  @Test
  public void queSiElUsuarioIngresaMayusculaYMinusculaPuedaEncontrarAlJugador() {
    // preparacion
    givenTengoUnTerminoDeBusquedaValido("Sofi");

    // ejecucion
    whenBuscoUsuariosPorNombre();

    // validacion
    thenEncuentroAlJugadorEsperado(1, "sofia");
  }

  @Test
  public void queSiElUsuarioIngresaUnJugadorQueNoExisteDebeDevolverUnaListaVacia() {
    // preparacion
    givenTengoUnTerminoDeBusquedaValido("jugadorquenoexiste");

    // ejecucion
    whenBuscoUsuariosPorNombre();

    // validacion
    thenLaListaDeResultadosEstaVacia();
  }

  @Test
  public void queSePuedaActualizarLaBiografiaYElAvatarDeUnUsuario() {
    // preparacion
    givenDatosParaActualizarElPerfil(2L, "Esta es mi nueva biografia", "fotocualquiera");

    // ejecucion
    whenActualizoElPerfil();

    // validacion
    thenElPerfilSeActualizaCorrectamente();
  }

  @Test
  public void siElUsuarioActualizaSuPerfilConUnAvatarVacioSeMuestrePorDefectoUnaFotoCualquiera() {
    // preparacion
    givenDatosParaActualizarElPerfil(3L, "Esta es mi nueva biografia", "");

    // ejecucion
    whenActualizoElPerfil();

    // validacion
    thenElAvatarSeGuardaVacio();
  }

  // given
  private void givenTengoUnTerminoDeBusquedaValido(String busqueda) {
    this.busqueda = busqueda;
  }

  private void givenTengoUnTerminoDeBusquedaVacio() {
    this.busqueda = "";
  }

  private void givenDatosParaActualizarElPerfil(Long id, String biografia, String avatar) {
    this.idUsuarioActualizar = id;
    this.nuevaBiografia = biografia;
    this.nuevoAvatar = avatar;
  }

  // when
  private void whenBuscoUsuariosPorNombre() {
    this.resultadoBusqueda = this.servicioUsuario.buscarUsuariosPorNombre(busqueda);
  }

  private void whenActualizoElPerfil() {
    this.servicioUsuario.actualizarPerfil(idUsuarioActualizar, nuevaBiografia, nuevoAvatar);
    this.usuarioActualizado = servicioUsuario.buscarUsuarioPorId(idUsuarioActualizar);
  }

  // then
  private void thenLaListaDeResultadosEstaVacia() {
    assertTrue(this.resultadoBusqueda.isEmpty());
  }

  private void thenEncuentroAlJugadorEsperado(int cantidadEsperada, String nombreEsperado) {
    assertThat(this.resultadoBusqueda.size(), equalTo(cantidadEsperada));
    assertThat(this.resultadoBusqueda.get(0).getUsername(), equalTo(nombreEsperado));
  }

  private void thenElPerfilSeActualizaCorrectamente() {
    assertThat(this.usuarioActualizado.getPerfil().getBiografia(), equalTo(this.nuevaBiografia));
    assertThat(this.usuarioActualizado.getPerfil().getAvatarUrl(), equalTo(this.nuevoAvatar));
  }

  private void thenElAvatarSeGuardaVacio() {
    assertThat(this.usuarioActualizado.getPerfil().getAvatarUrl(), equalTo(null));
  }
}
