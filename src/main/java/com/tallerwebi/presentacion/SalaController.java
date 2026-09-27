package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Sala;
import com.tallerwebi.dominio.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SalaController {

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
    Map<String, Object> modelo = new ModelMap();

    List<Sala> salas = new ArrayList<>();

    Usuario usuario = new Usuario();

    usuario.setUsername("Juan");

    salas.add(new Sala("TRV-1234", "Trivia del viernes", usuario));

    modelo.put("salas", salas);

    return new ModelAndView("salas-lista", modelo);
  }

  @RequestMapping(path = "/salas/unirse", method = RequestMethod.POST)
  public ModelAndView unirse(
    @RequestParam("codigo") String codigo,
    @RequestParam("invitado") String invitado
  ) {
    Map<String, Object> modelo = new ModelMap();

    Usuario host = new Usuario();
    host.setUsername("Juan");

    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);

    Usuario usuarioInvitado = new Usuario();
    usuarioInvitado.setUsername(invitado);

    sala.agregarJugador(usuarioInvitado);

    modelo.put("sala", sala);

    return new ModelAndView("sala-detalle", modelo);
  }
}
