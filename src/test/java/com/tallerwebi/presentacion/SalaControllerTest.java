package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SalaControllerTest {

  private AlmacenEnMemoria almacen;
  private ServicioSala servicioSala;
  private SalaController salaController;

  @BeforeEach
  void setUp() {
    almacen = new AlmacenEnMemoria();
    servicioSala = new ServicioSalaIm(almacen);
    salaController = new SalaController(servicioSala); // ajustalo a tu constructor
  }

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

  @Test
  public void deberiaMostrarErrorCuandoLaSalaNoEstaEnEspera() {
    Usuario host = new Usuario();
    host.setId(1L);
    host.setUsername("Juan");

    Usuario invitado = new Usuario();
    invitado.setId(2L);
    invitado.setUsername("Ana");

    Sala sala = servicioSala.crearSala("Trivia del viernes", host);
    sala.setEstado(EstadoSala.EN_CURSO);
    almacen.getUsuarios().put(2L, invitado);

    ModelAndView resultado = salaController.unirse(sala.getCodigo(), 2L);

    assertThat(resultado.getViewName(), equalTo("error-unirse"));
    assertThat(
      resultado.getModel().get("mensaje"),
      equalTo("La sala ya no se encuentra en fase de espera o ya comenzó")
    );
  }

  @Test
  public void deberiaMostrarLaSalaCuandoElInvitadoSeUneCorrectamente() {
    Usuario host = new Usuario();
    host.setId(1L);
    host.setUsername("Juan");

    Usuario invitado = new Usuario();
    invitado.setId(2L);
    invitado.setUsername("Ana");

    Sala sala = servicioSala.crearSala("Trivia del viernes", host);
    almacen.getUsuarios().put(2L, invitado);

    ModelAndView resultado = salaController.unirse(sala.getCodigo(), 2L);

    assertThat(resultado.getViewName(), equalTo("salas"));

    Sala mostrada = (Sala) resultado.getModel().get("sala");
    assertThat(mostrada, equalTo(sala));
    assertThat(sala.getJugadores(), hasSize(2));

    SalaJugador participacionInvitado = sala.getJugadores().get(1);
    assertThat(participacionInvitado.getUsuario(), equalTo(invitado));
    assertThat(participacionInvitado.isEsAnfitrion(), is(false));
  }

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
  public void deberiaCrearUnaSalaConCodigoGeneradoYAgregarAlHost() {
    ModelAndView resultado = salaController.crearSala("Trivia del viernes", "Juan");

    assertThat(resultado.getViewName(), equalTo("sala-detalle"));

    Sala sala = (Sala) resultado.getModel().get("sala");

    assertThat(sala, notNullValue());
    assertThat(sala.getCodigo(), matchesPattern("TRV-[ABCDEFGHJKLMNPQRSTUVWXYZ23456789]{4}"));
    assertThat(sala.getNombre(), equalTo("Trivia del viernes"));
    assertThat(sala.getHost().getUsername(), equalTo("Juan"));

    assertThat(sala.getJugadores(), hasSize(1));

    SalaJugador participacionHost = sala.getJugadores().get(0);
    assertThat(participacionHost.getUsuario(), equalTo(sala.getHost()));
    assertThat(participacionHost.getUsuario().getUsername(), equalTo("Juan"));
    assertThat(participacionHost.isEsAnfitrion(), is(true));
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

  @Test
  public void deberiaMostrarErrorCuandoLaSalaEstaLlena() {
    Usuario host = new Usuario();
    host.setId(1L);
    host.setUsername("Juan");

    Usuario invitado = new Usuario();
    invitado.setId(2L);
    invitado.setUsername("Ana");

    Sala sala = servicioSala.crearSala("Trivia del viernes", host);
    sala.setMaxJugadores(1);
    almacen.getUsuarios().put(2L, invitado);

    ModelAndView resultado = salaController.unirse(sala.getCodigo(), 2L);

    assertThat(resultado.getViewName(), equalTo("error-unirse"));
    assertThat(resultado.getModel().get("mensaje"), equalTo("Sala llena"));
    assertThat(sala.getJugadores(), hasSize(1));
  }

  @Test
  public void deberiaLanzarExcepcionAlVerUnaSalaInexistente() {
    assertThrows(SalaNoEncontradaException.class, () -> salaController.verSala("TRV-XXXX"));
  }
}
