package com.tallerwebi.dominio;

public interface ServicioSala {
  void unirse(Sala sala, Usuario usuario);
  Sala crearSala(String codigo, String nombre, Usuario host);
}
