package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Sala;
import com.tallerwebi.dominio.SalaLlenaException;
import com.tallerwebi.dominio.ServicioSala;
import com.tallerwebi.dominio.Usuario;
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

  private final ServicioSala servicioSala;

  @Autowired
  public SalaController(ServicioSala servicioSala) {
    this.servicioSala = servicioSala;
  }

  /*
  @RequestMapping(path = "salas", method = RequestMethod.GET)
  public ModelAndView listarSalas() {
    Map<String, Object> modelo = new ModelMap();

    modelo.put("mensaje", "la lista de salas estará disponible proximamente");

    return new ModelAndView("salas-lista", modelo);
  }
*/

  /*
  @RequestMapping(path = "/salas", method = RequestMethod.GET)
  public ModelAndView listarSalas() {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Sala sala = servicioSala.crearSala("TRV-1234", "Trivia del viernes", host);

    List<Sala> salas = new ArrayList<>();
    salas.add(sala);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("salas", salas);

    return new ModelAndView("salas-lista", modelo);
  }

   */

  /* código nuevo que va a empezar a tener sentido */
  /* mostrar las salas disponibles */
  @RequestMapping(path = "/salas", method = RequestMethod.GET)
  public ModelAndView listarSalas() {
    List<Sala> salas = servicioSala.listarSalas();

    Map<String, Object> modelo = new ModelMap();
    modelo.put("salas", salas);

    return new ModelAndView("salas-lista", modelo);
  }

  /*
  @RequestMapping(path = "/salas/unirse", method = RequestMethod.POST)
  public ModelAndView unirse(
    @RequestParam("codigo") String codigo,
    @RequestParam("invitado") String invitado
  ) {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Sala sala = servicioSala.crearSala(codigo,  host);

    Usuario usuarioInvitado = new Usuario();
    usuarioInvitado.setUsername(invitado);

    Map<String, Object> modelo = new ModelMap();

    try {
      servicioSala.unirse(sala, usuarioInvitado);
      modelo.put("sala", sala);
    } catch (SalaLlenaException e) {
      modelo.put("sala", sala);
      modelo.put("error", e.getMessage());
    }

    return new ModelAndView("sala-detalle", modelo);
  }
*/
  /*
  @RequestMapping(path = "/salas/crear", method = RequestMethod.POST)
  public ModelAndView crearSala(
    @RequestParam("nombre") String nombre,
    @RequestParam("codigo") String codigo,
    @RequestParam("host") String nombreHost
  ) {
    Usuario host = new Usuario();
    host.setUsername(nombreHost);

    Sala sala = servicioSala.crearSala(codigo, nombre, host);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("sala", sala);

    return new ModelAndView("sala-detalle", modelo);
  }

   */

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

  // Une a un invitado a la sala indicada en la URL
  @RequestMapping(path = "/salas/{codigo}/unirse", method = RequestMethod.POST)
  public ModelAndView unirse(
    @PathVariable("codigo") String codigo,
    @RequestParam("invitado") String invitado
  ) {
    Sala sala = servicioSala.buscarPorCodigo(codigo);
    Usuario usuarioInvitado = new Usuario();
    usuarioInvitado.setUsername(invitado);

    Map<String, Object> modelo = new ModelMap();

    try {
      servicioSala.unirse(sala, usuarioInvitado);
    } catch (SalaLlenaException e) {
      modelo.put("error", e.getMessage());
    }

    modelo.put("sala", sala);
    return new ModelAndView("sala-detalle", modelo);
  }

  /* Mostrar el formulario para crear una sala  */
  @RequestMapping(path = "/salas/crear", method = RequestMethod.GET)
  public ModelAndView mostrarFormularioCrearSala() {
    return new ModelAndView("sala-formulario");
  }
}
