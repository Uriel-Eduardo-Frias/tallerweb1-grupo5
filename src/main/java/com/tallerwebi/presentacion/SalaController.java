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

  @RequestMapping(path = "/salas", method = RequestMethod.GET)
  public ModelAndView listarSalas() {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Sala sala = servicioSala.crearSala(
            "TRV-1234",
            "Trivia del viernes",
            host
    );

    List<Sala> salas = new ArrayList<>();
    salas.add(sala);

    Map<String, Object> modelo = new ModelMap();
    modelo.put("salas", salas);

    return new ModelAndView("salas-lista", modelo);
  }

  @RequestMapping(path = "/salas/unirse", method = RequestMethod.POST)
  public ModelAndView unirse(
    @RequestParam("codigo") String codigo,
    @RequestParam("invitado") String invitado
  ) {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Sala sala = servicioSala.crearSala(
            codigo,
            "Trivia del viernes",
            host
    );

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
}
