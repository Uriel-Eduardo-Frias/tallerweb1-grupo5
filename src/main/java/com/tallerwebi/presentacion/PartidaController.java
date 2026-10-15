package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.ServicioPartidaPregunta;
import com.tallerwebi.dominio.servicio.ServicioRespuestaJugador;
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

  private static final String ID_USUARIO = "ID_USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_PARTIDA = "redirect:/partida/";
  private static final String SUFIJO_PREGUNTA = "/pregunta";
  private static final String SUFIJO_VOTACION_CATEGORIA = "/votacion-categoria";

  private final ServicioPartida servicioPartida;
  private final ServicioUsuario servicioUsuario;
  private final NotificadorSala notificadorSala;
  private final ServicioPartidaPregunta servicioPartidaPregunta;
  private final ServicioRespuestaJugador servicioRespuestaJugador;

  public PartidaController(
    ServicioPartida servicioPartida,
    ServicioUsuario servicioUsuario,
    NotificadorSala notificadorSala,
    ServicioPartidaPregunta servicioPartidaPregunta,
    ServicioRespuestaJugador servicioRespuestaJugador
  ) {
    this.servicioPartida = servicioPartida;
    this.servicioUsuario = servicioUsuario;
    this.notificadorSala = notificadorSala;
    this.servicioPartidaPregunta = servicioPartidaPregunta;
    this.servicioRespuestaJugador = servicioRespuestaJugador;
  }

  @RequestMapping(value = "/partidas/{codigoSala}/iniciar", method = RequestMethod.POST)
  public ModelAndView iniciarPartida(
    @PathVariable("codigoSala") String codigoSala,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(usuarioId);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      Partida partida = servicioPartida.iniciarPartida(codigoSala, usuario);

      notificadorSala.partidaIniciada(codigoSala, partida.getId());

      return new ModelAndView(REDIRECT_PARTIDA + partida.getId() + SUFIJO_VOTACION_CATEGORIA);
    } catch (IllegalStateException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());

      return new ModelAndView("redirect:/salas/" + codigoSala);
    }
  }

  @RequestMapping(value = "/partidas/{codigoSala}/finalizar", method = RequestMethod.POST)
  public ModelAndView finalizarPartida(
    @PathVariable("codigoSala") String codigoSala,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(usuarioId);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Partida partida = servicioPartida.finalizarPartida(codigoSala, usuario);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("partida", partida);

    return new ModelAndView("resultado", modelo);
  }

  @RequestMapping(value = "/partida/{id}/votacion-categoria", method = RequestMethod.GET)
  public ModelAndView mostrarVotacionCategoria(@PathVariable("id") Long id, HttpSession session) {
    if (obtenerUsuarioId(session) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Partida partida = servicioPartida.buscarPartidaPorId(id);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("partida", partida);
    modelo.put("mapaDeCategorias", servicioPartidaPregunta.obtenerCategorias());

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

    boolean todosVotaron = servicioPartida.registrarVotoCategoria(
      partidaId,
      usuarioId,
      categoriaId
    );

    if (todosVotaron) {
      return new ModelAndView(REDIRECT_PARTIDA + partidaId + SUFIJO_PREGUNTA);
    }

    return new ModelAndView(REDIRECT_PARTIDA + partidaId + SUFIJO_VOTACION_CATEGORIA);
  }

  @RequestMapping(value = "/partida/{id}/resultado-votacion", method = RequestMethod.POST)
  public ModelAndView obtenerResultadoVotacion(
    @PathVariable("id") Long partidaId,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioPartida.finalizarVotacionCategoria(partidaId, usuarioId);

    return new ModelAndView(REDIRECT_PARTIDA + partidaId + SUFIJO_PREGUNTA);
  }

  @RequestMapping(value = "/partida/{id}/listo", method = RequestMethod.POST)
  public ModelAndView marcarListo(@PathVariable("id") Long id, HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(usuarioId);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioPartida.marcarJugadorListo(id, usuario);

    return new ModelAndView(REDIRECT_PARTIDA + id + "/ronda");
  }

  @RequestMapping(value = "/partida/{id}/pregunta", method = RequestMethod.GET)
  public ModelAndView mostrarPregunta(@PathVariable("id") Long partidaId, HttpSession session) {
    if (obtenerUsuarioId(session) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Partida partida = servicioPartida.buscarPartidaPorId(partidaId);

    if (
      partida == null ||
      partida.obtenerRondaActual() == null ||
      partida.obtenerRondaActual().getPregunta() == null
    ) {
      return new ModelAndView("redirect:/salas");
    }

    PartidaRonda ronda = partida.obtenerRondaActual();
    Pregunta pregunta = ronda.getPregunta();

    Map<String, Object> modelo = new ModelMap();
    modelo.put("partida", partida);
    modelo.put("ronda", ronda);
    modelo.put("pregunta", pregunta);

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

    Partida partida = servicioPartida.buscarPartidaPorId(partidaId);
    Pregunta pregunta = obtenerPreguntaActiva(partida);
    String textoCorrecto = obtenerTextoCorrecto(pregunta);

    boolean esCorrecta;

    try {
      esCorrecta = servicioRespuestaJugador.registrarRespuesta(partidaId, usuarioId, opcionId);
    } catch (IllegalStateException e) {
      if (!"Ya respondiste esta pregunta.".equals(e.getMessage())) {
        throw e;
      }

      return manejarRespuestaDuplicada(partidaId, redirectAttributes);
    }

    agregarResultadoRespuesta(redirectAttributes, esCorrecta, textoCorrecto);

    return obtenerRedireccionPartida(partidaId);
  }

  private Pregunta obtenerPreguntaActiva(Partida partida) {
    if (partida == null) {
      throw new IllegalArgumentException("La partida no existe.");
    }

    PartidaRonda ronda = partida.obtenerRondaActual();

    if (ronda == null || ronda.getPregunta() == null) {
      throw new IllegalStateException("La partida no tiene una pregunta activa.");
    }

    return ronda.getPregunta();
  }

  private String obtenerTextoCorrecto(Pregunta pregunta) {
    for (Opcion opcion : pregunta.getOpciones()) {
      if (Boolean.TRUE.equals(opcion.getEsCorrecta())) {
        return opcion.getTexto();
      }
    }

    return "";
  }

  private void agregarResultadoRespuesta(
    RedirectAttributes redirectAttributes,
    boolean esCorrecta,
    String textoCorrecto
  ) {
    redirectAttributes.addFlashAttribute("esCorrecta", esCorrecta);
    redirectAttributes.addFlashAttribute("textoCorrecto", textoCorrecto);
    redirectAttributes.addFlashAttribute("mensaje", "Tu respuesta fue registrada.");
  }

  private String manejarRespuestaDuplicada(Long partidaId, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("respuestaYaRegistrada", true);
    redirectAttributes.addFlashAttribute("mensaje", "Tu respuesta ya fue registrada.");

    return obtenerRedireccionPartida(partidaId);
  }

  private String obtenerRedireccionPartida(Long partidaId) {
    Partida partida = servicioPartida.buscarPartidaPorId(partidaId);

    if (partida.getEstado() == EstadoPartida.FINALIZADA) {
      return REDIRECT_PARTIDA + partidaId + SUFIJO_PREGUNTA;
    }

    if (partida.getEstado() == EstadoPartida.VOTACION_CATEGORIA) {
      return REDIRECT_PARTIDA + partidaId + SUFIJO_VOTACION_CATEGORIA;
    }

    return REDIRECT_PARTIDA + partidaId + SUFIJO_PREGUNTA;
  }

  private Long obtenerUsuarioId(HttpSession session) {
    return (Long) session.getAttribute(ID_USUARIO);
  }
}
