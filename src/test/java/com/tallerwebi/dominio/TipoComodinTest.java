package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tallerwebi.dominio.enumeration.TipoComodin;
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
  public void bloqueoDeberiaTenerEtiquetaYDescripcion() {
    assertEquals("Bloqueo 10s", TipoComodin.BLOQUEO.getEtiqueta());
    assertEquals("Bloquea al oponente durante 10 segundos", TipoComodin.BLOQUEO.getDescripcion());
  }

  @Test
  public void borrosoDeberiaTenerEtiquetaYDescripcion() {
    assertEquals("Borroso 5s", TipoComodin.BORROSO.getEtiqueta());
    assertEquals(
      "Hace borrosa la pantalla del oponente durante 5 segundos",
      TipoComodin.BORROSO.getDescripcion()
    );
  }

  @Test
  public void mezclarDeberiaTenerEtiquetaYDescripcion() {
    assertEquals("Mezclar", TipoComodin.MEZCLAR.getEtiqueta());
    assertEquals("Desordena las opciones del oponente", TipoComodin.MEZCLAR.getDescripcion());
  }

  @Test
  public void valueOfDeberiaDevolverElComodinCorrecto() {
    assertEquals(7, TipoComodin.values().length);
    assertEquals(TipoComodin.TIEMPO_EXTRA, TipoComodin.valueOf("TIEMPO_EXTRA"));
    assertEquals(TipoComodin.BLOQUEO, TipoComodin.valueOf("BLOQUEO"));
    assertEquals(TipoComodin.BORROSO, TipoComodin.valueOf("BORROSO"));
    assertEquals(TipoComodin.MEZCLAR, TipoComodin.valueOf("MEZCLAR"));
  }
}
