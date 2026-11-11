package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.DesafioDiario;
import java.time.LocalDate;

public interface RepositorioDesafioDiario {
  DesafioDiario buscarPorFecha(LocalDate fecha);
  void guardar(DesafioDiario desafio);
}
