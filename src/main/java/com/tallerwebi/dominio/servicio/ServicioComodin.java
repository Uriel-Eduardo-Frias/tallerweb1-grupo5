package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.enumeration.TipoComodin;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.PartidaJugadorComodin;
import com.tallerwebi.presentacion.dto.Comodin5050DTO;
import java.util.List;

public interface ServicioComodin {
  void inicializarComodinesParaJugador(PartidaJugador jugador);
  List<PartidaJugadorComodin> obtenerComodinesDeJugador(Long partidaId, Long usuarioId);
  boolean tieneComodinDisponible(Long partidaId, Long usuarioId, TipoComodin tipo);
  int aplicarTiempoExtra(Long partidaId, Long usuarioId);
  Comodin5050DTO aplicarCincuentaCincuenta(Long partidaId, Long usuarioId);
  void activarDobleChance(Long partidaId, Long usuarioId);
}
