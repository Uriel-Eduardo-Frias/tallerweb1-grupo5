package com.tallerwebi.dominio.enumeration;

public enum TipoComodin {
  // (TURNO_TODOS)
  CINCUENTA_CINCUENTA("50/50", "Elimina 2 opciones incorrectas"),
  TIEMPO_EXTRA("+10s", "Agrega 10 segundos al temporizador"),
  PASAR_PREGUNTA("Pasar", "Cambia la pregunta actual sin penalización"),
  DOBLE_CHANCE("2x Chance", "Permite un segundo intento si fallas"),

  //  (PRIMERO_GANA)
  BLOQUEO("Bloqueo 10s", "Bloquea al oponente durante 10 segundos"),
  BORROSO("Borroso 5s", "Hace borrosa la pantalla del oponente durante 5 segundos"),
  MEZCLAR("Mezclar", "Desordena las opciones del oponente");

  private final String etiqueta;
  private final String descripcion;

  TipoComodin(String etiqueta, String descripcion) {
    this.etiqueta = etiqueta;
    this.descripcion = descripcion;
  }

  public String getEtiqueta() {
    return etiqueta;
  }

  public String getDescripcion() {
    return descripcion;
  }
}