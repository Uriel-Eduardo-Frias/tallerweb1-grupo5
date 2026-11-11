package com.tallerwebi.dominio.servicio.impl;

import com.tallerwebi.dominio.excepcion.DesafioDiarioException;
import com.tallerwebi.dominio.modelo.DesafioDiario;
import com.tallerwebi.dominio.modelo.IntentoDesafio;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Pregunta;
import com.tallerwebi.dominio.modelo.PreguntaDesafio;
import com.tallerwebi.dominio.modelo.RespuestaDesafio;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioDesafioDiario;
import com.tallerwebi.dominio.repositorio.RepositorioIntentoDesafio;
import com.tallerwebi.dominio.repositorio.RepositorioOpcion;
import com.tallerwebi.dominio.repositorio.RepositorioPregunta;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.ServicioDesafioDiario;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioDesafioDiarioImpl implements ServicioDesafioDiario {

  private static final int CANTIDAD_PREGUNTAS = 5;

  private final RepositorioDesafioDiario repositorioDesafio;
  private final RepositorioIntentoDesafio repositorioIntento;
  private final RepositorioUsuario repositorioUsuario;
  private final RepositorioPregunta repositorioPregunta;
  private final RepositorioOpcion repositorioOpcion;

  @Autowired
  public ServicioDesafioDiarioImpl(
    RepositorioDesafioDiario repositorioDesafio,
    RepositorioIntentoDesafio repositorioIntento,
    RepositorioUsuario repositorioUsuario,
    RepositorioPregunta repositorioPregunta,
    RepositorioOpcion repositorioOpcion
  ) {
    this.repositorioDesafio = repositorioDesafio;
    this.repositorioIntento = repositorioIntento;
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioPregunta = repositorioPregunta;
    this.repositorioOpcion = repositorioOpcion;
  }

  @Override
  public DesafioDiario obtenerDesafioDeHoy() {
    LocalDate fechaHoy = LocalDate.now();
    DesafioDiario desafio = repositorioDesafio.buscarPorFecha(fechaHoy);

    if (desafio == null) {
      desafio = crearDesafioDelDia(fechaHoy);
    }

    inicializarOpciones(desafio);
    return desafio;
  }

  private DesafioDiario crearDesafioDelDia(LocalDate fecha) {
    List<Pregunta> preguntas = repositorioPregunta.buscarAleatorias(CANTIDAD_PREGUNTAS);

    validarCantidadDePreguntas(preguntas);

    DesafioDiario desafio = new DesafioDiario(fecha);
    agregarPreguntasAlDesafio(desafio, preguntas);
    repositorioDesafio.guardar(desafio);

    return desafio;
  }

  private void validarCantidadDePreguntas(List<Pregunta> preguntas) {
    if (preguntas == null || preguntas.size() < CANTIDAD_PREGUNTAS) {
      throw new DesafioDiarioException("No hay suficientes preguntas para crear el desafío.");
    }
  }

  private void agregarPreguntasAlDesafio(DesafioDiario desafio, List<Pregunta> preguntas) {
    for (int i = 0; i < CANTIDAD_PREGUNTAS; i++) {
      desafio.agregarPregunta(preguntas.get(i), i + 1);
    }
  }

  private void inicializarOpciones(DesafioDiario desafio) {
    for (PreguntaDesafio preguntaDesafio : desafio.getPreguntas()) {
      Pregunta pregunta = preguntaDesafio.getPregunta();
      Hibernate.initialize(pregunta.getOpciones());
    }
  }

  private void inicializarRespuestas(IntentoDesafio intento) {
    Hibernate.initialize(intento.getRespuestas());

    for (RespuestaDesafio respuesta : intento.getRespuestas()) {
      Hibernate.initialize(respuesta.getPregunta());

      if (respuesta.getOpcionElegida() != null) {
        Hibernate.initialize(respuesta.getOpcionElegida());
      }
    }
  }

  @Override
  public IntentoDesafio iniciarOContinuarIntento(Long usuarioId) {
    DesafioDiario desafio = obtenerDesafioDeHoy();

    IntentoDesafio intentoPendiente = repositorioIntento.buscarIntentoPendiente(
      usuarioId,
      desafio.getId()
    );

    if (intentoPendiente != null) {
      inicializarOpciones(intentoPendiente.getDesafio());
      inicializarRespuestas(intentoPendiente);
      return intentoPendiente;
    }

    Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);

    if (usuario == null) {
      throw new DesafioDiarioException("No se encontró el usuario.");
    }

    IntentoDesafio nuevoIntento = new IntentoDesafio(usuario, desafio, LocalDateTime.now());

    repositorioIntento.guardar(nuevoIntento);
    return nuevoIntento;
  }

  @Override
  public RespuestaDesafio responder(Long usuarioId, Long preguntaId, Long opcionId) {
    IntentoDesafio intento = iniciarOContinuarIntento(usuarioId);

    validarIntentoAbierto(intento);

    Pregunta pregunta = buscarPreguntaDelDesafio(intento.getDesafio(), preguntaId);

    validarPreguntaSinResponder(intento, preguntaId);

    Opcion opcion = repositorioOpcion.obtenerPorId(opcionId);
    validarOpcionDeLaPregunta(opcion, pregunta);

    RespuestaDesafio respuesta = crearRespuesta(intento, pregunta, opcion);

    intento.agregarRespuesta(respuesta);
    actualizarPuntaje(intento, respuesta);
    finalizarSiRespondioTodo(intento);

    repositorioIntento.guardar(intento);
    inicializarRespuestas(intento);

    return respuesta;
  }

  private void validarIntentoAbierto(IntentoDesafio intento) {
    if (intento.estaFinalizado()) {
      throw new DesafioDiarioException("Este intento ya fue completado.");
    }
  }

  private Pregunta buscarPreguntaDelDesafio(DesafioDiario desafio, Long preguntaId) {
    for (PreguntaDesafio preguntaDesafio : desafio.getPreguntas()) {
      Pregunta pregunta = preguntaDesafio.getPregunta();

      if (pregunta.getIdentificador().equals(preguntaId)) {
        return pregunta;
      }
    }

    throw new DesafioDiarioException("La pregunta no pertenece al desafío de hoy.");
  }

  private void validarPreguntaSinResponder(IntentoDesafio intento, Long preguntaId) {
    for (RespuestaDesafio respuesta : intento.getRespuestas()) {
      Long idRespondido = respuesta.getPregunta().getIdentificador();

      if (idRespondido.equals(preguntaId)) {
        throw new DesafioDiarioException("Ya respondiste esta pregunta en este intento.");
      }
    }
  }

  private void validarOpcionDeLaPregunta(Opcion opcion, Pregunta pregunta) {
    if (opcion == null || opcion.getPregunta() == null) {
      throw new DesafioDiarioException("La opción seleccionada no existe.");
    }

    Long preguntaOpcionId = opcion.getPregunta().getIdentificador();

    if (!preguntaOpcionId.equals(pregunta.getIdentificador())) {
      throw new DesafioDiarioException("La opción no corresponde a esta pregunta.");
    }
  }

  private RespuestaDesafio crearRespuesta(
    IntentoDesafio intento,
    Pregunta pregunta,
    Opcion opcion
  ) {
    return new RespuestaDesafio(
      intento,
      pregunta,
      opcion,
      opcion.getEsCorrecta(),
      LocalDateTime.now(),
      false
    );
  }

  private void actualizarPuntaje(IntentoDesafio intento, RespuestaDesafio respuesta) {
    if (respuesta.isEsCorrecta()) {
      intento.setPuntaje(intento.getPuntaje() + 1);
    }
  }

  private void finalizarSiRespondioTodo(IntentoDesafio intento) {
    int respuestasRegistradas = intento.getRespuestas().size();
    int preguntasDelDesafio = intento.getDesafio().getPreguntas().size();

    if (respuestasRegistradas == preguntasDelDesafio) {
      intento.setFechaFin(LocalDateTime.now());
    }
  }

  @Override
  public IntentoDesafio obtenerIntentoDeHoy(Long usuarioId) {
    DesafioDiario desafio = obtenerDesafioDeHoy();

    IntentoDesafio intento = repositorioIntento.buscarIntentoPendiente(usuarioId, desafio.getId());

    if (intento != null) {
      inicializarOpciones(intento.getDesafio());
      inicializarRespuestas(intento);
    }

    return intento;
  }

  @Override
  public List<IntentoDesafio> obtenerRankingDeHoy() {
    DesafioDiario desafio = obtenerDesafioDeHoy();

    List<IntentoDesafio> intentos = repositorioIntento.buscarFinalizadosPorDesafio(desafio.getId());

    List<IntentoDesafio> mejoresIntentos = buscarMejoresIntentosPorUsuario(intentos);

    ordenarRankingPorPuntajeYDuracion(mejoresIntentos);

    return mejoresIntentos;
  }

  private List<IntentoDesafio> buscarMejoresIntentosPorUsuario(List<IntentoDesafio> intentos) {
    List<IntentoDesafio> mejoresIntentos = new ArrayList<>();

    for (IntentoDesafio intento : intentos) {
      Long usuarioId = intento.getUsuario().getId();
      int indiceUsuario = buscarIndiceDelUsuario(mejoresIntentos, usuarioId);

      if (indiceUsuario == -1) {
        mejoresIntentos.add(intento);
      } else {
        IntentoDesafio mejorActual = mejoresIntentos.get(indiceUsuario);

        if (esMejorIntento(intento, mejorActual)) {
          mejoresIntentos.set(indiceUsuario, intento);
        }
      }
    }

    return mejoresIntentos;
  }

  private int buscarIndiceDelUsuario(List<IntentoDesafio> intentos, Long usuarioId) {
    for (int i = 0; i < intentos.size(); i++) {
      Long idActual = intentos.get(i).getUsuario().getId();

      if (idActual.equals(usuarioId)) {
        return i;
      }
    }

    return -1;
  }

  private boolean esMejorIntento(IntentoDesafio candidato, IntentoDesafio actual) {
    if (candidato.getPuntaje() > actual.getPuntaje()) {
      return true;
    }

    if (candidato.getPuntaje().equals(actual.getPuntaje())) {
      return calcularDuracionEnSegundos(candidato) < calcularDuracionEnSegundos(actual);
    }

    return false;
  }

  private long calcularDuracionEnSegundos(IntentoDesafio intento) {
    return Duration.between(intento.getFechaInicio(), intento.getFechaFin()).getSeconds();
  }

  private void ordenarRankingPorPuntajeYDuracion(List<IntentoDesafio> intentos) {
    for (int i = 0; i < intentos.size(); i++) {
      for (int j = i + 1; j < intentos.size(); j++) {
        IntentoDesafio primero = intentos.get(i);
        IntentoDesafio segundo = intentos.get(j);

        if (esMejorIntento(segundo, primero)) {
          intentos.set(i, segundo);
          intentos.set(j, primero);
        }
      }
    }
  }

  @Override
  public List<IntentoDesafio> obtenerHistorial(Long usuarioId) {
    return repositorioIntento.buscarFinalizadosPorUsuario(usuarioId);
  }
}
