package com.tallerwebi.dominio;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AlmacenEnMemoria {

  private Map<String, Sala> salas = new HashMap<>();
  private Map<Long, Usuario> usuarios = new HashMap<>();
  private final Map<Long, Partida> partidas = new HashMap<>();
  private long siguienteIdPartida = 1L;

  public Map<String, Sala> getSalas() {
    return salas;
  }

  public Map<Long, Usuario> getUsuarios() {
    return usuarios;
  }

  public Map<Long, Partida> getPartidas() {
    return partidas;
  }

  public Long generarIdPartida() {
    Long id = siguienteIdPartida;
    siguienteIdPartida++;
    return id;
  }
}
