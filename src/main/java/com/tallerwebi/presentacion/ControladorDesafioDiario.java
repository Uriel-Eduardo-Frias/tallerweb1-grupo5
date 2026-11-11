package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.excepcion.DesafioDiarioException;
import com.tallerwebi.dominio.modelo.DesafioDiario;
import com.tallerwebi.dominio.modelo.IntentoDesafio;
import com.tallerwebi.dominio.modelo.PreguntaDesafio;
import com.tallerwebi.dominio.modelo.RespuestaDesafio;
import com.tallerwebi.dominio.servicio.ServicioDesafioDiario;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/desafio-diario")
public class ControladorDesafioDiario {

  private static final String REDIRECCION_LOGIN = "redirect:/login";
  private static final String REDIRECCION_DESAFIO = "redirect:/desafio-diario";

  private final ServicioDesafioDiario servicioDesafio;

  @Autowired
  public ControladorDesafioDiario(ServicioDesafioDiario servicioDesafio) {
    this.servicioDesafio = servicioDesafio;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView mostrarDesafio(HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return crearRedireccionLogin();
    }

    DesafioDiario desafio = servicioDesafio.obtenerDesafioDeHoy();
    IntentoDesafio intento = servicioDesafio.iniciarOContinuarIntento(usuarioId);

    if (intento.estaFinalizado()) {
      return crearVistaResultado(desafio, intento);
    }

    return crearVistaPregunta(desafio, intento, null);
  }

  @RequestMapping(value = "/responder", method = RequestMethod.POST)
  public ModelAndView responder(
    @RequestParam("preguntaId") Long preguntaId,
    @RequestParam("opcionId") Long opcionId,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return crearRedireccionLogin();
    }

    try {
      RespuestaDesafio respuesta = servicioDesafio.responder(usuarioId, preguntaId, opcionId);

      IntentoDesafio intento = respuesta.getIntento();

      if (intento.estaFinalizado()) {
        return crearVistaResultado(intento.getDesafio(), intento);
      }

      return new ModelAndView(REDIRECCION_DESAFIO);
    } catch (DesafioDiarioException excepcion) {
      return mostrarPreguntaConError(usuarioId, excepcion.getMessage());
    }
  }

  @RequestMapping(value = "/ranking", method = RequestMethod.GET)
  public ModelAndView mostrarRanking(HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return crearRedireccionLogin();
    }

    Map<String, Object> modelo = new ModelMap();
    modelo.put("desafio", servicioDesafio.obtenerDesafioDeHoy());
    modelo.put("ranking", servicioDesafio.obtenerRankingDeHoy());

    return new ModelAndView("desafio-diario-ranking", modelo);
  }

  @RequestMapping(value = "/historial", method = RequestMethod.GET)
  public ModelAndView mostrarHistorial(HttpSession session) {
    Long usuarioId = obtenerUsuarioId(session);

    if (usuarioId == null) {
      return crearRedireccionLogin();
    }

    Map<String, Object> modelo = new ModelMap();
    modelo.put("historial", servicioDesafio.obtenerHistorial(usuarioId));

    return new ModelAndView("desafio-diario-historial", modelo);
  }

  private ModelAndView crearRedireccionLogin() {
    return new ModelAndView(REDIRECCION_LOGIN);
  }

  private Long obtenerUsuarioId(HttpSession session) {
    Object valor = session.getAttribute("ID_USUARIO");

    if (valor instanceof Long) {
      return (Long) valor;
    }

    if (valor instanceof Integer) {
      return ((Integer) valor).longValue();
    }

    return null;
  }

  private PreguntaDesafio buscarPreguntaPendiente(DesafioDiario desafio, IntentoDesafio intento) {
    for (PreguntaDesafio preguntaDesafio : desafio.getPreguntas()) {
      Long preguntaId = preguntaDesafio.getPregunta().getIdentificador();

      if (!intentoTieneRespuesta(intento, preguntaId)) {
        return preguntaDesafio;
      }
    }

    return null;
  }

  private boolean intentoTieneRespuesta(IntentoDesafio intento, Long preguntaId) {
    for (RespuestaDesafio respuesta : intento.getRespuestas()) {
      Long idRespondido = respuesta.getPregunta().getIdentificador();

      if (idRespondido.equals(preguntaId)) {
        return true;
      }
    }

    return false;
  }

  private ModelAndView crearVistaPregunta(
    DesafioDiario desafio,
    IntentoDesafio intento,
    String mensajeError
  ) {
    PreguntaDesafio preguntaActual = buscarPreguntaPendiente(desafio, intento);

    if (preguntaActual == null) {
      return crearVistaResultado(desafio, intento);
    }

    Map<String, Object> modelo = crearModeloPregunta(desafio, intento, preguntaActual);

    if (mensajeError != null) {
      modelo.put("error", mensajeError);
    }

    return new ModelAndView("desafio-diario", modelo);
  }

  private Map<String, Object> crearModeloPregunta(
    DesafioDiario desafio,
    IntentoDesafio intento,
    PreguntaDesafio preguntaActual
  ) {
    Map<String, Object> modelo = new ModelMap();
    modelo.put("desafio", desafio);
    modelo.put("intento", intento);
    modelo.put("preguntaDesafio", preguntaActual);

    return modelo;
  }

  private ModelAndView crearVistaResultado(DesafioDiario desafio, IntentoDesafio intento) {
    Map<String, Object> modelo = new ModelMap();
    modelo.put("desafio", desafio);
    modelo.put("intento", intento);

    return new ModelAndView("desafio-diario-resultado", modelo);
  }

  private ModelAndView mostrarPreguntaConError(Long usuarioId, String mensajeError) {
    DesafioDiario desafio = servicioDesafio.obtenerDesafioDeHoy();
    IntentoDesafio intento = servicioDesafio.iniciarOContinuarIntento(usuarioId);

    if (intento.estaFinalizado()) {
      ModelAndView vista = crearVistaResultado(desafio, intento);
      vista.addObject("error", mensajeError);
      return vista;
    }

    return crearVistaPregunta(desafio, intento, mensajeError);
  }
}
