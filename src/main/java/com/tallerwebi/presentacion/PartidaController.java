package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import com.tallerwebi.dominio.modelo.Usuario;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PartidaController {

  private static final String ID_USUARIO = "ID_USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";

  private final ServicioPartida servicioPartida;
  private final ServicioUsuario servicioUsuario;

  @Autowired
  public PartidaController(ServicioPartida servicioPartida, ServicioUsuario servicioUsuario) {
    this.servicioPartida = servicioPartida;
    this.servicioUsuario = servicioUsuario;
  }

  @RequestMapping(path = "/partidas/{codigoSala}/iniciar", method = RequestMethod.POST)
  public ModelAndView iniciarPartida(
    @PathVariable("codigoSala") String codigoSala,
    HttpSession session
  ) {
    Long usuarioId = (Long) session.getAttribute(ID_USUARIO);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(usuarioId);

    Partida partida = servicioPartida.iniciarPartida(codigoSala, usuario);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("partida", partida);

    return new ModelAndView("redirect:/partida/" + partida.getId() + "/ronda");
  }

  @RequestMapping(path = "/partidas/{codigoSala}/finalizar", method = RequestMethod.POST)
  public ModelAndView finalizarPartida(
    @PathVariable("codigoSala") String codigoSala,
    HttpSession session
  ) {
    Long usuarioId = (Long) session.getAttribute(ID_USUARIO);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(usuarioId);

    Partida partida = servicioPartida.finalizarPartida(codigoSala, usuario);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("partida", partida);

    return new ModelAndView("resultado", modelo);
  }

  @RequestMapping(path = "/partida/{id}/ronda", method = RequestMethod.GET)
  public ModelAndView mostrarRonda(@PathVariable("id") Long id, HttpSession session) {
    Long usuarioId = (Long) session.getAttribute(ID_USUARIO);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(usuarioId);

    Partida partida = servicioPartida.buscarPartidaPorId(id);

    boolean todosListos = servicioPartida.estanTodosListos(id);

    Map<String, Object> modelo = new ModelMap();

    modelo.put("partida", partida);
    modelo.put("usuarioActual", usuario);
    modelo.put("todosListos", todosListos);

    return new ModelAndView("partida-votacion", modelo);
  }

  @RequestMapping(path = "/partida/{id}/listo", method = RequestMethod.POST)
  public ModelAndView marcarListo(@PathVariable("id") Long id, HttpSession session) {
    Long usuarioId = (Long) session.getAttribute(ID_USUARIO);

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(usuarioId);

    servicioPartida.marcarJugadorListo(id, usuario);

    return new ModelAndView("redirect:/partida/" + id + "/ronda");
  }
}
