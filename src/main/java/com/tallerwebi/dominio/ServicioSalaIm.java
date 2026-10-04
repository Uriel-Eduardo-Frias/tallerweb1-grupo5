package com.tallerwebi.dominio;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioSalaImpl")
@Transactional
public class ServicioSalaIm implements ServicioSala {

  private final AlmacenEnMemoria almacen;

  public ServicioSalaIm(AlmacenEnMemoria almacen) {
    this.almacen = almacen;
  }

  private String generarCodigoUnico() {
    String codigo;
    do {
      codigo = GeneradorCodigo.generarCodigoSala();
    } while (almacen.getSalas().containsKey(codigo));
    return codigo;
  }

  @Override
  public Sala unirse(String codigoInvitacion, Long usuarioId) {
    validarCodigo(codigoInvitacion);

    Sala sala = obtenerSalaOLanzar(codigoInvitacion);

    validarSalaEnEspera(sala);

    SalaJugador participacionExistente = buscarParticipacionPorUsuario(sala, usuarioId);

    if (participacionExistente != null) {
      participacionExistente.setEstadoJugador(EstadoJugador.CONECTADO);
      return sala;
    }

    Usuario usuarioEncontrado = obtenerUsuarioOLanzar(usuarioId);
    validarCapacidad(sala);

    SalaJugador nuevoParticipante = new SalaJugador();
    nuevoParticipante.setSala(sala);
    nuevoParticipante.setUsuario(usuarioEncontrado);
    nuevoParticipante.setEsAnfitrion(false);
    nuevoParticipante.setEstadoJugador(EstadoJugador.CONECTADO);

    sala.getJugadores().add(nuevoParticipante);

    return sala;
  }

  private Sala obtenerSalaOLanzar(String codigo) {
    Sala sala = buscarSalaPorCodigo(codigo);
    if (sala == null) {
      throw new IllegalArgumentException("No se encontró ninguna sala con el código " + codigo);
    }
    return sala;
  }

  private Usuario obtenerUsuarioOLanzar(Long usuarioId) {
    Usuario usuario = almacen.getUsuarios().get(usuarioId);
    if (usuario == null) {
      throw new IllegalArgumentException("El usuario no existe");
    }
    return usuario;
  }

  private void validarCapacidad(Sala sala) {
    if (sala.getJugadores().size() >= sala.getMaxJugadores()) {
      throw new SalaLlenaException(
        "La sala ha alcanzado su capacidad máxima de " + sala.getMaxJugadores() + " jugadores"
      );
    }
  }

  private void validarSalaEnEspera(Sala sala) {
    if (sala.getEstado() != EstadoSala.EN_ESPERA) {
      throw new IllegalStateException("La sala ya no se encuentra en fase de espera o ya comenzó");
    }
  }

  private void validarCodigo(String codigo) {
    if (codigo == null || codigo.trim().isEmpty()) {
      throw new IllegalArgumentException("El código de invitación es obligatorio");
    }
  }

  private Sala buscarSalaPorCodigo(String codigoInvitacion) {
    for (Sala sala : almacen.getSalas().values()) {
      if (codigoInvitacion.equals(sala.getCodigo())) {
        return sala;
      }
    }
    return null;
  }

  private SalaJugador buscarParticipacionPorUsuario(Sala sala, Long usuarioId) {
    for (SalaJugador jugador : sala.getJugadores()) {
      if (jugador.getUsuario().getId().equals(usuarioId)) {
        return jugador;
      }
    }

    return null;
  }

  @Override
  public Sala crearSala(
    String nombre,
    Usuario host,
    int maxJugadores,
    int totalRondas,
    ModoJuego modoJuego,
    boolean esPrivada
  ) {
    if (host == null) {
      throw new IllegalArgumentException("El usuario anfitrión no existe");
    }

    String nombreSala = (nombre != null && !nombre.isBlank())
      ? nombre.trim()
      : "Sala de " + host.getUsername();

    String codigo = generarCodigoUnico();

    Sala sala = new Sala(codigo, nombreSala, host);
    sala.setMaxJugadores(maxJugadores);
    sala.setTotalRondas(totalRondas);
    sala.setModoJuego(modoJuego);
    sala.setEsPrivada(esPrivada);

    SalaJugador jugadorHost = new SalaJugador(sala, EstadoJugador.CONECTADO, true, host);
    sala.agregarJugador(jugadorHost);

    almacen.getSalas().put(codigo, sala);
    return sala;
  }

  private SalaJugador buscarParticipacion(Sala sala, Usuario usuario) {
    for (SalaJugador jugador : sala.getJugadores()) {
      if (jugador.getUsuario().equals(usuario)) {
        return jugador;
      }
    }

    return null;
  }

  @Override
  public void salir(Sala sala, Usuario usuario) {
    SalaJugador participacion = buscarParticipacion(sala, usuario);

    if (participacion == null) {
      throw new UsuarioNoPerteneceASalaException("El usuario no pertenece a la sala");
    }

    boolean eraHost = usuario.equals(sala.getHost());

    sala.getJugadores().remove(participacion);

    if (eraHost) {
      if (sala.getJugadores().isEmpty()) {
        sala.setHost(null);
      } else {
        SalaJugador nuevaParticipacionHost = sala.getJugadores().get(0);
        Usuario nuevoHost = nuevaParticipacionHost.getUsuario();

        sala.setHost(nuevoHost);
        nuevaParticipacionHost.setEsAnfitrion(true);
      }
    }
  }

  @Override
  public void cambiarHost(Sala sala, Usuario solicitante, Usuario nuevoHost) {
    validarQueSolicitanteEsHost(sala, solicitante);
    validarQueUsuarioPerteneceASala(sala, nuevoHost);

    sala.setHost(nuevoHost);
  }

  private void validarQueSolicitanteEsHost(Sala sala, Usuario solicitante) {
    if (!sala.getHost().equals(solicitante)) {
      throw new UsuarioNoEsHostException("Solo el host puede transferir el rol");
    }
  }

  private void validarQueUsuarioPerteneceASala(Sala sala, Usuario usuario) {
    boolean pertenece = false;

    for (SalaJugador sj : sala.getJugadores()) {
      if (sj.getUsuario().equals(usuario)) {
        pertenece = true;
        break;
      }
    }

    if (!pertenece) {
      throw new UsuarioNoPerteneceASalaException("El nuevo host no pertenece a la sala");
    }
  }

  @Override
  public Sala buscarPorCodigo(String codigo) {
    Sala sala = almacen.getSalas().get(codigo);
    if (sala == null) {
      throw new SalaNoEncontradaException("No se pudo encontrar la sala");
    }
    return sala;
  }

  @Override
  public List<Sala> listarSalas() {
    return new ArrayList<>(almacen.getSalas().values());
  }
}
