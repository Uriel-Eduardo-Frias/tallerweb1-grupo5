package com.tallerwebi.dominio;

public interface ServicioPartida {
  Partida iniciarPartida(String codigoSala, Usuario solicitante);
  Partida finalizarPartida(String codigoSala, Usuario solicitante);
  Partida buscarPartidaPorCodigoSala(String codigoSala);
}
