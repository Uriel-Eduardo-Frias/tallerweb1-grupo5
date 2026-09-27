package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.tallerwebi.presentacion.SalaController;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class SalaControllerTest {

  @Test
  public void deberiaCrearUnaSala() {}

  @Test
  public void deberiaMostrarLaVistaDeSala() {
    SalaController controlador = new SalaController();

    ModelAndView salasListas = controlador.listarSalas();

    assertThat(salasListas.getViewName(), equalTo("salas-lista"));
  }
}
