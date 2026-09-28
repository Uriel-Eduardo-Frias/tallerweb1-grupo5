package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("servicioPartidaImp")
public class ServicioPartidaImp implements ServicioPartida {

  private final ServicioSala servicioSala;
  private final Map<String, Partida> partidas = new HashMap<>();

  public ServicioPartidaImp(ServicioSala servicioSala) {
    this.servicioSala = servicioSala;
  }

    @Override
  public Partida iniciarPartida(String codigoSala,Usuario solicitante) {
      Sala sala = servicioSala.buscarPorCodigo(codigoSala);

    //se valida que si la sala , el usuario no es el host se lanza una excepción
    if (!sala.getHost().equals(solicitante)) {
      throw new UsuarioNoEsHostException("Solo el host puede iniciar la partida");
    }
    Partida partida = new Partida(sala, EstadoPartida.INICIADA);
    partidas.put(codigoSala, partida);

    return partida;
  }

  @Override
  public Partida finalizarPartida() {
    return null;
  }

  @Override
  public Partida buscarPartidaPorCodigoSala(String codigoSala) {
    return null;
  }
}
