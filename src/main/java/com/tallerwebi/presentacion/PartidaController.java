package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.enumeration.EstadoRonda;
import com.tallerwebi.dominio.enumeration.TipoComodin;
import com.tallerwebi.dominio.excepcion.PrimerIntentoFallidoDobleChanceException;
import com.tallerwebi.dominio.excepcion.RespuestaDuplicadaException;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaRonda;
import com.tallerwebi.dominio.modelo.Pregunta;
import com.tallerwebi.dominio.modelo.ResultadoPartida;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioComodin;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.ServicioPartidaPregunta;
import com.tallerwebi.dominio.servicio.ServicioRespuestaJugador;
import com.tallerwebi.dominio.servicio.ServicioResultadoPartida;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PartidaController {

  private static final String MODELO_PARTIDA = "partida";
  private static final String MODELO_RESULTADO = "resultado";
  private static final String VISTA_FIN = "partida-fin";
  private static final String ID_USUARIO = "ID_USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_SALAS = "redirect:/salas";
  private static final String REDIRECT_PARTIDA = "redirect:/partida/";
  private static final String SUFIJO_IR = "/ir";
  private static final String SUFIJO_PREGUNTA = "/pregunta";
  private static final String SUFIJO_VOTACION_CATEGORIA = "/votacion-categoria";
  private static final String SUFIJO_RESULTADO = "/resultado";
  private static final String SUFIJO_FIN = "/fin";

  private final ServicioPartida servicioPartida;
  private final ServicioUsuario servicioUsuario;
  private final NotificadorSala notificadorSala;
  private final ServicioPartidaPregunta servicioPartidaPregunta;
  private final ServicioRespuestaJugador servicioRespuestaJugador;
  private final ServicioResultadoPartida servicioResultadoPartida;
  private final ServicioComodin servicioComodin;

  public PartidaController(
    ServicioPartida servicioPartida,
    ServicioUsuario servicioUsuario,
    NotificadorSala notificadorSala,
    ServicioPartidaPregunta servicioPartidaPregunta,
    ServicioRespuestaJugador servicioRespuestaJugador,
    ServicioResultadoPartida servicioResultadoPartida,
    ServicioComodin servicioComodin
  ) {
    this.servicioPartida = servicioPartida;
    this.servicioUsuario = servicioUsuario;
    this.notificadorSala = notificadorSala;
    this.servicioPartidaPregunta = servicioPartidaPregunta;
    this.servicioRespuestaJugador = servicioRespuestaJugador;
    this.servicioResultadoPartida = servicioResultadoPartida;
    this.servicioComodin = servicioComodin;
  }

  // ---------------------------------------------------------------------
  // INICIO / FIN DE PARTIDA
  // ---------------------------------------------------------------------

  @RequestMapping(value = "/partidas/{codigoSala}/iniciar", method = RequestMethod.POST)
  public ModelAndView iniciarPartida(
    @PathVariable("codigoSala") String codigoSala,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = obtenerUsuario(session);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      Partida partida = servicioPartida.iniciarPartida(codigoSala, usuario);

      notificadorSala.partidaIniciada(codigoSala, partida.getId());

      return new ModelAndView(REDIRECT_PARTIDA + partida.getId() + SUFIJO_IR);
    } catch (IllegalStateException excepcion) { // ✅ Variable de longitud >= 2
      redirectAttributes.addFlashAttribute("error", excepcion.getMessage());

      return new ModelAndView(REDIRECT_SALAS + "/" + codigoSala);
    }
  }

  @RequestMapping(value = "/partidas/{codigoSala}/finalizar", method = RequestMethod.POST)
  public ModelAndView finalizarPartida(
    @PathVariable("codigoSala") String codigoSala,
    HttpSession session
  ) {
    Usuario usuario = obtenerUsuario(session);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Partida partida = servicioPartida.finalizarPartida(codigoSala, usuario);

    return new ModelAndView(REDIRECT_PARTIDA + partida.getId() + SUFIJO_FIN);
  }

  @RequestMapping(value = "/partida/{id}/fin", method = RequestMethod.GET)
  public ModelAndView mostrarFin(@PathVariable("id") Long id, HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      ResultadoPartida resultado = servicioResultadoPartida.obtenerResultado(id, usuarioId);

      Map<String, Object> modelo = new ModelMap();
      modelo.put(MODELO_RESULTADO, resultado);

      return new ModelAndView(VISTA_FIN, modelo);
    } catch (IllegalStateException excepcionEstado) { // ✅ Variable descriptiva
      // La partida todavía no finalizó: el router ubica al jugador.
      return new ModelAndView(REDIRECT_PARTIDA + id + SUFIJO_IR);
    } catch (IllegalArgumentException excepcionArgumento) { // ✅ Variable descriptiva
      // El usuario no participó en esta partida.
      return new ModelAndView(REDIRECT_SALAS);
    }
  }

  // ---------------------------------------------------------------------
  // ROUTER: decide la pantalla según EstadoRonda (única fuente de verdad)
  // ---------------------------------------------------------------------

  @RequestMapping(value = "/partida/{id}/ir", method = RequestMethod.GET)
  public String irSegunEstado(@PathVariable("id") Long id, HttpSession session) {
    if (obtenerUsuarioId(session) == null) {
      return REDIRECT_LOGIN;
    }

    Partida partida = servicioPartida.buscarPartidaPorId(id);

    if (partida.getEstado() == EstadoPartida.FINALIZADA) {
      return REDIRECT_PARTIDA + id + SUFIJO_FIN;
    }

    PartidaRonda ronda = partida.obtenerRondaActual();

    if (ronda == null) {
      return REDIRECT_SALAS;
    }

    if (ronda.getEstado() == EstadoRonda.PREGUNTA_EN_CURSO && ronda.getPregunta() != null) {
      servicioPartida.finalizarRondaSiTodosRespondieron(id);
      partida = servicioPartida.buscarPartidaPorId(id);
      ronda = partida.obtenerRondaActual();
    }

    switch (ronda.getEstado()) {
      case VOTACION_CATEGORIA:
        return REDIRECT_PARTIDA + id + SUFIJO_VOTACION_CATEGORIA;
      case PREGUNTA_EN_CURSO:
        return REDIRECT_PARTIDA + id + SUFIJO_PREGUNTA;
      case RESULTADO_PREGUNTA:
        return REDIRECT_PARTIDA + id + SUFIJO_RESULTADO;
      default:
        return REDIRECT_SALAS;
    }
  }

  // ---------------------------------------------------------------------
  // VOTACIÓN DE CATEGORÍA
  // ---------------------------------------------------------------------

  @RequestMapping(value = "/partida/{id}/votacion-categoria", method = RequestMethod.GET)
  public ModelAndView mostrarVotacionCategoria(@PathVariable("id") Long id, HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Partida partida = servicioPartida.buscarPartidaPorId(id);

    if (!estaEnEstado(partida, EstadoRonda.VOTACION_CATEGORIA)) {
      return new ModelAndView(REDIRECT_PARTIDA + id + SUFIJO_IR);
    }

    Map<String, Object> modelo = new ModelMap();
    modelo.put(MODELO_PARTIDA, partida);
    modelo.put("mapaDeCategorias", servicioPartidaPregunta.obtenerCategorias());
    modelo.put("yaVoto", servicioPartida.usuarioYaVoto(id, usuarioId));

    return new ModelAndView("partida-votacion", modelo);
  }

  @RequestMapping(value = "/partida/{id}/votar-categoria", method = RequestMethod.POST)
  public ModelAndView votarCategoria(
    @PathVariable("id") Long partidaId,
    @RequestParam("categoriaId") Long categoriaId,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioPartida.registrarVotoCategoria(partidaId, usuarioId, categoriaId);
    } catch (IllegalStateException ignored) { // ✅ 'ignored' es reconocido por PMD
      // Voto duplicado o votación ya cerrada: el router ubica al jugador.
    }

    return new ModelAndView(REDIRECT_PARTIDA + partidaId + SUFIJO_IR);
  }

  @RequestMapping(value = "/partida/{id}/resultado-votacion", method = RequestMethod.POST)
  public ModelAndView finalizarVotacion(@PathVariable("id") Long partidaId, HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioPartida.finalizarVotacionCategoria(partidaId, usuarioId);

    return new ModelAndView(REDIRECT_PARTIDA + partidaId + SUFIJO_IR);
  }

  // ---------------------------------------------------------------------
  // PREGUNTA Y RESPUESTA
  // ---------------------------------------------------------------------

  @RequestMapping(value = "/partida/{id}/pregunta", method = RequestMethod.GET)
  public ModelAndView mostrarPregunta(@PathVariable("id") Long partidaId, HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Partida partida = servicioPartida.buscarPartidaPorId(partidaId);

    if (!estaEnEstado(partida, EstadoRonda.PREGUNTA_EN_CURSO)) {
      return new ModelAndView(REDIRECT_PARTIDA + partidaId + SUFIJO_IR);
    }

    servicioPartida.finalizarRondaSiTodosRespondieron(partidaId);
    partida = servicioPartida.buscarPartidaPorId(partidaId);
    if (!estaEnEstado(partida, EstadoRonda.PREGUNTA_EN_CURSO)) {
      return new ModelAndView(REDIRECT_PARTIDA + partidaId + SUFIJO_IR);
    }

    PartidaRonda ronda = partida.obtenerRondaActual();

    Map<String, Object> modelo = new ModelMap();
    modelo.put(MODELO_PARTIDA, partida);
    modelo.put("ronda", ronda);
    modelo.put("pregunta", ronda.getPregunta());
    modelo.put("yaRespondio", servicioRespuestaJugador.yaRespondio(partidaId, usuarioId));

    modelo.put(
      "puede5050",
      servicioComodin.tieneComodinDisponible(partidaId, usuarioId, TipoComodin.CINCUENTA_CINCUENTA)
    );
    modelo.put(
      "puedeTiempo",
      servicioComodin.tieneComodinDisponible(partidaId, usuarioId, TipoComodin.TIEMPO_EXTRA)
    );
    modelo.put(
      "puedeDobleChance",
      servicioComodin.tieneComodinDisponible(partidaId, usuarioId, TipoComodin.DOBLE_CHANCE)
    );

    modelo.put("nivelJugador", servicioComodin.obtenerNivelJugador(usuarioId));

    return new ModelAndView("partida-pregunta", modelo);
  }

  @RequestMapping(value = "/partida/{id}/responder", method = RequestMethod.POST)
  public String responder(
    @PathVariable("id") Long partidaId,
    @RequestParam("opcionId") Long opcionId,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return REDIRECT_LOGIN;
    }

    try {
      servicioRespuestaJugador.registrarRespuesta(partidaId, usuarioId, opcionId);
    } catch (PrimerIntentoFallidoDobleChanceException excepcion) { // ✅ Longitud >= 2
      redirectAttributes.addFlashAttribute("falloPrimerIntentoDobleChance", Boolean.TRUE);
      redirectAttributes.addFlashAttribute("opcionFalladaId", excepcion.getOpcionIncorrectaId());
      return REDIRECT_PARTIDA + partidaId + SUFIJO_PREGUNTA;
    } catch (RespuestaDuplicadaException ignored) {} //

    return REDIRECT_PARTIDA + partidaId + SUFIJO_IR;
  }

  @RequestMapping(value = "/partida/{id}/tiempo-agotado", method = RequestMethod.POST)
  public String tiempoAgotado(@PathVariable("id") Long partidaId, HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return REDIRECT_LOGIN;
    }

    try {
      servicioRespuestaJugador.registrarTiempoAgotado(partidaId, usuarioId);
    } catch (RespuestaDuplicadaException ignored) {}

    return REDIRECT_PARTIDA + partidaId + SUFIJO_IR;
  }

  // ---------------------------------------------------------------------
  // RESULTADO DE LA PREGUNTA Y AVANCE DE RONDA
  // ---------------------------------------------------------------------

  @RequestMapping(value = "/partida/{id}/resultado", method = RequestMethod.GET)
  public ModelAndView mostrarResultado(@PathVariable("id") Long id, HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Partida partida = servicioPartida.buscarPartidaPorId(id);

    if (!estaEnEstado(partida, EstadoRonda.RESULTADO_PREGUNTA)) {
      return new ModelAndView(REDIRECT_PARTIDA + id + SUFIJO_IR);
    }

    PartidaRonda ronda = partida.obtenerRondaActual();
    Boolean esCorrecta = servicioRespuestaJugador.obtenerResultado(id, usuarioId);
    boolean fuePorTiempo = servicioRespuestaJugador.fuePorTiempo(id, usuarioId);
    boolean esHost = servicioPartida.esHost(id, usuarioId);

    Map<String, Object> modelo = new ModelMap();
    modelo.put(MODELO_PARTIDA, partida);
    modelo.put("esCorrecta", Boolean.TRUE.equals(esCorrecta));
    modelo.put("fuePorTiempo", fuePorTiempo);
    modelo.put("textoCorrecto", obtenerTextoCorrecto(ronda.getPregunta()));
    modelo.put("esHost", esHost);
    modelo.put("esUltimaRonda", partida.getRondaActual() >= partida.getTotalRondas());

    return new ModelAndView("partida-resultado", modelo);
  }

  @RequestMapping(value = "/partida/{id}/siguiente", method = RequestMethod.POST)
  public String siguienteRonda(@PathVariable("id") Long id, HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return REDIRECT_LOGIN;
    }

    servicioPartida.avanzarRonda(id, usuarioId);

    return REDIRECT_PARTIDA + id + SUFIJO_IR;
  }

  // ---------------------------------------------------------------------
  // AUXILIARES
  // ---------------------------------------------------------------------

  private boolean estaEnEstado(Partida partida, EstadoRonda estadoEsperado) {
    PartidaRonda ronda = partida.obtenerRondaActual();

    return ronda != null && ronda.getEstado() == estadoEsperado;
  }

  private String obtenerTextoCorrecto(Pregunta pregunta) {
    for (Opcion opcion : pregunta.getOpciones()) {
      if (Boolean.TRUE.equals(opcion.getEsCorrecta())) {
        return opcion.getTexto();
      }
    }

    return "";
  }

  private Usuario obtenerUsuario(HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    return usuarioId == null ? null : servicioUsuario.buscarUsuarioPorId(usuarioId);
  }

  private Long obtenerUsuarioId(HttpSession session) {
    return (Long) session.getAttribute(ID_USUARIO);
  }
}
