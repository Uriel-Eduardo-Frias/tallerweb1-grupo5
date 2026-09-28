package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ServicioSalaTest {

  private ServicioSala servicioSala = new ServicioSalaIm();

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
}
