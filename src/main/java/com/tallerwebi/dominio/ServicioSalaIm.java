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
}
