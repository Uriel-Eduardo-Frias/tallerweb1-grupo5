package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ServicioSalaTest {

  private ServicioSala servicioSala = new ServicioSalaIm();
  private ServicioPartida servicioPartida = new ServicioPartidaImp(servicioSala);

  @Test
  public void deberiaLanzarExcepcionCuandoLaSalaEstaLlena() {
    Usuario host = new Usuario();
    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);
    Usuario invitado = new Usuario();

    invitado.setUsername("Ana");
    host.setUsername("Juan");

    sala.setMaxJugadores(1);
    sala.agregarJugador(host);

    assertThrows(SalaLlenaException.class, () -> servicioSala.unirse(sala, invitado));

    assertThat(sala.getJugadores(), hasSize(1));
  }

  /*
  @Test
  public void deberiaCrearUnaSala() {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Sala sala = servicioSala.crearSala("TRV-1234", "Trivia del viernes", host);

    assertThat(sala, notNullValue());
    assertThat(sala.getCodigo(), is("TRV-1234"));
    assertThat(sala.getNombre(), is("Trivia del viernes"));
    assertThat(sala.getHost(), is(host));
  }
*/

  @Test
  public void deberiaQuitarAlIntegranteCuandoSaleDeLaSala() {
    // Given
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Usuario integrante = new Usuario();
    integrante.setUsername("Ana");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);

    sala.agregarJugador(host);

    sala.agregarJugador(integrante);

    // When
    servicioSala.salir(sala, integrante);

    // Then
    assertThat(sala.getJugadores(), hasSize(1));

    assertThat(sala.getJugadores(), not(hasItem(hasProperty("username", equalTo("Ana")))));

    assertThat(sala.getHost(), equalTo(host));
  }

  @Test
  public void deberiaLanzarExcepcionSiElUsuarioNoPerteneceALaSala() {
    // Given
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Usuario usuarioAjeno = new Usuario();
    usuarioAjeno.setUsername("Pedro");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);

    sala.agregarJugador(host);

    assertThrows(
      UsuarioNoPerteneceASalaException.class,
      () -> servicioSala.salir(sala, usuarioAjeno)
    );

    assertThat(sala.getJugadores(), hasSize(1));
    assertThat(sala.getJugadores(), hasItem(host));
    assertThat(sala.getHost(), equalTo(host));
  }

  @Test
  public void deberiaReasignarElHostCuandoElHostSale() {
    // Given
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Usuario siguienteHost = new Usuario();

    siguienteHost.setUsername("Ana");

    Usuario usuarioAjeno = new Usuario();
    usuarioAjeno.setUsername("Pedro");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);

    sala.agregarJugador(host);

    sala.agregarJugador(siguienteHost);

    sala.agregarJugador(usuarioAjeno);

    // When
    servicioSala.salir(sala, host);

    // Then
    assertThat(sala.getJugadores(), not(hasItem(host)));
    assertThat(sala.getHost(), equalTo(siguienteHost));
  }

  @Test
  public void deberiaDejarLaSalaSinHostCuandoSaleElUltimoIntegrante() {
    // Given
    Usuario host = new Usuario();

    host.setUsername("Juan");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);

    sala.agregarJugador(host);

    // When
    servicioSala.salir(sala, host);

    // Then
    assertThat(sala.getJugadores(), empty());
    assertThat(sala.getHost(), nullValue());
  }

  /*
  @Test
  public void deberiaCrearUnaSalaConElHostComoPrimerJugador() {
    // Given
    Usuario host = new Usuario();
    host.setUsername("Juan");

    // When
    Sala sala = servicioSala.crearSala("TRV-1234", "Trivia del viernes", host);

    // Then
    assertThat(sala, notNullValue());
    assertThat(sala.getCodigo(), equalTo("TRV-1234"));
    assertThat(sala.getNombre(), equalTo("Trivia del viernes"));
    assertThat(sala.getHost(), equalTo(host));
    assertThat(sala.getJugadores(), hasSize(1));
    assertThat(sala.getJugadores(), hasItem(host));
  }
*/

  @Test
  public void deberiaCambiarElHostCuandoElHostActualLoSolicita() {
    // Given
    Usuario hostActual = new Usuario();

    hostActual.setUsername("Juan");

    Usuario nuevoHost = new Usuario();
    nuevoHost.setUsername("Ana");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", hostActual);
    sala.agregarJugador(hostActual);
    sala.agregarJugador(nuevoHost);

    // When
    servicioSala.cambiarHost(sala, hostActual, nuevoHost);

    // Then
    assertThat(sala.getHost(), equalTo(nuevoHost));
    assertThat(sala.getJugadores(), hasItem(hostActual));
    assertThat(sala.getJugadores(), hasItem(nuevoHost));
  }

  @Test
  public void deberiaRechazarElCambioSiElSolicitanteNoEsElHost() {
    // Given
    Usuario hostActual = new Usuario();
    Usuario integrante = new Usuario();
    Usuario nuevoHost = new Usuario();

    hostActual.setUsername("Juan");
    integrante.setUsername("Ana");
    nuevoHost.setUsername("Pedro");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", hostActual);
    sala.agregarJugador(hostActual);
    sala.agregarJugador(integrante);
    sala.agregarJugador(nuevoHost);

    assertThrows(
      UsuarioNoEsHostException.class,
      () -> servicioSala.cambiarHost(sala, integrante, nuevoHost)
    );
  }

  @Test
  public void deberiaRechazarElCambioSiElNuevoHostNoPerteneceALaSala() {
    // Given
    Usuario hostActual = new Usuario();
    hostActual.setUsername("Juan");

    Usuario usuarioAjeno = new Usuario();
    usuarioAjeno.setUsername("Pedro");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", hostActual);
    sala.agregarJugador(hostActual);

    assertThrows(
      UsuarioNoPerteneceASalaException.class,
      () -> servicioSala.cambiarHost(sala, hostActual, usuarioAjeno)
    );

    assertThat(sala.getHost(), equalTo(hostActual));
  }

  /*
  @Test
  public void deberiaCrearUnaPartidaAsociadaALaSalaConEstadoInicial() {
    // Given
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Usuario integrante = new Usuario();
    integrante.setUsername("Ana");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);
    sala.agregarJugador(host);
    sala.agregarJugador(integrante);

    // When
    Partida partida = servicioPartida.iniciarPartida("TRV-1234", host);

    // Then
    assertThat(partida, notNullValue());
    assertThat(partida.getSala(), equalTo(sala));
    assertThat(partida.getEstado(), equalTo(EstadoPartida.INICIADA));
  }
  */

  @Test
  public void deberiaCrearUnaPartidaAsociadaALaSalaConEstadoInicial() {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Usuario integrante = new Usuario();
    integrante.setUsername("Ana");

    Sala sala = servicioSala.crearSala("Trivia del viernes", host);
    servicioSala.unirse(sala, integrante);

    Partida partida = servicioPartida.iniciarPartida(sala.getCodigo(), host);

    assertThat(partida, notNullValue());
    assertThat(partida.getSala(), equalTo(sala));
    assertThat(partida.getEstado(), equalTo(EstadoPartida.INICIADA));
  }
}
