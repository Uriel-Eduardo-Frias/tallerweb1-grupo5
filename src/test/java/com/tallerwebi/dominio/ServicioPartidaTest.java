package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ServicioPartidaTest {
  /*
  private ServicioSala servicioSala = new ServicioSalaIm();
  private ServicioPartida servicioPartida = new ServicioPartidaImp(servicioSala);
  */
  /*
  @Test
  public void deberiaLanzarExcepcionSiQuienIniciaLaPartidaNoEsHost() {
    Usuario host = new Usuario();
    host.setUsername("Juan");
    Usuario otro = new Usuario();
    otro.setUsername("Ana");

    Sala sala = servicioSala.crearSala("Trivia del viernes", host);
    servicioSala.unirse(sala, otro);

    assertThrows(
      UsuarioNoEsHostException.class,
      () -> servicioPartida.iniciarPartida(sala.getCodigo(), otro)
    );
  }

  @Test
  public void deberiaLanzarExcepcionSiLaSalaDeLaPartidaNoExiste() {
    assertThrows(
      SalaNoEncontradaException.class,
      () -> servicioPartida.iniciarPartida("TRV-XXXX", new Usuario())
    );
  }

  @Test
  public void deberiaDevolverNullPorAhoraEnFinalizarPartidaSinImplementar() {
    assertThat(servicioPartida.finalizarPartida(), nullValue());
  }

  @Test
  public void deberiaEncontrarLaPartidaIniciadaPorCodigoDeSala() {
    Usuario host = new Usuario();
    host.setUsername("Juan");
    Sala sala = servicioSala.crearSala("Trivia del viernes", host);

    Partida iniciada = servicioPartida.iniciarPartida(sala.getCodigo(), host);

    assertThat(servicioPartida.buscarPartidaPorCodigoSala(sala.getCodigo()), equalTo(iniciada));
  }
  */
}
