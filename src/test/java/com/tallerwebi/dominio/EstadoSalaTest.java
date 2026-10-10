package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tallerwebi.dominio.enums.EstadoSala;
import org.junit.jupiter.api.Test;

public class EstadoSalaTest {

  @Test
  public void deberiaTenerTresEstados() {
    assertEquals(3, EstadoSala.values().length);
    assertEquals(EstadoSala.EN_ESPERA, EstadoSala.valueOf("EN_ESPERA"));
    assertEquals(EstadoSala.EN_CURSO, EstadoSala.valueOf("EN_CURSO"));
    assertEquals(EstadoSala.FINALIZADA, EstadoSala.valueOf("FINALIZADA"));
  }
}
