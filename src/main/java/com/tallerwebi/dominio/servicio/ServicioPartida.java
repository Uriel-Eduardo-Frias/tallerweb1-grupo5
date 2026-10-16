package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.PartidaJugadorComodin;
import com.tallerwebi.dominio.modelo.Usuario;

public interface ServicioPartida {
  Partida iniciarPartida(String codigoSala, Usuario solicitante);
  Partida finalizarPartida(String codigoSala, Usuario solicitante);
  Partida buscarPartidaPorCodigoSala(String codigoSala);
  Partida buscarPartidaPorId(Long id);
  void marcarJugadorListo(Long idPartida, Usuario usuario);
  boolean estanTodosListos(Long idPartida);
  void sumarPuntaje(PartidaJugador jugador, int puntos);
  void registrarAcierto(PartidaJugador jugador);
  void registrarFallo(PartidaJugador jugador);
  boolean puedeUsarse(PartidaJugadorComodin comodinJugador);
  boolean consumirComodin(PartidaJugadorComodin comodinJugador);
  void finalizarVotacionCategoria(Long partidaId, Long usuarioId);
  boolean registrarVotoCategoria(Long partidaId, Long usuarioId, Long categoriaId);
  boolean finalizarRondaSiTodosRespondieron(Long partidaId);
  void avanzarRonda(Long partidaId, Long usuarioId);
  boolean usuarioYaVoto(Long partidaId, Long usuarioId);
  boolean esHost(Long partidaId, Long usuarioId);
}
