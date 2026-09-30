package com.tallerwebi.dominio;

public interface ServicioPartida {
  Partida iniciarPartida(String codigoSala, Usuario solicitante);
  Partida finalizarPartida();
  Partida buscarPartidaPorCodigoSala(String codigoSala);
}
