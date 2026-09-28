package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Categoria;
import com.tallerwebi.dominio.Opcion;
import com.tallerwebi.dominio.Pregunta;
import com.tallerwebi.dominio.ServicioPartida;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/partida")
public class ControladorPartida {

    private final ServicioPartida servicioPartida;

    public ControladorPartida(ServicioPartida servicioPartida) {
        this.servicioPartida = servicioPartida;
    }


    @GetMapping("/votacion")
    public String mostrarPantallaVotacion(
            @RequestParam(name = "ronda", defaultValue = "1") Integer numeroDeRonda,
            @RequestParam(name = "puntaje", defaultValue = "0") Integer puntajeActual,
            Model modelo) {

        Map<Long, Categoria> mapaDeCategorias = servicioPartida.obtenerCategorias();

        modelo.addAttribute("numeroDeRonda", numeroDeRonda);
        modelo.addAttribute("puntajeActual", puntajeActual);
        modelo.addAttribute("mapaDeCategorias", mapaDeCategorias);

        return "votacion";
    }


    @PostMapping("/iniciar-pregunta")
    public String iniciarPregunta(
            @RequestParam(name = "categoriaId") Long identificadorCategoria,
            @RequestParam(name = "ronda", defaultValue = "1") Integer numeroDeRonda,
            @RequestParam(name = "puntaje", defaultValue = "0") Integer puntajeActual,
            Model modelo) {

        Pregunta preguntaSeleccionada = servicioPartida.obtenerPreguntaPorCategoria(identificadorCategoria);

        modelo.addAttribute("pregunta", preguntaSeleccionada);
        modelo.addAttribute("numeroDeRonda", numeroDeRonda);
        modelo.addAttribute("puntajeActual", puntajeActual);
        modelo.addAttribute("categoriaId", identificadorCategoria);

        return "pregunta";
    }


    @PostMapping("/responder")
    public String procesarRespuesta(
            @RequestParam(name = "categoriaId") Long identificadorCategoria,
            @RequestParam(name = "opcionId") Long identificadorOpcionElegida,
            @RequestParam(name = "ronda", defaultValue = "1") Integer numeroDeRonda,
            @RequestParam(name = "puntaje", defaultValue = "0") Integer puntajeActual,
            Model modelo) {

        Pregunta preguntaActual = servicioPartida.obtenerPreguntaPorCategoria(identificadorCategoria);

        boolean laRespuestaFueCorrecta = false;
        String textoDeLaRespuestaCorrecta = "";

        for (Opcion opcion : preguntaActual.getOpciones()) {
            if (opcion.isEsCorrecta()) {
                textoDeLaRespuestaCorrecta = opcion.getTexto();
            }

            if (opcion.getId().equals(identificadorOpcionElegida) && opcion.isEsCorrecta()) {
                laRespuestaFueCorrecta = true;
                puntajeActual += 10;
            }
        }

        modelo.addAttribute("pregunta", preguntaActual);
        modelo.addAttribute("categoriaId", identificadorCategoria);
        modelo.addAttribute("numeroDeRonda", numeroDeRonda);
        modelo.addAttribute("puntajeActual", puntajeActual);
        modelo.addAttribute("yaRespondio", true);
        modelo.addAttribute("esCorrecta", laRespuestaFueCorrecta);
        modelo.addAttribute("textoCorrecto", textoDeLaRespuestaCorrecta);

        return "pregunta";
    }


    @PostMapping("/siguiente-ronda")
    public String pasarASiguienteRonda(
            @RequestParam(name = "ronda", defaultValue = "1") Integer numeroDeRonda,
            @RequestParam(name = "puntaje", defaultValue = "0") Integer puntajeActual) {

        if (numeroDeRonda < 5) {
            return "redirect:/partida/votacion?ronda=" + (numeroDeRonda + 1) + "&puntaje=" + puntajeActual;
        }

        // Si completó las 5 rondas, reinicia el juego
        return "redirect:/partida/votacion?ronda=1&puntaje=0";
    }
}