package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioSala {
  Sala unirse(String codigo, Long usuarioId);
  Sala crearSala(
    String nombre,
    Usuario host,
    int maxJugadores,
    int totalRondas,
    ModoJuego modoJuego,
    boolean esPrivada
  );
  void salir(Sala sala, Usuario usuario);
  void cambiarHost(Sala sala, Usuario solicitante, Usuario nuevoHost);
  Sala buscarPorCodigo(String codigo);
  List<Sala> listarSalas();
}
