package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPartidaTest {

  private ServicioPartida servicioPartida;
  private ServicioSala servicioSala;

  private Sala sala;
  private Usuario host;

  @BeforeEach
  public void inicializar() {
    servicioSala = mock(ServicioSala.class);

    servicioPartida = new ServicioPartidaImp(servicioSala);

    host = new Usuario();
    host.setUsername("Juan");

    sala = new Sala();
    sala.setCodigo("ABC123");
    sala.setNombre("Trivia del viernes");
    sala.setHost(host);
    sala.setEstado(EstadoSala.EN_ESPERA);
    sala.setJugadores(new ArrayList<>());

    SalaJugador jugador = new SalaJugador();
    jugador.setUsuario(host);

    sala.getJugadores().add(jugador);

    when(servicioSala.buscarPorCodigo("ABC123")).thenReturn(sala);
  }

  @Test
  public void dadoQueHayUnaSalaEnEsperaCuandoInicioLaPartidaObtengoPartidaEnCurso() {
    Partida partida = servicioPartida.iniciarPartida("ABC123", host);

    assertThat(partida.getEstado(), equalTo(EstadoPartida.EN_CURSO));

    assertThat(sala.getEstado(), equalTo(EstadoSala.EN_CURSO));
  }

  @Test
  public void dadoQueHayUnaPartidaEnCursoCuandoLaFinalizoObtengoPartidaFinalizada() {
    servicioPartida.iniciarPartida("ABC123", host);

    Partida partida = servicioPartida.finalizarPartida("ABC123", host);

    assertThat(partida.getEstado(), equalTo(EstadoPartida.FINALIZADA));

    assertThat(sala.getEstado(), equalTo(EstadoSala.FINALIZADA));
  }

  @Test
  public void dadoQueInicioUnaPartidaCuandoLaBuscoPorCodigoObtengoLaPartidaEnCurso() {
    servicioPartida.iniciarPartida("ABC123", host);

    Partida partida = servicioPartida.buscarPartidaPorCodigoSala("ABC123");

    assertThat(partida.getEstado(), equalTo(EstadoPartida.EN_CURSO));
  }
}
