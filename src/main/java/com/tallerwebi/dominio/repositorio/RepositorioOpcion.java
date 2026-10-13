package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.Opcion;

@FunctionalInterface
public interface RepositorioOpcion {
  Opcion obtenerPorId(Long opcionId);
}
