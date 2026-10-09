package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class GeneradorCodigoTest {

  @Test
  void generarCodigoSalaDebeTenerFormatoValido() {
    for (int i = 0; i < 200; i++) {
      String codigo = GeneradorCodigo.generarCodigoSala();
      assertTrue(codigo.matches("^TRV-[A-HJ-NP-Z2-9]{4}$"), "Código inválido: " + codigo);
    }
  }

  @Test
  void elConstructorPrivadoNoDebeRomperSiSeInvocaPorReflection() throws Exception {
    Constructor<GeneradorCodigo> constructor = GeneradorCodigo.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    assertNotNull(constructor.newInstance());
  }
}
