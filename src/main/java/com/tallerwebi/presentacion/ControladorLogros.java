package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicio.ServicioLogro;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorLogros {

  private final ServicioLogro servicioLogro;

  public ControladorLogros(ServicioLogro servicioLogro) {
    this.servicioLogro = servicioLogro;
  }

  @RequestMapping(path = "/logros", method = RequestMethod.GET)
  public ModelAndView irALogros(HttpServletRequest request) {
    Long usuarioId = obtenerUsuarioId(request);

    if (usuarioId == null) {
      return new ModelAndView("redirect:/login");
    }

    Map<String, Object> modelo = new ModelMap();
    modelo.put("logros", servicioLogro.obtenerLogrosDisponibles(usuarioId));

    return new ModelAndView("logros", modelo);
  }

  private Long obtenerUsuarioId(HttpServletRequest request) {
    Object id = request.getSession().getAttribute("ID_USUARIO");

    return id instanceof Long ? (Long) id : null;
  }
}
