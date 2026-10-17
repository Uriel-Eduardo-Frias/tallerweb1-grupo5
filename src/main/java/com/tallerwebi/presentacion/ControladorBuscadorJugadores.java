package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorBuscadorJugadores {

  private ServicioUsuario servicioUsuario;

  @Autowired
  public ControladorBuscadorJugadores(ServicioUsuario servicioUsuario) {
    this.servicioUsuario = servicioUsuario;
  }

  @RequestMapping(path = "/buscar-jugadores", method = RequestMethod.GET)
  public ModelAndView buscarJugadores(
    @RequestParam(name = "q", required = false) String terminoBusqueda,
    HttpServletRequest request
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }

    Map<String, Object> modelo = new ModelMap();
    List<Usuario> resultados = servicioUsuario.buscarUsuariosPorNombre(terminoBusqueda);

    modelo.put("jugadores", resultados);
    modelo.put("terminoBusqueda", terminoBusqueda != null ? terminoBusqueda : "");

    return new ModelAndView("buscar-jugadores", modelo);
  }
}
