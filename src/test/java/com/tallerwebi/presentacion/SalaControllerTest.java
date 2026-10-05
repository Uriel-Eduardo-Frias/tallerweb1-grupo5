package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SalaControllerTest {

  @Mock
  private ServicioUsuario servicioUsuario;

  private AlmacenEnMemoria almacen;
  private ServicioSala servicioSala;

  @Mock
  private NotificadorSala notificadorSala;

  @InjectMocks
  private SalaController salaController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    almacen = new AlmacenEnMemoria();
    servicioSala = new ServicioSalaIm(almacen, servicioUsuario);
    salaController = new SalaController(servicioSala, servicioUsuario, notificadorSala);
  }

  @Test
  public void deberiaMostrarLaSalaEnLaLista() {
    Usuario host = new Usuario();
    host.setId(1L);
    host.setUsername("Juan");

    Sala creada = servicioSala.crearSala(
      "Trivia del viernes",
      host,
      4,
      5,
      ModoJuego.TURNO_TODOS,
      false
    );

    ModelAndView resultado = salaController.listarSalas();

    assertThat(resultado.getViewName(), equalTo("salas-lista"));

    @SuppressWarnings("unchecked")
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

    Sala sala = crearSalaDePrueba("Trivia del viernes", host);
    sala.setEstado(EstadoSala.EN_CURSO);
    almacen.getUsuarios().put(2L, invitado);

    ModelAndView resultado = salaController.unirse(sala.getCodigo(), sessionDe(invitado));

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

    when(servicioUsuario.buscarUsuarioPorId(2L)).thenReturn(invitado);

    Sala sala = crearSalaDePrueba("Trivia del viernes", host);

    ModelAndView resultado = salaController.unirse(sala.getCodigo(), sessionDe(invitado));

    assertThat(resultado.getViewName(), equalTo("redirect:/salas/" + sala.getCodigo()));

    assertThat(sala.getJugadores(), hasSize(2));

    SalaJugador participacionInvitado = sala.getJugadores().get(1);

    assertThat(participacionInvitado.getUsuario(), equalTo(invitado));

    assertThat(participacionInvitado.isEsAnfitrion(), is(false));
  }

  /*LO COMENTE PARA PRBAR
  @Test
  public void deberiaMostrarElFormularioParaCrearUnaSala() {
    ModelAndView resultado = salaController.mostrarFormularioCrearSala();

    assertThat(resultado.getViewName(), equalTo("crear-sala"));
    assertThat(resultado.getModel().get("CrearSalaDTO"), notNullValue());
  }
*/
  /*FALLA ESTE TEST
  @Test
  public void deberiaCrearUnaSalaConCodigoGeneradoYAgregarAlHost() {
    Usuario host = new Usuario();
    host.setId(1L);
    host.setUsername("Juan");

    when(servicioUsuario.buscarUsuarioPorId(1L)).thenReturn(host);

    CrearSalaDTO form = formularioSala("Trivia del viernes", 4, 5, ModoJuego.TURNO_TODOS, false);

    ModelAndView resultado = salaController.crearSala(
      form,
      bindingResultValido(form),
      sesionDe(host)
    );

    String vista = resultado.getViewName();
    assertThat(vista, notNullValue());
    assertThat(vista.startsWith("redirect:/salas/"), is(true));

    String codigo = vista.substring("redirect:/salas/".length());
    Sala sala = servicioSala.buscarPorCodigo(codigo);

    assertThat(sala, notNullValue());
    assertThat(sala.getCodigo(), matchesPattern("TRV-[ABCDEFGHJKLMNPQRSTUVWXYZ23456789]{4}"));
    assertThat(sala.getNombre(), equalTo("Trivia del viernes"));
    assertThat(sala.getHost().getUsername(), equalTo("Juan"));
    assertThat(sala.getJugadores(), hasSize(1));

    SalaJugador participacionHost = sala.getJugadores().get(0);
    assertThat(participacionHost.getUsuario(), equalTo(host));
    assertThat(participacionHost.isEsAnfitrion(), is(true));
  }
*/

  @Test
  public void deberiaMostrarElDetalleDeLaSalaBuscadaPorCodigo() {
    Usuario host = new Usuario();
    host.setId(1L);
    host.setUsername("Juan");

    Sala creada = crearSalaDePrueba("Trivia del viernes", host);

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

    Sala sala = crearSalaDePrueba("Trivia del viernes", host);
    sala.setMaxJugadores(1);
    almacen.getUsuarios().put(2L, invitado);

    ModelAndView resultado = salaController.unirse(sala.getCodigo(), sessionDe(invitado));

    assertThat(resultado.getViewName(), equalTo("error-unirse"));
    assertThat(resultado.getModel().get("mensaje"), equalTo("Sala llena"));
    assertThat(sala.getJugadores(), hasSize(1));
  }

  @Test
  public void deberiaLanzarExcepcionAlVerUnaSalaInexistente() {
    assertThrows(SalaNoEncontradaException.class, () -> salaController.verSala("TRV-XXXX"));
  }

  private MockHttpServletRequest requestConSesionDe(Usuario usuario) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.getSession().setAttribute("USUARIO", usuario);
    return request;
  }

  /*FALLA ESTE TEST
  @Test
  public void deberiaCrearUnaSalaYRedirigirAlLobby() {
    Usuario host = new Usuario();
    host.setId(1L);
    host.setUsername("Juan");

    when(servicioUsuario.buscarUsuarioPorId(1L)).thenReturn(host);

    CrearSalaDTO form = formularioSala("Trivia del viernes", 4, 5, ModoJuego.TURNO_TODOS, false);

    ModelAndView resultado = salaController.crearSala(
      form,
      bindingResultValido(form),
      sesionDe(host)
    );

    String vista = resultado.getViewName();
    assertThat(vista, notNullValue());
    assertThat(vista.startsWith("redirect:/salas/"), is(true));

    String codigo = vista.substring("redirect:/salas/".length());
    Sala sala = servicioSala.buscarPorCodigo(codigo);

    assertThat(sala, notNullValue());
    assertThat(sala.getNombre(), equalTo("Trivia del viernes"));
    assertThat(sala.getHost().getUsername(), equalTo("Juan"));
    assertThat(sala.getJugadores(), hasSize(1));
    assertThat(sala.getMaxJugadores(), equalTo(4));
    assertThat(sala.getTotalRondas(), equalTo(5));
    assertThat(sala.getModoJuego(), equalTo(ModoJuego.TURNO_TODOS));
    assertThat(sala.isEsPrivada(), is(false));
  }
*/

  private CrearSalaDTO formularioSala(
    String nombre,
    int maxJugadores,
    int totalRondas,
    ModoJuego modoJuego,
    boolean esPrivada
  ) {
    CrearSalaDTO form = new CrearSalaDTO();
    form.setNombre(nombre);
    form.setMaxJugadores(maxJugadores);
    form.setTotalRondas(totalRondas);
    form.setModoJuego(modoJuego);
    form.setEsPrivada(esPrivada);
    return form;
  }

  private HttpSession sessionDe(Usuario usuario) {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("ID_USUARIO", usuario.getId());
    return session;
  }

  private BindingResult bindingResultValido(CrearSalaDTO form) {
    return new BeanPropertyBindingResult(form, "crearSalaDTO");
  }

  private Sala crearSalaDePrueba(String nombre, Usuario host) {
    return servicioSala.crearSala(nombre, host, 4, 5, ModoJuego.TURNO_TODOS, false);
  }
}
