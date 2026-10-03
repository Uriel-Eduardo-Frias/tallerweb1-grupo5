package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicioSalaTest {

  private AlmacenEnMemoria almacen;
  private ServicioSalaIm servicioSala;

  @BeforeEach
  void limpiarAlmacen() {
    almacen = new AlmacenEnMemoria();
    servicioSala = new ServicioSalaIm(almacen);
  }

  @Test
  public void deberiaLanzarExcepcionCuandoLaSalaAlcanzaSuCapacidadMaxima() {
    Usuario host = new Usuario();
    host.setId(1L);
    host.setUsername("Juan");

    Usuario invitado = new Usuario();
    invitado.setId(2L);
    invitado.setUsername("Ana");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);
    sala.setEstado(EstadoSala.EN_ESPERA);
    sala.setMaxJugadores(1);

    SalaJugador participacionHost = new SalaJugador(sala, EstadoJugador.CONECTADO, true, host);

    sala.agregarJugador(participacionHost);

    almacen.getUsuarios().put(1L, host);
    almacen.getUsuarios().put(2L, invitado);
    almacen.getSalas().put(sala.getCodigo(), sala);

    almacen.getUsuarios().put(host.getId(), host);
    almacen.getUsuarios().put(invitado.getId(), invitado);
    almacen.getSalas().put(sala.getCodigo(), sala);

    assertThrows(SalaLlenaException.class, () -> servicioSala.unirse("TRV-1234", 2L));

    assertThat(sala.getJugadores(), hasSize(1));
  }

  @Test
  public void deberiaLanzarExcepcionSiElUsuarioNoPerteneceALaSala() {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Usuario usuarioAjeno = new Usuario();
    usuarioAjeno.setUsername("Pedro");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);

    SalaJugador participacionHost = new SalaJugador(sala, EstadoJugador.CONECTADO, true, host);
    sala.agregarJugador(participacionHost);

    assertThrows(
      UsuarioNoPerteneceASalaException.class,
      () -> servicioSala.salir(sala, usuarioAjeno)
    );

    assertThat(sala.getJugadores(), hasSize(1));
    assertThat(sala.getJugadores(), hasItem(participacionHost));
    assertThat(sala.getHost(), equalTo(host));
  }

  @Test
  public void deberiaReasignarElAnfitrionCuandoElAnfitrionSale() {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Usuario siguienteAnfitrion = new Usuario();
    siguienteAnfitrion.setUsername("Ana");

    Usuario otroIntegrante = new Usuario();
    otroIntegrante.setUsername("Pedro");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);

    SalaJugador participacionHost = new SalaJugador(sala, EstadoJugador.CONECTADO, true, host);
    SalaJugador participacionAna = new SalaJugador(
      sala,
      EstadoJugador.CONECTADO,
      false,
      siguienteAnfitrion
    );
    SalaJugador participacionPedro = new SalaJugador(
      sala,
      EstadoJugador.CONECTADO,
      false,
      otroIntegrante
    );

    sala.agregarJugador(participacionHost);
    sala.agregarJugador(participacionAna);
    sala.agregarJugador(participacionPedro);

    servicioSala.salir(sala, host);

    assertThat(sala.getJugadores(), hasSize(2));
    assertThat(sala.getJugadores(), not(hasItem(participacionHost)));
    assertThat(sala.getHost(), equalTo(siguienteAnfitrion));
    assertThat(participacionAna.isEsAnfitrion(), is(true));
  }

  @Test
  public void deberiaDejarLaSalaSinHostCuandoSaleElUltimoIntegrante() {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);

    SalaJugador participacionHost = new SalaJugador(sala, EstadoJugador.CONECTADO, true, host);
    sala.agregarJugador(participacionHost);

    servicioSala.salir(sala, host);

    assertThat(sala.getJugadores(), empty());
    assertThat(sala.getHost(), nullValue());
  }

  @Test
  public void deberiaEncontrarUnaSalaPorSuCodigo() {
    Usuario host = new Usuario();
    Sala creada = servicioSala.crearSala("Trivia del viernes", host);

    assertThat(servicioSala.buscarPorCodigo(creada.getCodigo()), equalTo(creada));
  }

  @Test
  public void deberiaLanzarExcepcionSiLaSalaNoExiste() {
    assertThrows(SalaNoEncontradaException.class, () -> servicioSala.buscarPorCodigo("TRV-XXXX"));
  }

  @Test
  public void deberiaLanzarExcepcionAlAgregarUnJugadorNulo() {
    Sala sala = new Sala("TRV-1234", "Trivia", new Usuario());

    assertThrows(JugadorInexistenteExeption.class, () -> sala.agregarJugador(null));
  }

  @Test
  public void deberiaPasarAEnCursoAlIniciarUnaSalaEnEspera() {
    Sala sala = new Sala("TRV-1234", "Trivia", new Usuario());

    sala.iniciar();

    assertThat(sala.getEstado(), equalTo(EstadoSala.EN_CURSO));
  }

  @Test
  public void deberiaLanzarExcepcionAlIniciarUnaSalaQueYaNoEstaEnEspera() {
    Sala sala = new Sala("TRV-1234", "Trivia", new Usuario());
    sala.iniciar();

    assertThrows(IllegalStateException.class, sala::iniciar);
  }

  @Test
  public void deberiaPermitirModificarLosDatosDeLaSala() {
    Usuario hostInicial = new Usuario();
    hostInicial.setUsername("Juan");

    Sala sala = new Sala("TRV-1234", "Trivia", hostInicial);

    Usuario nuevoHost = new Usuario();
    nuevoHost.setUsername("Ana");

    sala.setCodigo("TRV-9999");
    sala.setNombre("Otra trivia");
    sala.setHost(nuevoHost);

    SalaJugador participacionHost = new SalaJugador(sala, EstadoJugador.CONECTADO, true, nuevoHost);

    List<SalaJugador> jugadores = new ArrayList<>();
    jugadores.add(participacionHost);
    sala.setJugadores(jugadores);

    assertThat(sala.getCodigo(), equalTo("TRV-9999"));
    assertThat(sala.getNombre(), equalTo("Otra trivia"));
    assertThat(sala.getHost(), equalTo(nuevoHost));
    assertThat(sala.getJugadores(), hasSize(1));
    assertThat(sala.getJugadores().get(0).getUsuario(), equalTo(nuevoHost));
  }
}
