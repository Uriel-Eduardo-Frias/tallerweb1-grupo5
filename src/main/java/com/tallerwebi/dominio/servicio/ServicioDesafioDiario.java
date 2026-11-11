package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.DesafioDiario;
import com.tallerwebi.dominio.modelo.IntentoDesafio;
import com.tallerwebi.dominio.modelo.RespuestaDesafio;
import java.util.List;

public interface ServicioDesafioDiario {
  DesafioDiario obtenerDesafioDeHoy();

  IntentoDesafio iniciarOContinuarIntento(Long usuarioId);

  RespuestaDesafio responder(Long usuarioId, Long preguntaId, Long opcionId);

  IntentoDesafio obtenerIntentoDeHoy(Long usuarioId);

  List<IntentoDesafio> obtenerRankingDeHoy();

  List<IntentoDesafio> obtenerHistorial(Long usuarioId);
}
