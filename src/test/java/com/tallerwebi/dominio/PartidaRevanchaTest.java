package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.modelo.Partida;
import java.time.LocalDateTime;
import org.junit.Test;

public class PartidaRevanchaTest {

  @Test
  void unaPartidaNuevaNoTieneSalaDeRevancha() {
    Partida partida = new Partida(null, EstadoPartida.EN_CURSO);

    assertNull(partida.getSalaRevanchaCodigo());
  }

  @Test
  void guardaYDevuelveElCodigoDeLaSalaDeRevancha() {
    Partida partida = new Partida(null, EstadoPartida.FINALIZADA);

    partida.setSalaRevanchaCodigo("NUEVA1");

    assertEquals("NUEVA1", partida.getSalaRevanchaCodigo());
  }

  // Cubren el refactor de fechas (válidos con o sin el @Embeddable PeriodoPartida)

  @Test
  void laFechaDeInicioSeAsignaAlCrearLaPartida() {
    Partida partida = new Partida(null, EstadoPartida.EN_CURSO);

    assertNotNull(partida.getFechaInicio());
  }

  @Test
  void guardaYDevuelveLaFechaDeFin() {
    Partida partida = new Partida(null, EstadoPartida.EN_CURSO);
    LocalDateTime fin = LocalDateTime.of(2026, 10, 31, 20, 0);

    partida.setFechaFin(fin);

    assertEquals(fin, partida.getFechaFin());
  }
}
