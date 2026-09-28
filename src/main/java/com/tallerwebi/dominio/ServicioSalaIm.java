package com.tallerwebi.dominio;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service("servicioSalaImpl")
@Transactional
public class ServicioSalaIm implements ServicioSala {

  @Override
  public void unirse(Sala sala, Usuario usuario) {
    if (sala.getJugadores().size() >= sala.getMaxJugadores()) {
      throw new SalaLlenaException("Sala llena");
    }
    sala.agregarJugador(usuario);
  }

  @Override
  public Sala crearSala(String codigo, String nombre, Usuario host) {
    return new Sala(codigo, nombre, host);
  }

  @Override
  public void salir(Sala sala, Usuario usuario) {
    //pregunto primero:si la sala de los jugadores un usuario no pertenece a esa sala
    //entonces lanzo una excepción
    if (!sala.getJugadores().contains(usuario)) {
      throw new UsuarioNoPerteneceASalaException("El usuario no pertenece a la sala");
    }

    //si el usuario era host , se quita ese jugador
    boolean eraHost = sala.getHost().equals(usuario);

    sala.quitarJugador(usuario);

    //si era host se quita el jugador , entonces se establece null al host actual
    //de lo contrario obtengo el primer jugador que encuentre
    if (eraHost) {
      if (sala.getJugadores().isEmpty()) {
        sala.setHost(null);
      } else {
        sala.setHost(sala.getJugadores().get(0));
      }
    }
  }
}
