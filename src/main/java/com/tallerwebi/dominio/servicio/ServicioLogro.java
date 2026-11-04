package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.enumeration.TipoLogro;
import com.tallerwebi.dominio.modelo.Logro;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.Usuario;
import java.util.List;

public interface ServicioLogro {
  void evaluarLogroDePartida(Long partidaId);

  List<TipoLogro> evaluarLogrosDelJugador(PartidaJugador jugador);

  List<Logro> obtenerLogrosDisponibles(Long usuarioId);
}
