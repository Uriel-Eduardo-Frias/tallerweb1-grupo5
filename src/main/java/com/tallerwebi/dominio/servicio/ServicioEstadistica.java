package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.EstadisticaUsuario;
import java.util.List;

public interface ServicioEstadistica {
  EstadisticaUsuario obtenerEstadisticaUsuario(Long usuarioId);
  List<EstadisticaUsuario> obtenerLeaderBoard(int limite);
  int calcularNivelUsuario(Long usuarioId);
}
