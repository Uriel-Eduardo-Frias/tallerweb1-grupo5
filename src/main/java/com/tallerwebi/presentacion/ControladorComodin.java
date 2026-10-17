package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicio.ServicioComodin;
import com.tallerwebi.presentacion.dto.Comodin5050DTO;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/partida/{id}/comodin")
public class ControladorComodin {

    private final ServicioComodin servicioComodin;

    @Autowired
    public ControladorComodin(ServicioComodin servicioComodin) {
        this.servicioComodin = servicioComodin;
    }

    @PostMapping("/tiempo-extra")
    @ResponseBody
    public ResponseEntity<?> tiempoExtra(@PathVariable("id") Long partidaId, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("ID_USUARIO");
        if (usuarioId == null) {
            return ResponseEntity.status(401).build();
        }

        int segundos = servicioComodin.aplicarTiempoExtra(partidaId, usuarioId);
        return ResponseEntity.ok(Map.of("segundosExtra", segundos));
    }

    @PostMapping("/50-50")
    @ResponseBody
    public ResponseEntity<Comodin5050DTO> cincuentaCincuenta(
            @PathVariable("id") Long partidaId,
            HttpSession session
    ) {
        Long usuarioId = (Long) session.getAttribute("ID_USUARIO");
        if (usuarioId == null) {
            return ResponseEntity.status(401).build();
        }

        Comodin5050DTO resultado = servicioComodin.aplicarCincuentaCincuenta(partidaId, usuarioId);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/pasar-pregunta")
    public String pasarPregunta(@PathVariable("id") Long partidaId, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("ID_USUARIO");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        servicioComodin.aplicarPasarPregunta(partidaId, usuarioId);
        return "redirect:/partida/" + partidaId + "/pregunta";
    }

    @PostMapping("/doble-chance")
    @ResponseBody
    public ResponseEntity<?> dobleChance(@PathVariable("id") Long partidaId, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("ID_USUARIO");
        if (usuarioId == null) {
            return ResponseEntity.status(401).build();
        }

        servicioComodin.activarDobleChance(partidaId, usuarioId);
        return ResponseEntity.ok(Map.of("activado", true));
    }
}