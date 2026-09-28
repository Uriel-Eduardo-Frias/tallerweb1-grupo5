package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

@Service("servicioPartidaImp")
public class ServicioPartidaImp implements ServicioPartida {

  @Override
  public Partida iniciarPartida(Sala sala, Usuario solicitante) {
    //se valida que si la sala , el usuario no es el host se lanza una excepción
    if (!sala.getHost().equals(solicitante)) {
      throw new UsuarioNoEsHostException("Solo el host puede iniciar la partida");
    }
    return new Partida(sala, EstadoPartida.INICIADA);
  }

  @Override
  public Partida finalizarPartida() {
    return null;
  }
}
