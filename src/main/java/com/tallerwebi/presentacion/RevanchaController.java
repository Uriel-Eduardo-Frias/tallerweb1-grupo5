package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RevanchaController {

  private static final String ID_USUARIO = "ID_USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_SALAS = "redirect:/salas/";
  private static final String REDIRECT_FIN_PREFIJO = "redirect:/partida/";
  private static final String REDIRECT_FIN_SUFIJO = "/fin";
  private static final String EVENTO_REVANCHA_CREADA = "REVANCHA_CREADA";

  private final ServicioPartida servicioPartida;
  private final ServicioUsuario servicioUsuario;
  private final NotificadorSala notificadorSala;

  public RevanchaController(
    ServicioPartida servicioPartida,
    ServicioUsuario servicioUsuario,
    NotificadorSala notificadorSala
  ) {
    this.servicioPartida = servicioPartida;
    this.servicioUsuario = servicioUsuario;
    this.notificadorSala = notificadorSala;
  }

  // La vista de fin lo consulta por JS para saber qué botón mostrar
  @RequestMapping(value = "/partida/{id}/revancha/estado", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> estado(
    @PathVariable("id") Long id,
    HttpSession session
  ) {
    Long usuarioId = (Long) session.getAttribute(ID_USUARIO);

    if (usuarioId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    try {
      String salaRevancha = servicioPartida.obtenerSalaRevancha(id, usuarioId);
      Partida partida = servicioPartida.buscarPartidaPorId(id);

      Map<String, Object> cuerpo = new HashMap<>();
      cuerpo.put("codigoSala", partida.getSala().getCodigo());
      cuerpo.put("esHost", servicioPartida.esHost(id, usuarioId));
      cuerpo.put("salaRevanchaCodigo", salaRevancha);

      return ResponseEntity.ok(cuerpo);
    } catch (IllegalArgumentException excepcion) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @RequestMapping(value = "/partida/{id}/revancha", method = RequestMethod.POST)
  public ModelAndView crearRevancha(@PathVariable("id") Long id, HttpSession session) {
    Long usuarioId = (Long) session.getAttribute(ID_USUARIO);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(usuarioId);

    try {
      String codigoRevancha = servicioPartida.crearRevancha(id, usuario);
      Partida partida = servicioPartida.buscarPartidaPorId(id);

      notificadorSala.rondaActualizada(partida.getSala().getCodigo(), id, EVENTO_REVANCHA_CREADA);

      return new ModelAndView(REDIRECT_SALAS + codigoRevancha);
    } catch (IllegalStateException excepcion) {
      return new ModelAndView(REDIRECT_FIN_PREFIJO + id + REDIRECT_FIN_SUFIJO);
    }
  }

  @RequestMapping(value = "/partida/{id}/revancha/unirse", method = RequestMethod.POST)
  public ModelAndView unirseARevancha(@PathVariable("id") Long id, HttpSession session) {
    Long usuarioId = (Long) session.getAttribute(ID_USUARIO);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      String codigo = servicioPartida.unirseARevancha(id, usuarioId);

      return new ModelAndView(REDIRECT_SALAS + codigo);
    } catch (IllegalStateException | IllegalArgumentException excepcion) {
      return new ModelAndView(REDIRECT_FIN_PREFIJO + id + REDIRECT_FIN_SUFIJO);
    }
  }
}
