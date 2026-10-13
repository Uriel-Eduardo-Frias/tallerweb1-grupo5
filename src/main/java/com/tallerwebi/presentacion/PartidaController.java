package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.ServicioPartidaPregunta;
import com.tallerwebi.dominio.servicio.ServicioRespuestaJugador;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PartidaController {

  private static final String ID_USUARIO = "ID_USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_PARTIDA = "redirect:/partida/";

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

  // INICIO DE PARTIDA

  @PostMapping("/partidas/{codigoSala}/iniciar")
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

      return new ModelAndView(REDIRECT_PARTIDA + partida.getId() + "/votacion-categoria");
    } catch (IllegalStateException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());

      return new ModelAndView("redirect:/salas/" + codigoSala);
    }
  }

  // FINALIZACIÓN DE PARTIDA

  @PostMapping("/partidas/{codigoSala}/finalizar")
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

  // VOTACIÓN DE CATEGORÍAS

  @GetMapping("/partida/{id}/votacion-categoria")
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

  @PostMapping("/partida/{id}/votar-categoria")
  public ModelAndView votarCategoria(
    @PathVariable("id") Long partidaId,
    @RequestParam("categoriaId") Long categoriaId,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioPartida.registrarVotoCategoria(partidaId, usuarioId, categoriaId);

    return new ModelAndView(REDIRECT_PARTIDA + partidaId + "/votacion-categoria");
  }

  @PostMapping("/partida/{id}/resultado-votacion")
  public ModelAndView obtenerResultadoVotacion(
    @PathVariable("id") Long partidaId,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioPartida.finalizarVotacionCategoria(partidaId, usuarioId);

    return new ModelAndView(REDIRECT_PARTIDA + partidaId + "/pregunta");
  }

  // ESTADO DEL JUGADOR

  @PostMapping("/partida/{id}/listo")
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

  // MOSTRAR PREGUNTA

  @GetMapping("/partida/{id}/pregunta")
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

  // REGISTRAR RESPUESTA DEL JUGADOR

  @PostMapping("/partida/{id}/responder")
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

    boolean esCorrecta = servicioRespuestaJugador.registrarRespuesta(
      partidaId,
      usuarioId,
      opcionId
    );

    Partida partida = servicioPartida.buscarPartidaPorId(partidaId);
    Pregunta pregunta = partida.obtenerRondaActual().getPregunta();

    String textoCorrecto = "";

    for (Opcion opcion : pregunta.getOpciones()) {
      if (opcion.getEsCorrecta()) {
        textoCorrecto = opcion.getTexto();
        break;
      }
    }

    redirectAttributes.addFlashAttribute("esCorrecta", esCorrecta);
    redirectAttributes.addFlashAttribute("textoCorrecto", textoCorrecto);
    redirectAttributes.addFlashAttribute("mensaje", "Tu respuesta fue registrada.");

    return REDIRECT_PARTIDA + partidaId + "/pregunta";
  }

  // OBTENER USUARIO DE LA SESIÓN

  private Long obtenerUsuarioId(HttpSession session) {
    return (Long) session.getAttribute(ID_USUARIO);
  }
}
