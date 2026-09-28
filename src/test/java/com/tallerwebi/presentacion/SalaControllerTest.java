package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.Sala;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class SalaControllerTest {

  private SalaController salaController = new SalaController();

  @Test
  public void deberiaCrearUnaSala() {}

  /*
  @Test
  public void deberiaMostrarLaVistaDeSala() {
    SalaController controlador = new SalaController();

    ModelAndView salasListas = controlador.listarSalas();

    assertThat(salasListas.getViewName(), equalTo("salas-lista"));
  }

  @Test
  public void deberiaMostrarLaVistaDeSalasConUnMensaje() {
    ModelAndView resultado = salaController.listarSalas();

    assertThat(resultado.getViewName(), equalTo("salas-lista"));

    assertThat(
      resultado.getModel().get("mensaje"),
      equalTo("la lista de salas estará disponible proximamente")
    );
  }
*/

  @Test
  public void deberiaMostrarLaSalaEnLaLista() {
    // When
    ModelAndView resultado = salaController.listarSalas();

    // Then
    assertThat(resultado.getViewName(), equalTo("salas-lista"));

    //al devolver un tipo Object , se debe castear porque java no tiene idea de le estamos pasando una litsa de salas
    List<Sala> salas = (List<Sala>) resultado.getModel().get("salas");

    assertThat(salas, hasSize(1));
    //valido que encuentre el primer código que encuentre y a su vez con los demás atributos , teniendo en cuenta que es una lista iterable
    //estos son datos hardcodeados que hacen que el test corra
    assertThat(salas.get(0).getCodigo(), equalTo("TRV-1234"));
    assertThat(salas.get(0).getNombre(), equalTo("Trivia del viernes"));
    assertThat(salas.get(0).getHost().getUsername(), equalTo("Juan"));
  }

  @Test
  public void deberiaAgregarAlInvitadoALaSalaCuandoElCodigoEsValido() {
    // Given
    String codigoValido = "TRV-1234";
    String invitado = "Ana";

    // When
    ModelAndView resultado = salaController.unirse(codigoValido, invitado);

    // Then
    assertThat(resultado.getViewName(), equalTo("sala-detalle"));

    Sala sala = (Sala) resultado.getModel().get("sala");
    assertThat(sala.getJugadores(), hasItem(hasProperty("username", equalTo(invitado))));
  }
}
