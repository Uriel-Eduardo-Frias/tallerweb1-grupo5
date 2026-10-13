package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.CrearSalaDTO;
import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.ModoJuego;
import com.tallerwebi.dominio.excepcion.SalaLlenaException;
import com.tallerwebi.dominio.excepcion.SalaNoEncontradaException;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioSala;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

public class SalaControllerTest {

  private static final Long USUARIO_ID = 1L;
  private static final String CODIGO_SALA = "TRV-1234";
  private static final String NOMBRE_SALA = "Trivia del viernes";

  private SalaController controladorSala;
  private ServicioSala servicioSalaMock;
  private ServicioUsuario servicioUsuarioMock;
  private NotificadorSala notificadorSalaMock;
  private MockHttpSession sessionFalsa;
  private ModelAndView mav;
  private CrearSalaDTO formularioSala;
  private BindingResult estadoDelFormulario;

  @BeforeEach
  public void init() {
    this.servicioSalaMock = mock(ServicioSala.class);
    this.servicioUsuarioMock = mock(ServicioUsuario.class);
    this.notificadorSalaMock = mock(NotificadorSala.class);
    this.sessionFalsa = new MockHttpSession();

    this.controladorSala =
      new SalaController(this.servicioSalaMock, this.servicioUsuarioMock, this.notificadorSalaMock);
  }

  @Test
  public void queCuandoElUsuarioQuieraVerLasSalasSeMuestreLaVistaDeLista() {
    givenUnaListaDeSalasDisponibles();
    whenElUsuarioListaLasSalas();
    thenSeMuestraLaVista("salas-lista");
    thenLaVistaContieneLaListaDeSalas();
  }

  @Test
  public void queCuandoUnUsuarioCreeUnaSalaValidaSeRedirijaAlLobby() {
    Usuario usuario = givenUnUsuarioAutenticado();
    givenUnFormularioDeSalaValido();
    givenElServicioCreaLaSala(usuario);
    whenElUsuarioCreaLaSala();
    thenSeMuestraLaVista("redirect:/salas/" + CODIGO_SALA);
    thenElControladorUsaElServicioParaCrearLaSala(usuario);
  }

  @Test
  public void queCuandoHayaErroresDeValidacionAlCrearSalaVuelvaAlFormulario() {
    givenUnUsuarioAutenticado();
    givenUnFormularioDeSalaValido();
    givenElFormularioTieneErrores();
    whenElUsuarioCreaLaSala();
    thenSeMuestraLaVista("crear-sala");
    thenElServicioNoCreaNingunaSala();
  }

  @Test
  public void queCuandoUnUsuarioSeUnaCorrectamenteSeRedirijaAlLobbyYSeNotifique() {
    givenUnUsuarioAutenticado();
    Sala sala = givenUnaSalaParaUnirse();
    whenElUsuarioSeUneALaSala();
    thenSeMuestraLaVista("redirect:/salas/" + CODIGO_SALA);
    thenSeNotificaQueElJugadorSeUnio(sala);
  }

  @Test
  public void queCuandoUnUsuarioIntenteUnirseAUnaSalaLlenaMuestreError() {
    givenUnUsuarioAutenticado();
    givenUnaSalaLlenaAlUnirse();
    whenElUsuarioSeUneALaSala();
    thenSeMuestraLaVista("error-unirse");
    thenElMensajeDeErrorEs("Sala llena");
  }

  @Test
  public void queCuandoUnUsuarioBusqueUnaSalaPorCodigoSeMuestreElDetalle() {
    givenUnUsuarioAutenticado();
    givenUnaSalaBuscadaPorCodigo();
    whenElUsuarioVeElDetalleDeLaSala();
    thenSeMuestraLaVista("sala-detalle");
    thenLaVistaContieneLaSalaEnElModelo();
  }

  @Test
  public void queCuandoUnUsuarioBusqueUnaSalaInexistenteLanceExcepcion() {
    givenUnUsuarioAutenticado();
    givenUnaSalaQueNoExiste();
    assertThrows(SalaNoEncontradaException.class, this::whenElUsuarioVeElDetalleDeLaSala);
  }

  @Test
  public void queCuandoUnUsuarioSinSesionQuieraCrearSalaRedirijaAlLogin() {
    givenUnUsuarioSinSesion();
    givenUnFormularioDeSalaValido();
    whenElUsuarioCreaLaSala();
    thenSeMuestraLaVista("redirect:/login");
  }

  private void givenUnaListaDeSalasDisponibles() {
    List<Sala> salas = new ArrayList<>();
    Usuario host = new Usuario();
    host.setUsername("Host1");
    salas.add(new Sala(CODIGO_SALA, "Sala 1", host));
    when(this.servicioSalaMock.listarSalas()).thenReturn(salas);
  }

  private Usuario givenUnUsuarioAutenticado() {
    Usuario usuario = new Usuario();
    usuario.setId(USUARIO_ID);
    usuario.setUsername("Juan");
    this.sessionFalsa.setAttribute("ID_USUARIO", USUARIO_ID);
    when(this.servicioUsuarioMock.buscarUsuarioPorId(USUARIO_ID)).thenReturn(usuario);
    return usuario;
  }

  private void givenUnUsuarioSinSesion() {
    this.sessionFalsa = new MockHttpSession();
  }

  private void givenUnFormularioDeSalaValido() {
    this.formularioSala = new CrearSalaDTO();
    this.formularioSala.setNombre(NOMBRE_SALA);
    this.formularioSala.setMaxJugadores(4);
    this.formularioSala.setTotalRondas(5);
    this.formularioSala.setModoJuego(ModoJuego.TURNO_TODOS);
    this.formularioSala.setEsPrivada(false);
    this.estadoDelFormulario = new BeanPropertyBindingResult(this.formularioSala, "crearSalaDTO");
  }

  private void givenElFormularioTieneErrores() {
    this.estadoDelFormulario.reject("nombre", "Nombre invalido");
  }

  private void givenElServicioCreaLaSala(Usuario host) {
    Sala sala = new Sala(CODIGO_SALA, NOMBRE_SALA, host);
    when(
      this.servicioSalaMock.crearSala(
          this.formularioSala.getNombre(),
          host,
          this.formularioSala.getMaxJugadores(),
          this.formularioSala.getTotalRondas(),
          this.formularioSala.getModoJuego(),
          this.formularioSala.isEsPrivada()
        )
    )
      .thenReturn(sala);
  }

  private Sala givenUnaSalaParaUnirse() {
    Sala sala = new Sala(CODIGO_SALA, NOMBRE_SALA, new Usuario());
    when(this.servicioSalaMock.unirse(CODIGO_SALA, USUARIO_ID)).thenReturn(sala);
    return sala;
  }

  private void givenUnaSalaLlenaAlUnirse() {
    when(this.servicioSalaMock.unirse(CODIGO_SALA, USUARIO_ID))
      .thenThrow(new SalaLlenaException("Sala llena"));
  }

  private void givenUnaSalaBuscadaPorCodigo() {
    Usuario host = new Usuario();
    host.setId(USUARIO_ID);
    Sala sala = new Sala(CODIGO_SALA, NOMBRE_SALA, host);
    when(this.servicioSalaMock.buscarPorCodigo(CODIGO_SALA)).thenReturn(sala);
  }

  private void givenUnaSalaQueNoExiste() {
    when(this.servicioSalaMock.buscarPorCodigo(CODIGO_SALA))
      .thenThrow(new SalaNoEncontradaException("No existe la sala"));
  }

  private void whenElUsuarioListaLasSalas() {
    this.mav = this.controladorSala.listarSalas();
  }

  private void whenElUsuarioCreaLaSala() {
    this.mav =
      this.controladorSala.crearSala(
          this.formularioSala,
          this.estadoDelFormulario,
          this.sessionFalsa
        );
  }

  private void whenElUsuarioSeUneALaSala() {
    this.mav = this.controladorSala.unirse(CODIGO_SALA, this.sessionFalsa);
  }

  private void whenElUsuarioVeElDetalleDeLaSala() {
    this.mav = this.controladorSala.verSala(CODIGO_SALA, this.sessionFalsa);
  }

  private void thenSeMuestraLaVista(String vistaEsperada) {
    assertThat(this.mav.getViewName(), equalTo(vistaEsperada));
  }

  private void thenLaVistaContieneLaListaDeSalas() {
    List<Sala> salasEnModelo = (List<Sala>) this.mav.getModel().get("salas");
    assertThat(salasEnModelo, hasSize(1));
    assertThat(salasEnModelo.get(0).getCodigo(), equalTo(CODIGO_SALA));
  }

  private void thenElControladorUsaElServicioParaCrearLaSala(Usuario host) {
    verify(this.servicioSalaMock, times(1))
      .crearSala(
        this.formularioSala.getNombre(),
        host,
        this.formularioSala.getMaxJugadores(),
        this.formularioSala.getTotalRondas(),
        this.formularioSala.getModoJuego(),
        this.formularioSala.isEsPrivada()
      );
  }

  private void thenElServicioNoCreaNingunaSala() {
    verifyNoInteractions(this.servicioSalaMock);
  }

  private void thenSeNotificaQueElJugadorSeUnio(Sala sala) {
    verify(this.notificadorSalaMock, times(1)).jugadorSeUnio(sala);
  }

  private void thenElMensajeDeErrorEs(String mensajeEsperado) {
    assertThat(this.mav.getModel().get("mensaje"), equalTo(mensajeEsperado));
  }

  private void thenLaVistaContieneLaSalaEnElModelo() {
    Sala salaEnModelo = (Sala) this.mav.getModel().get("sala");
    assertThat(salaEnModelo, notNullValue());
    assertThat(salaEnModelo.getCodigo(), equalTo(CODIGO_SALA));
  }
}
