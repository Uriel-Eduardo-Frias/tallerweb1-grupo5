package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enumeration.TipoLogro;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logro {

  private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private final TipoLogro tipoLogro;
  private final LocalDateTime fechaDesbloqueo;

  public Logro(TipoLogro tipoLogro, LocalDateTime fechaDesbloqueo) {
    this.tipoLogro = tipoLogro;
    this.fechaDesbloqueo = fechaDesbloqueo;
  }

  public String getCodigo() {
    return tipoLogro.name();
  }

  public String getDescripcion() {
    return tipoLogro.getDescripcion();
  }

  public String getNombre() {
    return tipoLogro.getNombre();
  }

  public boolean isDesbloqueado() {
    return fechaDesbloqueo != null;
  }

  public String getFechaFormateada() {
    return fechaDesbloqueo == null ? "" : fechaDesbloqueo.format(FORMATO_FECHA);
  }
}
