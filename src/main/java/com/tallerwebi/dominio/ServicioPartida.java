package com.tallerwebi.dominio;

public interface ServicioPartida {
  Partida iniciarPartida(Sala sala, Usuario solicitante);
  Partida finalizarPartida();
}
