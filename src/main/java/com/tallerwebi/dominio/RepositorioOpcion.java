package com.tallerwebi.dominio;

@FunctionalInterface
public interface RepositorioOpcion {
  Opcion obtenerPorId(Long opcionId);
}
