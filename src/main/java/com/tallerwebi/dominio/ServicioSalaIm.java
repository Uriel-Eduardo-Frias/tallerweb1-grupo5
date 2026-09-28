package com.tallerwebi.dominio;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("servicioSalaImpl")
@Transactional
public class ServicioSalaIm implements ServicioSala {


  private final Map<String, Sala> salas = new HashMap<>();


  @Override
  public void unirse(Sala sala, Usuario usuario) {
    if (sala.getJugadores().size() >= sala.getMaxJugadores()) {
      throw new SalaLlenaException("Sala llena");
    }
    sala.agregarJugador(usuario);
  }

  @Override
  public Sala crearSala(String codigo, String nombre, Usuario host) {
    Sala sala = new Sala(codigo, nombre, host);

    sala.agregarJugador(host);
    salas.put(codigo,sala);
    return sala;
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

  @Override
  public void cambiarHost(Sala sala, Usuario solicitante, Usuario nuevoHost) {
    //validamos que si el host no es igual al soliciante se lanza la excepción para esta prueba
    if (!sala.getHost().equals(solicitante)) {
      throw new UsuarioNoEsHostException("Solo el host puede transferir el rol");
    }

    //si en la sala de los jugadores, no contiene un host se lanza la excepción
    if (!sala.getJugadores().contains(nuevoHost)) {
      throw new UsuarioNoPerteneceASalaException("El nuevo host no pertenece a la sala");
    }

    sala.setHost(nuevoHost);
  }

  @Override
  public Sala buscarPorCodigo(String codigo) {
    //se busca la sala por su código, sino la encuentra se devuelve una excepción que no encontrolo la sala
    Sala sala = salas.get(codigo);

    if(sala == null){
      throw new SalaNoEncontradaException("No se pudo encontrar la sala");
    }

    return sala;
  }



  @Override
  public List<Sala> listarSalas() {
    return new ArrayList<>(this.salas.values());
  }
}
