package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class SalaControllerTest {

  private ServicioSala servicioSala = mock(ServicioSala.class);
  private SalaController salaController = new SalaController(servicioSala);

  @Disabled("Test de prueba vacío para validar al crear una sala")
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
    Usuario host = new Usuario();
    host.setUsername("Juan");
    Sala sala = new Sala("TRV-1234", "Trivia del viernes", host);

    Usuario invitado = new Usuario();
    invitado.setUsername("Ana");

    when(servicioSala.crearSala("TRV-1234", "Trivia del viernes", host)).thenReturn(sala);

    // Then
    ModelAndView resultado = salaController.unirse("TRV-1234", "Ana");

    assertThat(resultado.getViewName(), equalTo("sala-detalle"));
    assertThat(resultado.getModel().get("sala"), equalTo(sala));
    assertThat(resultado.getModel().get("error"), nullValue());
    verify(servicioSala).unirse(sala, invitado);
  }
}
