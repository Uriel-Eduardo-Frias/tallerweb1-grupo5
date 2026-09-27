package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioUsuario;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

  private ServicioUsuario servicioUsuario;

  @Autowired
  public ControladorPerfil(ServicioUsuario servicioUsuario) {
    this.servicioUsuario = servicioUsuario;
  }

  @RequestMapping(path = "/perfil", method = RequestMethod.GET)
  public ModelAndView verMiPerfil(HttpServletRequest request) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");

    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }

    Usuario usuario = servicioUsuario.buscarUsuarioPorId(idUsuario);
    Map<String, Object> modelo = new ModelMap();
    modelo.put("usuario", usuario);
    modelo.put("perfil", usuario.getPerfil());

    return new ModelAndView("perfil", modelo);
  }

  @RequestMapping(path = "/perfil/{id}", method = RequestMethod.GET)
  public ModelAndView verPerfilPublic(@PathVariable("id") Long id) {
    Usuario usuarioBuscado = servicioUsuario.buscarUsuarioPorId(id);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("usuario", usuarioBuscado);
    modelo.put("perfil", usuarioBuscado.getPerfil());

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
}
