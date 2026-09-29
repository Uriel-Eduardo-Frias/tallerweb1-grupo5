package com.tallerwebi.dominio;

import java.util.Random;

public class GeneradorCodigo {

  private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

  private static final Random RANDOM = new Random();

  private GeneradorCodigo() {
    // Evita instanciar esta clase de utilidades
  }

  public static String generarCodigoSala() {
    StringBuilder sb = new StringBuilder("TRV-");

    for (int i = 0; i < 4; i++) {
      int index = RANDOM.nextInt(CARACTERES.length());
      sb.append(CARACTERES.charAt(index));
    }

    return sb.toString();
  }
}
