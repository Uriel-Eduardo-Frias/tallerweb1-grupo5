package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorHomeTest {

  private ControladorHome controladorHome;
  private ModelAndView mav;

  @BeforeEach
  public void init() {
    this.controladorHome = new ControladorHome();
  }

  @Test
  public void queAlIrAlHomeDevuelvaLaVistaDelHome() {
    // ejecucion
    whenElUsuarioVaAlHome();

    // validacion
    thenSeMuestraLaVista("home");
  }

  @Test
  public void siElUsuarioEscogeBuscarJugadoresQueDevuelvaLaVistaDeBuscarJugadores() {
    // ejecuccion
    whenElUsuarioEscogeBuscarJugadores();

    // validacion
    thenSeMuestraLaVista("buscar-jugadores");
  }

  // when
  private void whenElUsuarioVaAlHome() {
    this.mav = this.controladorHome.irAlHome();
  }

  private void whenElUsuarioEscogeBuscarJugadores() {
    this.mav = this.controladorHome.buscarJugadores();
  }

  // then
  private void thenSeMuestraLaVista(String vistaEsperada) {
    assertThat(this.mav.getViewName(), equalTo(vistaEsperada));
  }
}
