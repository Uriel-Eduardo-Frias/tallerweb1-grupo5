package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.enumeration.EstadoRonda;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaRonda;
import com.tallerwebi.dominio.modelo.Pregunta;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class PartidaRondaTest {

  @Test
  void dadoQueCreoUnaRondaCuandoUsoElConstructorVacioEntoncesInicializaSusValores() {
    PartidaRonda ronda = new PartidaRonda();

    assertEquals(EstadoRonda.VOTACION_CATEGORIA, ronda.getEstado());
    assertNotNull(ronda.getFechaInicio());
    assertNull(ronda.getPartida());
    assertNull(ronda.getPregunta());
  }

  @Test
  void dadoQueCreoUnaRondaCuandoIndicoPartidaYNumeroEntoncesLosAsigna() {
    Partida partida = new Partida();
    PartidaRonda ronda = new PartidaRonda(partida, 2);

    assertSame(partida, ronda.getPartida());
    assertEquals(2, ronda.getNumero());
    assertEquals(EstadoRonda.VOTACION_CATEGORIA, ronda.getEstado());
  }

  @Test
  void dadoQueTengoUnaRondaCuandoAsignoUnaPreguntaEntoncesLaConserva() {
    PartidaRonda ronda = new PartidaRonda();
    Pregunta pregunta = new Pregunta();

    ronda.setPregunta(pregunta);

    assertSame(pregunta, ronda.getPregunta());
  }

  @Test
  void dadoQueTengoUnaRondaCuandoModificoSusDatosEntoncesDevuelveLosValoresActualizados() {
    PartidaRonda ronda = new PartidaRonda();
    Partida partida = new Partida();
    Pregunta pregunta = new Pregunta();
    LocalDateTime inicio = LocalDateTime.of(2026, 10, 1, 10, 0);
    LocalDateTime fin = LocalDateTime.of(2026, 10, 1, 10, 1);

    ronda.setId(5L);
    ronda.setPartida(partida);
    ronda.setNumero(4);
    ronda.setPregunta(pregunta);
    ronda.setFechaInicio(inicio);
    ronda.setFechaFin(fin);

    assertEquals(5L, ronda.getId());
    assertSame(partida, ronda.getPartida());
    assertEquals(4, ronda.getNumero());
    assertSame(pregunta, ronda.getPregunta());
    assertEquals(inicio, ronda.getFechaInicio());
    assertEquals(fin, ronda.getFechaFin());
  }
}
