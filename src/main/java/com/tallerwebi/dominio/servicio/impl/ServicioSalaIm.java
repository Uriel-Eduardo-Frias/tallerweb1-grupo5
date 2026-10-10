package com.tallerwebi.dominio.servicio.impl;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.enums.EstadoJugador;
import com.tallerwebi.dominio.enums.EstadoSala;
import com.tallerwebi.dominio.enums.ModoJuego;
import com.tallerwebi.dominio.excepcion.SalaLlenaException;
import com.tallerwebi.dominio.excepcion.SalaNoEncontradaException;
import com.tallerwebi.dominio.excepcion.UsuarioNoEsHostException;
import com.tallerwebi.dominio.excepcion.UsuarioNoPerteneceASalaException;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.SalaJugador;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioSala;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.ServicioSala;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service("servicioSalaImpl")
@Transactional
public class ServicioSalaIm implements ServicioSala {

  private final RepositorioSala repositorioSala;
  private final RepositorioUsuario repositorioUsuario;

  public ServicioSalaIm(RepositorioSala repositorioSala, RepositorioUsuario repositorioUsuario) {
    this.repositorioSala = repositorioSala;
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public Sala unirse(String codigoInvitacion, Long usuarioId) {
    validarCodigo(codigoInvitacion);

    Sala sala = obtenerSalaOLanzar(codigoInvitacion);
    validarSalaEnEspera(sala);

    SalaJugador participacion = buscarParticipacionPorUsuario(sala, usuarioId);

    if (participacion != null) {
      participacion.setEstadoJugador(EstadoJugador.CONECTADO);
      repositorioSala.guardar(sala);
      return sala;
    }

    validarCapacidad(sala);

    Usuario usuario = obtenerUsuarioPersistido(usuarioId);

    SalaJugador nuevoJugador = new SalaJugador(sala, EstadoJugador.CONECTADO, false, usuario);

    nuevoJugador.setSala(sala);
    sala.getJugadores().add(nuevoJugador);

    repositorioSala.guardar(sala);
    return sala;
  }

  @Override
  public Sala crearSala(
    String nombre,
    Usuario hostRecibido,
    int maxJugadores,
    int totalRondas,
    ModoJuego modoJuego,
    boolean esPrivada
  ) {
    if (hostRecibido == null || hostRecibido.getId() == null) {
      throw new IllegalArgumentException("El usuario anfitrión debe existir en la base de datos");
    }

    Usuario host = obtenerUsuarioPersistido(hostRecibido.getId());

    String nombreSala;
    if (nombre != null && !nombre.isBlank()) {
      nombreSala = nombre.trim();
    } else {
      nombreSala = "Sala de " + host.getUsername();
    }

    Sala sala = new Sala(generarCodigoUnico(), nombreSala, host);

    sala.setMaxJugadores(maxJugadores);
    sala.setTotalRondas(totalRondas);
    sala.setModoJuego(modoJuego);
    sala.setEsPrivada(esPrivada);

    SalaJugador jugadorHost = new SalaJugador(sala, EstadoJugador.CONECTADO, true, host);

    jugadorHost.setSala(sala);
    sala.getJugadores().add(jugadorHost);

    repositorioSala.guardar(sala);
    return sala;
  }

  @Override
  public void salir(Sala salaRecibida, Usuario usuarioRecibido) {
    if (salaRecibida == null) {
      throw new IllegalArgumentException("La sala es obligatoria");
    }

    if (usuarioRecibido == null || usuarioRecibido.getId() == null) {
      throw new IllegalArgumentException("El usuario es obligatorio");
    }

    Sala sala = obtenerSalaOLanzar(salaRecibida.getCodigo());
    Usuario usuario = obtenerUsuarioPersistido(usuarioRecibido.getId());
    SalaJugador participacion = buscarParticipacion(sala, usuario);

    if (participacion == null) {
      throw new UsuarioNoPerteneceASalaException("El usuario no pertenece a la sala");
    }

    boolean eraHost = sala.getHost() != null && sala.getHost().getId().equals(usuario.getId());

    sala.getJugadores().remove(participacion);

    if (eraHost) {
      reasignarHost(sala);
    }

    repositorioSala.guardar(sala);
  }

  @Override
  public void cambiarHost(
    Sala salaRecibida,
    Usuario solicitanteRecibido,
    Usuario nuevoHostRecibido
  ) {
    if (salaRecibida == null) {
      throw new IllegalArgumentException("La sala es obligatoria");
    }

    Sala sala = obtenerSalaOLanzar(salaRecibida.getCodigo());

    if (solicitanteRecibido == null || solicitanteRecibido.getId() == null) {
      throw new UsuarioNoEsHostException("Solo el host puede transferir el rol");
    }

    Usuario solicitante = obtenerUsuarioPersistido(solicitanteRecibido.getId());

    validarQueSolicitanteEsHost(sala, solicitante);

    if (nuevoHostRecibido == null || nuevoHostRecibido.getId() == null) {
      throw new UsuarioNoPerteneceASalaException("El nuevo host no pertenece a la sala");
    }

    Usuario nuevoHost = obtenerUsuarioPersistido(nuevoHostRecibido.getId());

    validarQueUsuarioPerteneceASala(sala, nuevoHost);

    sala.setHost(nuevoHost);
    actualizarAnfitriones(sala, nuevoHost);

    repositorioSala.guardar(sala);
  }

  @Override
  public Sala buscarPorCodigo(String codigo) {
    validarCodigo(codigo);
    return obtenerSalaOLanzar(codigo);
  }

  @Override
  public List<Sala> listarSalas() {
    return repositorioSala.listarSalas();
  }

  private String generarCodigoUnico() {
    String codigo;

    do {
      codigo = GeneradorCodigo.generarCodigoSala();
    } while (repositorioSala.obtenerSalaPorCodigo(codigo) != null);

    return codigo;
  }

  private Sala obtenerSalaOLanzar(String codigo) {
    Sala sala = repositorioSala.obtenerPorCodigoConJugadores(codigo);

    if (sala == null) {
      throw new SalaNoEncontradaException("No se encontró ninguna sala con el código " + codigo);
    }

    return sala;
  }

  private Usuario obtenerUsuarioPersistido(Long usuarioId) {
    Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);

    if (usuario == null) {
      throw new IllegalArgumentException("No existe un usuario persistido con ID " + usuarioId);
    }

    return usuario;
  }

  private void validarCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new IllegalArgumentException("El código de invitación es obligatorio");
    }
  }

  private void validarSalaEnEspera(Sala sala) {
    if (sala.getEstado() != EstadoSala.EN_ESPERA) {
      throw new IllegalStateException("La sala ya no se encuentra en fase de espera o ya comenzó");
    }
  }

  private void validarCapacidad(Sala sala) {
    if (sala.getJugadores().size() >= sala.getMaxJugadores()) {
      throw new SalaLlenaException(
        "La sala ha alcanzado su capacidad máxima de " + sala.getMaxJugadores() + " jugadores"
      );
    }
  }

  private SalaJugador buscarParticipacionPorUsuario(Sala sala, Long usuarioId) {
    for (SalaJugador jugador : sala.getJugadores()) {
      if (jugador.getUsuario() != null && jugador.getUsuario().getId().equals(usuarioId)) {
        return jugador;
      }
    }

    return null;
  }

  private SalaJugador buscarParticipacion(Sala sala, Usuario usuario) {
    for (SalaJugador jugador : sala.getJugadores()) {
      if (jugador.getUsuario() != null && jugador.getUsuario().getId().equals(usuario.getId())) {
        return jugador;
      }
    }

    return null;
  }

  private void reasignarHost(Sala sala) {
    if (sala.getJugadores().isEmpty()) {
      sala.setHost(null);
      return;
    }

    SalaJugador nuevaParticipacionHost = sala.getJugadores().get(0);
    Usuario nuevoHost = nuevaParticipacionHost.getUsuario();

    sala.setHost(nuevoHost);
    actualizarAnfitriones(sala, nuevoHost);
  }

  private void actualizarAnfitriones(Sala sala, Usuario nuevoHost) {
    for (SalaJugador jugador : sala.getJugadores()) {
      boolean esNuevoHost = jugador.getUsuario().getId().equals(nuevoHost.getId());

      jugador.setEsAnfitrion(esNuevoHost);
    }
  }

  private void validarQueSolicitanteEsHost(Sala sala, Usuario solicitante) {
    if (sala.getHost() == null || !sala.getHost().getId().equals(solicitante.getId())) {
      throw new UsuarioNoEsHostException("Solo el host puede transferir el rol");
    }
  }

  private void validarQueUsuarioPerteneceASala(Sala sala, Usuario usuario) {
    boolean pertenece = false;

    for (SalaJugador jugador : sala.getJugadores()) {
      if (jugador.getUsuario() != null && jugador.getUsuario().getId().equals(usuario.getId())) {
        pertenece = true;
        break;
      }
    }

    if (!pertenece) {
      throw new UsuarioNoPerteneceASalaException("El nuevo host no pertenece a la sala");
    }
  }
}
