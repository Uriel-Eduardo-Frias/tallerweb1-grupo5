package com.tallerwebi.dominio.enumeration;

public enum TipoLogro {
  EN_RACHA("En Racha", "Acierta 3 preguntas consecutivas en una partida"),
  FUEGO_PURO("Fuego Puro", "Acierta 5 preguntas seguidas sin equivocarte"),
  CAMPEON_DEBUTANTE("Campeon Debutante", "Gana tu primera partida de Trivia"),
  ERUDITO("Erudito", "Alcanza 50 respuestas correctas en tu historial"),
  IMPARABLE("Imparable", "Gana 3 partidas consecutivas");

  private final String nombre;
  private final String descripcion;

  TipoLogro(String nombre, String descripcion) {
    this.nombre = nombre;
    this.descripcion = descripcion;
  }

  public String getNombre() {
    return nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }
}
