package com.tallerwebi.dominio;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SalaControllerTest {

  @Test
  public void deberiaCrearUnaSala() {}


  @Test
  public void deberiaMostrarLaVistaDeSala() {
    SalaControllerTest controlador = new SalaControllerTest();

    ModelAndView resultado = controlador.crearSala();

    assertThat(resultado.getViewName(), equalTo("sala"));
  }
}
