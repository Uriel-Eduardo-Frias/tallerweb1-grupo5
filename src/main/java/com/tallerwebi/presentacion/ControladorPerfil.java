package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.modelo.EstadisticaUsuario;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPerfil {

  private static final int VICTORIAS_PARA_NIVEL_TRES = 2;
  private static final int VICTORIAS_PARA_NIVEL_DOS = 1;
  private static final int NIVEL_INICIAL = 1;
  private static final int NIVEL_INTERMEDIO = 2;
  private static final int NIVEL_AVANZADO = 3;

  private final ServicioUsuario servicioUsuario;
  private final ServicioEstadistica servicioEstadistica;

  @Autowired
  public ControladorPerfil(
    ServicioUsuario servicioUsuario,
    ServicioEstadistica servicioEstadistica
  ) {
    this.servicioUsuario = servicioUsuario;
    this.servicioEstadistica = servicioEstadistica;
  }

  @RequestMapping(path = "/perfil", method = RequestMethod.GET)
  public ModelAndView verMiPerfil(HttpServletRequest request) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");

    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(idUsuario);
    EstadisticaUsuario estadistica = servicioEstadistica.obtenerEstadisticaUsuario(idUsuario);

    int partidasGanadas = (estadistica != null) ? estadistica.getPartidasGanadas() : 0;
    int nivel = calcularNivel(partidasGanadas);

    if (usuario != null && usuario.getPerfil() != null) {
      usuario.getPerfil().setNivel(nivel);
    }

    Map<String, Object> modelo = new ModelMap();
    modelo.put("usuario", usuario);
    modelo.put("perfil", usuario != null ? usuario.getPerfil() : null);
    modelo.put("estadistica", estadistica);
    modelo.put("nivel", nivel);

    return new ModelAndView("perfil", modelo);
  }

  @RequestMapping(path = "/perfil/{id}", method = RequestMethod.GET)
  public ModelAndView verPerfilPublic(@PathVariable("id") Long id) {
    Usuario usuarioBuscado = servicioUsuario.buscarUsuarioPorId(id);

    if (usuarioBuscado == null) {
      return new ModelAndView("redirect:/buscar-jugadores");
    }

    EstadisticaUsuario estadistica = servicioEstadistica.obtenerEstadisticaUsuario(id);

    int partidasGanadas = (estadistica != null) ? estadistica.getPartidasGanadas() : 0;
    int nivel = calcularNivel(partidasGanadas);

    if (usuarioBuscado.getPerfil() != null) {
      usuarioBuscado.getPerfil().setNivel(nivel);
    }

    Map<String, Object> modelo = new ModelMap();
    modelo.put("usuario", usuarioBuscado);
    modelo.put("perfil", usuarioBuscado.getPerfil());
    modelo.put("estadistica", estadistica);
    modelo.put("nivel", nivel);

    return new ModelAndView("perfil-publico", modelo);
  }

  @RequestMapping(path = "/perfil/actualizar", method = RequestMethod.POST)
  public ModelAndView actualizarPerfil(
    @RequestParam(value = "biografia", required = false, defaultValue = "") String biografia,
    @RequestParam(value = "avatarUrl", required = false, defaultValue = "") String avatarUrl,
    HttpServletRequest request
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario != null) {
      servicioUsuario.actualizarPerfil(idUsuario, biografia, avatarUrl);
    }

    return new ModelAndView("redirect:/perfil");
  }

  private int calcularNivel(int partidasGanadas) {
    if (partidasGanadas >= VICTORIAS_PARA_NIVEL_TRES) {
      return NIVEL_AVANZADO;
    }
    if (partidasGanadas == VICTORIAS_PARA_NIVEL_DOS) {
      return NIVEL_INTERMEDIO;
    }
    return NIVEL_INICIAL;
  }
}
