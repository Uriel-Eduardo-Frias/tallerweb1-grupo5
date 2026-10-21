package com.tallerwebi.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.modelo.EstadisticaUsuario;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.servlet.ModelAndView;

public class ControladorEstadisticaTest {

  private static final Long ID_USUARIO = 7L;

  private ServicioEstadistica servicioEstadistica;
  private HttpServletRequest request;
  private HttpSession session;

  private ControladorEstadisticas controlador;

  @BeforeEach
  void preparar() {
    servicioEstadistica = mock(ServicioEstadistica.class);
    request = mock(HttpServletRequest.class);
    session = mock(HttpSession.class);

    controlador = new ControladorEstadisticas(servicioEstadistica);
    when(request.getSession()).thenReturn(session);
  }

  @Test
  void sinSesionRedirigeAlLogin() {
    when(session.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView vista = controlador.verEstadisticas(request);

    assertEquals("redirect:/login", vista.getViewName());
    verify(servicioEstadistica, never()).obtenerEstadisticaUsuario(anyLong());
    verify(servicioEstadistica, never()).obtenerLeaderBoard(anyInt());
  }

  @Test
  void conSesionMuestraLaVistaConLaEstadisticaYElLeaderboard() {
    EstadisticaUsuario estadistica = new EstadisticaUsuario();
    List<EstadisticaUsuario> leaderboard = List.of(new EstadisticaUsuario());

    when(session.getAttribute("ID_USUARIO")).thenReturn(ID_USUARIO);
    when(servicioEstadistica.obtenerEstadisticaUsuario(ID_USUARIO)).thenReturn(estadistica);
    when(servicioEstadistica.obtenerLeaderBoard(10)).thenReturn(leaderboard);

    ModelAndView vista = controlador.verEstadisticas(request);

    assertEquals("estadisticas", vista.getViewName());
    assertSame(estadistica, vista.getModel().get("estadistica"));
    assertSame(leaderboard, vista.getModel().get("leaderboard"));
  }
}
