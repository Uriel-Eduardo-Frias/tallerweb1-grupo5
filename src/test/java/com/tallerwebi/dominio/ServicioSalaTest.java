package com.tallerwebi.dominio;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        assertThrows(SalaLlenaException.class, () -> servicioSala.unirse(sala, invitado));

        assertThat(sala.getJugadores(), hasSize(1));
    }
}
