package com.tallerwebi.presentacion;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioPartida;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import java.util.Map;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

class RevanchaControllerTest {

  private static final Long PARTIDA_ID = 1L;
  private static final Long USUARIO_ID = 10L;
  private static final String CODIGO_SALA_ANTERIOR = "VIEJA1";
  private static final String CODIGO_REVANCHA = "NUEVA1";

  private ServicioPartida servicioPartida;
  private ServicioUsuario servicioUsuario;
  private NotificadorSala notificadorSala;

  private RevanchaController controlador;
  private MockHttpSession sesionConUsuario;
  private MockHttpSession sesionSinUsuario;

  @BeforeEach
  void preparar() {
    servicioPartida = mock(ServicioPartida.class);
    servicioUsuario = mock(ServicioUsuario.class);
    notificadorSala = mock(NotificadorSala.class);

    controlador = new RevanchaController(servicioPartida, servicioUsuario, notificadorSala);

    sesionConUsuario = new MockHttpSession();
    sesionConUsuario.setAttribute("ID_USUARIO", USUARIO_ID);

    sesionSinUsuario = new MockHttpSession();
  }

  @Test
  void estadoDevuelve401SiNoHayUsuarioEnSesion() {
    ResponseEntity<Map<String, Object>> respuesta = controlador.estado(
      PARTIDA_ID,
      sesionSinUsuario
    );

    assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
    verifyNoInteractions(servicioPartida);
  }

  @Test
  void estadoDevuelveDatosParaPintarElBotonCuandoYaHayRevancha() {
    when(servicioPartida.obtenerSalaRevancha(PARTIDA_ID, USUARIO_ID)).thenReturn(CODIGO_REVANCHA);
    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partidaConSala());
    when(servicioPartida.esHost(PARTIDA_ID, USUARIO_ID)).thenReturn(false);

    ResponseEntity<Map<String, Object>> respuesta = controlador.estado(
      PARTIDA_ID,
      sesionConUsuario
    );

    assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    Map<String, Object> cuerpo = respuesta.getBody();
    assertNotNull(cuerpo);
    assertEquals(CODIGO_SALA_ANTERIOR, cuerpo.get("codigoSala"));
    assertEquals(false, cuerpo.get("esHost"));
    assertEquals(CODIGO_REVANCHA, cuerpo.get("salaRevanchaCodigo"));
  }

  @Test
  void estadoIndicaQueEsHostYQueTodaviaNoHayRevancha() {
    when(servicioPartida.obtenerSalaRevancha(PARTIDA_ID, USUARIO_ID)).thenReturn(null);
    when(servicioPartida.buscarPartidaPorId(PARTIDA_ID)).thenReturn(partidaConSala());
    when(servicioPartida.esHost(PARTIDA_ID, USUARIO_ID)).thenReturn(true);

    ResponseEntity<Map<String, Object>> respuesta = controlador.estado(
      PARTIDA_ID,
      sesionConUsuario
    );

    Map<String, Object> cuerpo = respuesta.getBody();
    assertNotNull(cuerpo);
    assertEquals(true, cuerpo.get("esHost"));
    assertTrue(cuerpo.containsKey("salaRevanchaCodigo"));
    assertNull(cuerpo.get("salaRevanchaCodigo"));
  }

  @Test
  void estadoDevuelve403SiElUsuarioNoParticipoEnLaPartida() {
    when(servicioPartida.obtenerSalaRevancha(PARTIDA_ID, USUARIO_ID))
      .thenThrow(new IllegalArgumentException("El usuario no participó en esta partida"));

    ResponseEntity<Map<String, Object>> respuesta = controlador.estado(
      PARTIDA_ID,
      sesionConUsuario
    );

    assertEquals(HttpStatus.FORBIDDEN, respuesta.getStatusCode());
  }

  // ---------- POST /partida/{id}/revancha ----------

  @Test
  void crearRevanchaRedirigeAlLoginSiNoHayUsuarioEnSesion() {
    ModelAndView vista = controlador.crearRevancha(PARTIDA_ID, sesionSinUsuario);

    assertEquals("redirect:/login", vista.getViewName());
    verifyNoInteractions(servicioPartida, notificadorSala);
  }

  @Test
  void crearRevanchaRedirigeALaSalaNuevaYNotificaALosDemasJugadores() {
    Usuario usuario = mock(Usuario.class);
    when(servicioUsuario.buscarUsuarioPorId(USUARIO_ID)).thenReturn(usuario);
    when(servicioPartida.crearRevancha(PARTIDA_ID, usuario)).thenReturn(CODIGO_REVANCHA);
    ModelAndView vista = controlador.crearRevancha(PARTIDA_ID, sesionConUsuario);

    assertEquals("redirect:/salas/" + CODIGO_REVANCHA, vista.getViewName());
    verify(notificadorSala).rondaActualizada(CODIGO_SALA_ANTERIOR, PARTIDA_ID, "REVANCHA_CREADA");
  }

  @Test
  void crearRevanchaVuelveALaPantallaFinalYNoNotificaSiElServicioRechaza() {
    Usuario usuario = mock(Usuario.class);
    when(servicioUsuario.buscarUsuarioPorId(USUARIO_ID)).thenReturn(usuario);
    when(servicioPartida.crearRevancha(PARTIDA_ID, usuario))
      .thenThrow(new IllegalStateException("La partida todavía no finalizó"));

    ModelAndView vista = controlador.crearRevancha(PARTIDA_ID, sesionConUsuario);

    assertEquals("redirect:/partida/" + PARTIDA_ID + "/fin", vista.getViewName());
    verify(notificadorSala, never()).rondaActualizada(anyString(), anyLong(), anyString());
  }

  // ---------- POST /partida/{id}/revancha/unirse ----------

  @Test
  void unirseRedirigeAlLoginSiNoHayUsuarioEnSesion() {
    ModelAndView vista = controlador.unirseARevancha(PARTIDA_ID, sesionSinUsuario);

    assertEquals("redirect:/login", vista.getViewName());
    verifyNoInteractions(servicioPartida);
  }

  @Test
  void unirseRedirigeALaSalaDeLaRevancha() {
    when(servicioPartida.unirseARevancha(PARTIDA_ID, USUARIO_ID)).thenReturn(CODIGO_REVANCHA);

    ModelAndView vista = controlador.unirseARevancha(PARTIDA_ID, sesionConUsuario);

    assertEquals("redirect:/salas/" + CODIGO_REVANCHA, vista.getViewName());
  }

  @Test
  void unirseVuelveALaPantallaFinalSiTodaviaNoHayRevancha() {
    when(servicioPartida.unirseARevancha(PARTIDA_ID, USUARIO_ID))
      .thenThrow(new IllegalStateException("Todavía no hay una revancha creada"));

    ModelAndView vista = controlador.unirseARevancha(PARTIDA_ID, sesionConUsuario);

    assertEquals("redirect:/partida/" + PARTIDA_ID + "/fin", vista.getViewName());
  }

  @Test
  void unirseVuelveALaPantallaFinalSiElUsuarioNoParticipo() {
    when(servicioPartida.unirseARevancha(PARTIDA_ID, USUARIO_ID))
      .thenThrow(new IllegalArgumentException("El usuario no participó en esta partida"));

    ModelAndView vista = controlador.unirseARevancha(PARTIDA_ID, sesionConUsuario);

    assertEquals("redirect:/partida/" + PARTIDA_ID + "/fin", vista.getViewName());
  }

  // ---------- helpers ----------

  private Partida partidaConSala() {
    Sala sala = mock(Sala.class);
    when(sala.getCodigo()).thenReturn(CODIGO_SALA_ANTERIOR);

    Partida partida = mock(Partida.class);
    when(partida.getSala()).thenReturn(sala);

    return partida;
  }
}
