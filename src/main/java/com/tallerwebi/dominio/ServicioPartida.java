package com.tallerwebi.dominio;

public interface ServicioPartida {
  Partida iniciarPartida(String codigoSala, Usuario solicitante);
  Partida finalizarPartida(String codigoSala, Usuario solicitante);
  Partida buscarPartidaPorCodigoSala(String codigoSala);
  Partida buscarPartidaPorId(Long id);
  void marcarJugadorListo(Long idPartida, Usuario usuario);
  boolean estanTodosListos(Long idPartida);
  //RespuestaJugador buscarRespuestaDeJugador(PartidaRonda partidaRonda, Long partidaJugadorId)
  //boolean haRespondido(PartidaRonda partidaRonda, Long partidaJugadorId)
  //RespuestaJugador obtenerPrimerAcierto(PartidaRonda partidaRonda)
  void sumarPuntaje(PartidaJugador jugador, int puntos);
  void registrarAcierto(PartidaJugador jugador);
  void registrarFallo(PartidaJugador jugador);
  //PartidaJugadorComodin buscarComodin(PartidaJugador jugador, Long comodinId
  boolean puedeUsarse(PartidaJugadorComodin comodinJugador);
  boolean consumirComodin(PartidaJugadorComodin comodinJugador);
}
