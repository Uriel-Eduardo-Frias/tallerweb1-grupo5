package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tallerwebi.dominio.enums.TipoComodin;
import org.junit.jupiter.api.Test;

public class TipoComodinTest {

  @Test
  public void cincuentaCincuentaDeberiaTenerEtiquetaYDescripcion() {
    assertEquals("50/50", TipoComodin.CINCUENTA_CINCUENTA.getEtiqueta());
    assertEquals(
      "Elimina 2 opciones incorrectas",
      TipoComodin.CINCUENTA_CINCUENTA.getDescripcion()
    );
  }

  @Test
  public void tiempoExtraDeberiaTenerEtiquetaYDescripcion() {
    assertEquals("+10s", TipoComodin.TIEMPO_EXTRA.getEtiqueta());
    assertEquals("Agrega 10 segundos al temporizador", TipoComodin.TIEMPO_EXTRA.getDescripcion());
  }

  @Test
  public void pasarPreguntaDeberiaTenerEtiquetaYDescripcion() {
    assertEquals("Pasar", TipoComodin.PASAR_PREGUNTA.getEtiqueta());
    assertEquals(
      "Cambia la pregunta actual sin penalización",
      TipoComodin.PASAR_PREGUNTA.getDescripcion()
    );
  }

  @Test
  public void dobleChanceDeberiaTenerEtiquetaYDescripcion() {
    assertEquals("2x Chance", TipoComodin.DOBLE_CHANCE.getEtiqueta());
    assertEquals("Permite un segundo intento si fallas", TipoComodin.DOBLE_CHANCE.getDescripcion());
  }

  @Test
  public void valueOfDeberiaDevolverElComodinCorrecto() {
    assertEquals(4, TipoComodin.values().length);
    assertEquals(TipoComodin.TIEMPO_EXTRA, TipoComodin.valueOf("TIEMPO_EXTRA"));
  }
}
