package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class GeneradorCodigoTest {

  @Test
  public void deberiaGenerarCodigoConFormatoValido() {
    for (int i = 0; i < 100; i++) {
      String codigo = GeneradorCodigo.generarCodigoSala();

      assertEquals(8, codigo.length());
      assertTrue(codigo.matches("TRV-[A-HJ-NP-Z2-9]{4}"), "Codigo invalido: " + codigo);
    }
  }

  @Test
  public void constructorPrivadoDeberiaPoderInvocarseConReflection() throws Exception {
    Constructor<GeneradorCodigo> constructor = GeneradorCodigo.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    assertNotNull(constructor.newInstance());
  }
}
