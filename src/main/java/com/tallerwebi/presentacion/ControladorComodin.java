package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicio.ServicioComodin;
import com.tallerwebi.presentacion.dto.Comodin5050DTO;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/partida/{id}/comodin")
public class ControladorComodin {

  private static final String ID_USUARIO = "ID_USUARIO";
  private static final String CLAVE_SEGUNDOS_EXTRA = "segundosExtra";
  private static final String CLAVE_ACTIVADO = "activado";

  private final ServicioComodin servicioComodin;

  @Autowired
  public ControladorComodin(ServicioComodin servicioComodin) {
    this.servicioComodin = servicioComodin;
  }

  @PostMapping("/tiempo-extra")
  @ResponseBody
  public ResponseEntity<Map<String, Integer>> tiempoExtra(
    @PathVariable("id") Long partidaId,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);
    if (usuarioId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    int segundos = servicioComodin.aplicarTiempoExtra(partidaId, usuarioId);
    return ResponseEntity.ok(Map.of(CLAVE_SEGUNDOS_EXTRA, segundos));
  }

  @PostMapping("/50-50")
  @ResponseBody
  public ResponseEntity<Comodin5050DTO> cincuentaCincuenta(
    @PathVariable("id") Long partidaId,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);
    if (usuarioId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Comodin5050DTO resultado = servicioComodin.aplicarCincuentaCincuenta(partidaId, usuarioId);
    return ResponseEntity.ok(resultado);
  }

  @PostMapping("/doble-chance")
  @ResponseBody
  public ResponseEntity<Map<String, Boolean>> dobleChance(
    @PathVariable("id") Long partidaId,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);
    if (usuarioId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    servicioComodin.activarDobleChance(partidaId, usuarioId);
    return ResponseEntity.ok(Map.of(CLAVE_ACTIVADO, Boolean.TRUE));
  }

  @PostMapping("/bloqueo")
  @ResponseBody
  public ResponseEntity<Map<String, Boolean>> bloqueo(
    @PathVariable("id") Long partidaId,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);
    if (usuarioId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    servicioComodin.aplicarBloqueo(partidaId, usuarioId);
    return ResponseEntity.ok(Map.of(CLAVE_ACTIVADO, Boolean.TRUE));
  }

  @PostMapping("/borroso")
  @ResponseBody
  public ResponseEntity<Map<String, Boolean>> borroso(
    @PathVariable("id") Long partidaId,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);
    if (usuarioId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    servicioComodin.aplicarBorroso(partidaId, usuarioId);
    return ResponseEntity.ok(Map.of(CLAVE_ACTIVADO, Boolean.TRUE));
  }

  @PostMapping("/mezclar")
  @ResponseBody
  public ResponseEntity<Map<String, Boolean>> mezclar(
    @PathVariable("id") Long partidaId,
    HttpSession session
  ) {
    Long usuarioId = obtenerUsuarioId(session);
    if (usuarioId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    servicioComodin.aplicarMezclar(partidaId, usuarioId);
    return ResponseEntity.ok(Map.of(CLAVE_ACTIVADO, Boolean.TRUE));
  }

  private Long obtenerUsuarioId(HttpSession session) {
    return (Long) session.getAttribute(ID_USUARIO);
  }
}
