package com.tallerwebi.dominio;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AlmacenEnMemoria {

  private Map<String, Sala> salas = new HashMap<>();
  private Map<Long, Usuario> usuarios = new HashMap<>();

  public Map<String, Sala> getSalas() {
    return salas;
  }

  public Map<Long, Usuario> getUsuarios() {
    return usuarios;
  }
}
