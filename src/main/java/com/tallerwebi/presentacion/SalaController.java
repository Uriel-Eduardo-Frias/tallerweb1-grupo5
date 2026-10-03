package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
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
public class SalaController {

  private ServicioSala servicioSala;

  @Autowired
  public SalaController(ServicioSala servicioSala) {
    this.servicioSala = servicioSala;
  }

  /* código nuevo que va a empezar a tener sentido */
  /* mostrar las salas disponibles */
  @RequestMapping(path = "/salas", method = RequestMethod.GET)
  public ModelAndView listarSalas() {
    List<Sala> salas = servicioSala.listarSalas();

    Map<String, Object> modelo = new ModelMap();
    modelo.put("salas", salas);

    return new ModelAndView("salas-lista", modelo);
  }

  // Crea la sala; el servicio genera su código automáticamente
  @RequestMapping(path = "/salas/crear", method = RequestMethod.POST)
  public ModelAndView crearSala(
    @RequestParam("nombre") String nombre,
    @RequestParam("host") String nombreHost
  ) {
    Usuario host = new Usuario();
    host.setUsername(nombreHost);

    Sala sala = servicioSala.crearSala(nombre, host);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("sala", sala);

    return new ModelAndView("sala-detalle", modelo);
  }

  // Busca y muestra una sala existente
  @RequestMapping(path = "/salas/{codigo}", method = RequestMethod.GET)
  public ModelAndView verSala(@PathVariable("codigo") String codigo) {
    Sala sala = servicioSala.buscarPorCodigo(codigo);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("sala", sala);

    return new ModelAndView("sala-detalle", modelo);
  }

  @RequestMapping(path = "/salas/{codigo}/unirse", method = RequestMethod.POST)
  public ModelAndView unirse(@PathVariable("codigo") String codigo, HttpServletRequest request) {
    Usuario usuario = (Usuario) request.getSession().getAttribute("USUARIO");

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    try {
      Sala sala = servicioSala.unirse(codigo, usuario.getId());

      Map<String, Object> modelo = new ModelMap();
      modelo.put("sala", sala);
      modelo.put("codigo", codigo);
      return new ModelAndView("salas", modelo);
    } catch (SalaLlenaException e) {
      return vistaErrorUnirse("Sala llena");
    } catch (IllegalStateException e) {
      return vistaErrorUnirse(e.getMessage());
    }
  }

  private ModelAndView vistaErrorUnirse(String mensaje) {
    Map<String, Object> modelo = new ModelMap();
    modelo.put("mensaje", mensaje);
    modelo.put("salas", servicioSala.listarSalas());
    return new ModelAndView("error-unirse", modelo);
  }

  /* Mostrar el formulario para crear una sala  */
  @RequestMapping(path = "/salas/crear", method = RequestMethod.GET)
  public ModelAndView mostrarFormularioCrearSala() {
    return new ModelAndView("sala-formulario");
  }
}
