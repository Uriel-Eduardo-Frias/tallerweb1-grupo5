package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SalaController {

  private ServicioSala servicioSala;
  private ServicioUsuario servicioUsuario;
  private static final String ATRIBUTO_CREAR_SALA_DTO = "crearSalaDTO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String ATRIBUTO_ID_USUARIO = "ID_USUARIO";

  @Autowired
  public SalaController(ServicioSala servicioSala, ServicioUsuario servicioUsuario) {
    this.servicioSala = servicioSala;
    this.servicioUsuario = servicioUsuario;
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
    @ModelAttribute(ATRIBUTO_CREAR_SALA_DTO) CrearSalaDTO form,
    BindingResult result,
    HttpSession session
  ) {
    Long usuarioId = (Long) session.getAttribute(ATRIBUTO_ID_USUARIO);
    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario host = servicioUsuario.buscarUsuarioPorId(usuarioId);
    if (host == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Map<String, Object> modelo = new ModelMap();

    if (result.hasErrors()) {
      modelo.put(ATRIBUTO_CREAR_SALA_DTO, form);
      return new ModelAndView("crear-sala", modelo);
    }

    try {
      Sala sala = servicioSala.crearSala(
        form.getNombre(),
        host,
        form.getMaxJugadores(),
        form.getTotalRondas(),
        form.getModoJuego(),
        form.isEsPrivada()
      );

      return new ModelAndView("redirect:/salas/" + sala.getCodigo());
    } catch (Exception ex) {
      modelo.put(ATRIBUTO_CREAR_SALA_DTO, form);
      modelo.put("error", ex.getMessage());
      return new ModelAndView("crear-sala", modelo);
    }
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
  public ModelAndView unirse(@PathVariable("codigo") String codigo, HttpSession session) {
    Long usuarioId = (Long) session.getAttribute(ATRIBUTO_ID_USUARIO);
    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      Sala sala = servicioSala.unirse(codigo, usuarioId);

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

  @RequestMapping(path = "/salas/crear", method = RequestMethod.GET)
  public ModelAndView mostrarFormularioCrearSala(HttpSession session) {
    if (session.getAttribute(ATRIBUTO_ID_USUARIO) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Map<String, Object> modelo = new ModelMap();
    modelo.put(ATRIBUTO_CREAR_SALA_DTO, new CrearSalaDTO());
    modelo.put("modos", ModoJuego.values());

    return new ModelAndView("crear-sala", modelo);
  }
}
