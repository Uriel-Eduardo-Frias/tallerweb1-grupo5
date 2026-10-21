package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorEstadisticas {

  private static final int TAMANIO_LEADERBOARD = 10;
  private ServicioEstadistica servicioEstadistica;

  @Autowired
  public ControladorEstadisticas(ServicioEstadistica servicioEstadistica) {
    this.servicioEstadistica = servicioEstadistica;
  }

  @RequestMapping(path = "/estadisticas", method = RequestMethod.GET)
  public ModelAndView verEstadisticas(HttpServletRequest request) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");

    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }

    Map<String, Object> modelo = new ModelMap();
    modelo.put("estadistica", servicioEstadistica.obtenerEstadisticaUsuario(idUsuario));
    modelo.put("leaderboard", servicioEstadistica.obtenerLeaderBoard(TAMANIO_LEADERBOARD));

    return new ModelAndView("estadisticas", modelo);
  }
}
