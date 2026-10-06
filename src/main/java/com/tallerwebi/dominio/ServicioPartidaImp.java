package com.tallerwebi.dominio;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service("servicioPartidaImp")
public class ServicioPartidaImp implements ServicioPartida {

  private final ServicioSala servicioSala;
  private final Map<String, Partida> partidas = new HashMap<>();

  public ServicioPartidaImp(ServicioSala servicioSala) {
    this.servicioSala = servicioSala;
  }

  @Override
  public Partida iniciarPartida(String codigoSala, Usuario solicitante) {
    //primero busco la sala mediante su código
    Sala salaBuscada = this.buscarSala(codigoSala);

    //la sala se puede iniciar
    this.validarSalaPuedeIniciarse(salaBuscada);

    //es host el usuario
    this.validarEsHost(salaBuscada, solicitante);

    //se cambia el estado de la sala
    this.cambiarSalaAEnCurso(salaBuscada);

    //se crea la partida
    Partida partida = this.crearPartida(salaBuscada);

    //se guarda la partida
    this.guardarPartida(codigoSala, partida);

    //se devuelve la partida
    return partida;
  }

  @Override
  public Partida finalizarPartida(String codigoSala, Usuario solicitante) {
    //se busca la partida por el código de sala
    Partida partidaBuscada = buscarPartida(codigoSala);

    //se valida si la partida puede finalizar
    this.validarPartidaPuedeFinalizar(partidaBuscada);

    //se obtiene de la partida la sala
    Sala sala = partidaBuscada.getSala();

    //se valida si es host
    validarEsHost(sala, solicitante);

    //se cambia el estado partida
    this.cambiarEstadoPartida(partidaBuscada);

    this.cambiarSalaAFinalizada(sala);

    return partidaBuscada;
  }

  @Override
  public Partida buscarPartidaPorCodigoSala(String codigoSala) {
    return buscarPartida(codigoSala);
  }

  private void validarPartidaPuedeFinalizar(Partida partida) {
    if (partida.getEstado() != EstadoPartida.EN_CURSO) {
      throw new IllegalStateException("La partida no se encuentra en curso");
    }
  }

  private Partida buscarPartida(String codigoSala) {
    Partida partida = partidas.get(codigoSala);

    if (partida == null) {
      throw new PartidaNoEncontradaException("No hay una partida para la sala " + codigoSala);
    }

    return partida;
  }

  private Sala buscarSala(String codigoSala) {
    Sala sala = servicioSala.buscarPorCodigo(codigoSala);

    if (sala == null) {
      throw new SalaNoEncontradaException("No existe la sala " + codigoSala);
    }

    return sala;
  }

  private void validarSalaPuedeIniciarse(Sala sala) {
    if (sala.getEstado() != EstadoSala.EN_ESPERA) {
      throw new SalaNoPuedeIniciarseException("La sala no está esperando jugadores");
    }

    if (sala.getJugadores().isEmpty()) {
      throw new SalaSinJugadoresException("No hay jugadores para iniciar la partida");
    }
  }

  private void validarEsHost(Sala sala, Usuario solicitante) {
    if (!sala.getHost().equals(solicitante)) {
      throw new UsuarioNoEsHostException("Solo el host puede iniciar la partida");
    }
  }

  private void cambiarSalaAEnCurso(Sala sala) {
    sala.setEstado(EstadoSala.EN_CURSO);
  }

  private void cambiarSalaAFinalizada(Sala sala) {
    sala.setEstado(EstadoSala.FINALIZADA);
  }

  private Partida crearPartida(Sala sala) {
    return new Partida(sala, EstadoPartida.EN_CURSO);
  }

  private void guardarPartida(String codigoSala, Partida partida) {
    partidas.put(codigoSala, partida);
  }

  private void cambiarEstadoPartida(Partida partida) {
    partida.setEstado(EstadoPartida.FINALIZADA);
  }
}
