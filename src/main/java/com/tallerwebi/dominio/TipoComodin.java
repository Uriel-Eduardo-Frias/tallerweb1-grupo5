package com.tallerwebi.dominio;

public enum TipoComodin {
  CINCUENTA_CINCUENTA("50/50", "Elimina 2 opciones incorrectas"),
  TIEMPO_EXTRA("+10s", "Agrega 10 segundos al temporizador"),
  PASAR_PREGUNTA("Pasar", "Cambia la pregunta actual sin penalización"),
  DOBLE_CHANCE("2x Chance", "Permite un segundo intento si fallas");

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
