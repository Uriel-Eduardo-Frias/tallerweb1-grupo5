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

  private ServicioSala servicioSala = new ServicioSalaIm();
  private SalaController salaController = new SalaController(servicioSala);

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

  /*
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
*/

  @Test
  public void deberiaMostrarLaSalaEnLaLista() {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Sala creada = servicioSala.crearSala("Trivia del viernes", host);

    ModelAndView resultado = salaController.listarSalas();

    assertThat(resultado.getViewName(), equalTo("salas-lista"));

    List<Sala> salas = (List<Sala>) resultado.getModel().get("salas");

    assertThat(salas, hasSize(1));
    assertThat(salas.get(0).getCodigo(), equalTo(creada.getCodigo()));
    assertThat(salas.get(0).getNombre(), equalTo("Trivia del viernes"));
    assertThat(salas.get(0).getHost().getUsername(), equalTo("Juan"));
  }

  /*
  @Test
  public void deberiaAgregarAlInvitadoALaSalaCuandoElCodigoEsValido() {
    // Given
    String codigo = "TRV-1234";
    String nombreInvitado = "Ana";

    // When
    ModelAndView resultado = salaController.unirse(codigo, nombreInvitado);

    // Then
    assertThat(resultado.getViewName(), equalTo("sala-detalle"));

    Sala sala = (Sala) resultado.getModel().get("sala");

    assertThat(sala, notNullValue());
    assertThat(sala.getCodigo(), equalTo(codigo));
    assertThat(sala.getJugadores(), hasItem(hasProperty("username", equalTo(nombreInvitado))));
    assertThat(sala.getJugadores(), hasSize(2));
    assertThat(resultado.getModel().get("error"), nullValue());
  }
*/
  /*
  @Test
  public void deberiaAgregarAlInvitadoALaSalaExistente() {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Sala creada = servicioSala.crearSala("Trivia del viernes", host);
    String codigo = creada.getCodigo();

    ModelAndView resultado = salaController.unirse(codigo, "Ana");

    assertThat(resultado.getViewName(), equalTo("sala-detalle"));

    Sala sala = (Sala) resultado.getModel().get("sala");

    assertThat(sala, notNullValue());
    assertThat(sala.getCodigo(), equalTo(codigo));
    assertThat(sala.getJugadores(), hasItem(hasProperty("username", equalTo("Ana"))));
    assertThat(sala.getJugadores(), hasSize(2));
    assertThat(resultado.getModel().get("error"), nullValue());
  }
*/

  @Test
  public void deberiaMostrarElFormularioParaCrearUnaSala() {
    // Given
    // El controlador ya está preparado con ServicioSala.

    // When
    ModelAndView resultado = salaController.mostrarFormularioCrearSala();

    // Then
    assertThat(resultado.getViewName(), equalTo("sala-formulario"));
  }

  @Test
  public void deberiaPasarElHostEnLaSalaMostradaAlCrear() {
    // Given
    ServicioSala servicioReal = new ServicioSalaIm();
    SalaController controlador = new SalaController(servicioReal);

    // When
    ModelAndView resultado = controlador.crearSala("TRV-1234", "Juan");

    // Then
    Sala sala = (Sala) resultado.getModel().get("sala");

    assertThat(resultado.getViewName(), equalTo("sala-detalle"));
    assertThat(sala.getJugadores(), hasSize(1));
    assertThat(sala.getJugadores(), hasItem(hasProperty("username", equalTo("Juan"))));
  }

  @Test
  public void deberiaCrearUnaSalaConCodigoGeneradoYAgregarAlHost() {
    ModelAndView resultado = salaController.crearSala("Trivia del viernes", "Juan");

    assertThat(resultado.getViewName(), equalTo("sala-detalle"));

    Sala sala = (Sala) resultado.getModel().get("sala");

    assertThat(sala, notNullValue());
    assertThat(sala.getCodigo(), matchesPattern("TRV-[ABCDEFGHJKLMNPQRSTUVWXYZ23456789]{4}"));
    assertThat(sala.getNombre(), equalTo("Trivia del viernes"));
    assertThat(sala.getHost().getUsername(), equalTo("Juan"));
    assertThat(sala.getJugadores(), hasSize(1));
    assertThat(sala.getJugadores(), hasItem(hasProperty("username", equalTo("Juan"))));
  }

  @Test
  public void deberiaMostrarElDetalleDeLaSalaBuscadaPorCodigo() {
    Usuario host = new Usuario();
    host.setUsername("Juan");

    Sala creada = servicioSala.crearSala("Trivia del viernes", host);

    ModelAndView resultado = salaController.verSala(creada.getCodigo());

    assertThat(resultado.getViewName(), equalTo("sala-detalle"));

    Sala mostrada = (Sala) resultado.getModel().get("sala");
    assertThat(mostrada.getCodigo(), equalTo(creada.getCodigo()));
  }
}
