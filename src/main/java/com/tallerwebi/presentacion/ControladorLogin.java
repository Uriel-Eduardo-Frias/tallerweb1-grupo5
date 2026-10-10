package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicio.ServicioUsuario;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorLogin {

  private static final String ATRIBUTO_DATOS_LOGIN = "datosLogin";

  private ServicioUsuario servicioUsuario;

  @Autowired
  public ControladorLogin(ServicioUsuario servicioUsuario) {
    this.servicioUsuario = servicioUsuario;
  }

  // 1. Login usuario:
  @RequestMapping(path = "/login", method = RequestMethod.GET)
  public ModelAndView irALogin() {
    Map<String, Object> modelo = new ModelMap();
    modelo.put(ATRIBUTO_DATOS_LOGIN, new DatosLogin());
    return new ModelAndView("login", modelo);
  }

  @RequestMapping(path = "/validar-login", method = RequestMethod.POST)
  public ModelAndView validarLogin(
    @ModelAttribute(ATRIBUTO_DATOS_LOGIN) DatosLogin datosLogin,
    HttpServletRequest request
  ) {
    Usuario usuarioBuscado = servicioUsuario.autenticarUsuario(
      datosLogin.getUsername(),
      datosLogin.getPassword()
    );
    if (usuarioBuscado != null) {
      request.getSession().setAttribute("ROL", usuarioBuscado.getRol());
      request.getSession().setAttribute("ID_USUARIO", usuarioBuscado.getId());
      request.getSession().setAttribute("USERNAME", usuarioBuscado.getUsername());
      return new ModelAndView("redirect:/home");
    } else {
      Map<String, Object> model = new ModelMap();
      model.put("error", "Usuario o clave incorrecta");
      return new ModelAndView("login", model);
    }
  }

  // 2. Registrar usuario:
  @RequestMapping(path = "/registro", method = RequestMethod.POST)
  public ModelAndView registrarme(@ModelAttribute(ATRIBUTO_DATOS_LOGIN) DatosLogin datosLogin) {
    Map<String, Object> model = new ModelMap();
    try {
      servicioUsuario.registrarUsuario(
        datosLogin.getUsername(),
        datosLogin.getEmail(),
        datosLogin.getPassword(),
        datosLogin.getNombreCompleto()
      );
      return new ModelAndView("redirect:/login");
    } catch (UsuarioExistente e) {
      model.put("error", "El nombre de usuario ya está en uso. Elegí otro.");
      return new ModelAndView("registro", model);
    } catch (Exception e) {
      model.put("error", "Error interno: " + e.toString());
      return new ModelAndView("registro", model);
    }
  }

  @RequestMapping(path = "/registro", method = RequestMethod.GET)
  public ModelAndView nuevoUsuario() {
    Map<String, Object> model = new ModelMap();
    model.put(ATRIBUTO_DATOS_LOGIN, new DatosLogin());
    return new ModelAndView("registro", model);
  }

  @RequestMapping(path = "/logout", method = RequestMethod.GET)
  public ModelAndView logout(HttpServletRequest request) {
    request.getSession().invalidate(); // este metodo sirve para borrar los datos de una session
    return new ModelAndView("redirect:/login");
  }
}
