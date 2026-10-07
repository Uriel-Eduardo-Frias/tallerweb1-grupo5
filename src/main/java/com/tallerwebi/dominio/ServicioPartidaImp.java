package com.tallerwebi.dominio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service("servicioPartidaImp")
public class ServicioPartidaImp implements ServicioPartida {

  private final ServicioSala servicioSala;
  private final Map<String, Partida> partidas = new HashMap<>();
  private Long siguienteId = 1L;

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

    this.crearPrimeraRonda(partida);

    this.asignarId(partida);

    //se guarda la partida
    this.guardarPartida(codigoSala, partida);

    //se devuelve la partida
    return partida;
  }

  private void crearPrimeraRonda(Partida partida) {
    //se instancia la ronda
    PartidaRonda ronda = new PartidaRonda(partida, 1);

    //por defecto se añade la votación categoría
    ronda.setEstado(EstadoRonda.VOTACION_CATEGORIA);

    //obtengo de partida la cantidad de rondas y añado una
    partida.getRondas().add(ronda);
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
  public Partida buscarPartidaPorId(Long id) {
    for (Partida partida : partidas.values()) {
      if (partida.getId().equals(id)) {
        return partida;
      }
    }

    throw new PartidaNoEncontradaException("No existe la partida " + id);
  }

  @Override
  public Partida buscarPartidaPorCodigoSala(String codigoSala) {
    return buscarPartida(codigoSala);
  }

  /*
  @Override
  public PartidaRonda activarPreguntaRonda(String codigoSala, int numeroRonda) {
    Partida partida = buscarPartida(codigoSala);

    if (partida == null) {
      return null;
    }

    PartidaRonda ronda = buscarRonda(partida, numeroRonda);

    if (!puedeActivarse(ronda)) {
      return ronda;
    }

    //Pregunta pregunta = servicioPregunta.seleccionarPreguntaAleatoria();

    /*
    if (pregunta == null) {
      return ronda;
    }


    //ronda.setPregunta(pregunta);
    ronda.setEstado(EstadoRonda.PREGUNTA_ACTIVA);

    return ronda;
  }
  */
  /*
  private PartidaRonda buscarRonda(Partida partida, int numeroRonda) {
    List<PartidaRonda> rondas = partida.getRondas();

    for (int i = 0; i < rondas.size(); i++) {
      PartidaRonda ronda = rondas.get(i);

      if (ronda.getNumero() == numeroRonda) {
        return ronda;
      }
    }

    return null;
  }
*/
  /*
  private boolean puedeActivarse(PartidaRonda ronda) {
    return ronda != null
            && ronda.getEstado() == EstadoRonda.VOTACION_CATEGORIA;
  }
*/

  private void validarPartidaPuedeFinalizar(Partida partida) {
    if (partida.getEstado() != EstadoPartida.EN_CURSO) {
      throw new IllegalStateException("La partida no se encuentra en curso");
    }
  }

  private void asignarId(Partida partida) {
    partida.setId(siguienteId);
    siguienteId++;
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

  @Override
  public void marcarJugadorListo(Long idPartida, Usuario usuario) {
    Partida partida = buscarPartidaPorId(idPartida);

    Sala sala = partida.getSala();

    SalaJugador jugador = buscarJugadorEnSala(sala, usuario);

    jugador.setEstadoJugador(EstadoJugador.LISTO);
  }

  private SalaJugador buscarJugadorEnSala(Sala sala, Usuario usuario) {
    for (SalaJugador jugador : sala.getJugadores()) {
      if (jugador.getUsuario().getId().equals(usuario.getId())) {
        return jugador;
      }
    }

    throw new JugadorInexistenteExeption("El usuario no pertenece a la sala");
  }

  @Override
  public boolean estanTodosListos(Long idPartida) {
    Partida partida = buscarPartidaPorId(idPartida);

    Sala sala = partida.getSala();

    if (sala.getJugadores().isEmpty()) {
      return false;
    }

    for (SalaJugador jugador : sala.getJugadores()) {
      if (jugador.getEstadoJugador() != EstadoJugador.LISTO) {
        return false;
      }
    }

    return true;
  }
}
