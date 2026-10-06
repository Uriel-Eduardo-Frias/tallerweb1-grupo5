package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.ServicioPartida;
import com.tallerwebi.dominio.ServicioUsuario;
import com.tallerwebi.dominio.Usuario;
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
    Long usuarioId = (Long) session.getAttribute("ID_USUARIO");

    if (usuarioId == null) {
      return new ModelAndView("redirect:/login");
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(usuarioId);

    Partida partida = servicioPartida.iniciarPartida(codigoSala, usuario);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("partida", partida);

    return new ModelAndView("partida", modelo);
  }

  @RequestMapping(path = "/partidas/{codigoSala}/finalizar", method = RequestMethod.POST)
  public ModelAndView finalizarPartida(
    @PathVariable("codigoSala") String codigoSala,
    HttpSession session
  ) {
    Long usuarioId = (Long) session.getAttribute("ID_USUARIO");

    if (usuarioId == null) {
      return new ModelAndView("redirect:/login");
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(usuarioId);

    Partida partida = servicioPartida.finalizarPartida(codigoSala, usuario);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("partida", partida);

    return new ModelAndView("resultado", modelo);
  }
}
