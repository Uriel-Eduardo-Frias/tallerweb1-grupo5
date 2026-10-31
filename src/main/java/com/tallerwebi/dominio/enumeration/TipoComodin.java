package com.tallerwebi.dominio.enumeration;

public enum TipoComodin {
  //  (Modo TURNO_TODOS)
  CINCUENTA_CINCUENTA("50/50", "Elimina 2 opciones incorrectas"),
  TIEMPO_EXTRA("+10s", "Agrega 10 segundos al temporizador"),
  PASAR_PREGUNTA("Pasar", "Cambia la pregunta actual sin penalización"),
  DOBLE_CHANCE("2x Chance", "Permite un segundo intento si fallas"),

  //  (Modo PRIMERO_GANA)
  BLOQUEAR_OPONENTE("Congelar", "Bloquea las opciones del oponente durante 5 segundos"),
  PANTALLA_HUMO("Humo", "Ciega con niebla la pantalla del oponente durante 4 segundos"),
  CONFUSION_OPCIONES("Confusión", "Desordena e invierte las opciones del oponente");

  private final String etiqueta;
  private final String descripcion;

  TipoComodin(String etiqueta, String descripcion) {
    this.etiqueta = etiqueta;
    this.descripcion = descripcion;
  }

  public String getEtiqueta() { return etiqueta; }
  public String getDescripcion() { return descripcion; }
}