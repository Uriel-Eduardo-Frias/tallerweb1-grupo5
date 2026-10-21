package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.EstadoRonda;
import com.tallerwebi.dominio.excepcion.PartidaNoEncontradaException;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.PartidaRonda;
import com.tallerwebi.dominio.modelo.VotacionCategoria;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioRespuestaJugador;
import com.tallerwebi.dominio.servicio.ServicioResultadoPartida;
import com.tallerwebi.dominio.servicio.impl.ServicioRondaImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioRondaImplTest {

  private static final Long PARTIDA_ID = 1L;
  private static final Long HOST_ID = 5L;
  private static final Long RONDA_ID = 100L;

  private RepositorioPartida repositorioPartida;
  private RepositorioPartidaJugador repositorioPartidaJugador;
  private RepositorioRespuestaJugador repositorioRespuestaJugador;
  private NotificadorSala notificadorSala;
  private ServicioResultadoPartida servicioResultadoPartida;
  private ServicioRondaImpl servicio;

  private Partida partida;
  private PartidaRonda ronda;

  @BeforeEach
  void setUp() {
    repositorioPartida = mock(RepositorioPartida.class);
    repositorioPartidaJugador = mock(RepositorioPartidaJugador.class);
    repositorioRespuestaJugador = mock(RepositorioRespuestaJugador.class);
    notificadorSala = mock(NotificadorSala.class);
    servicioResultadoPartida = mock(ServicioResultadoPartida.class);

    servicio =
      new ServicioRondaImpl(
        repositorioPartida,
        repositorioPartidaJugador,
        repositorioRespuestaJugador,
        notificadorSala,
        servicioResultadoPartida
      );

    partida = mock(Partida.class, RETURNS_DEEP_STUBS);
    ronda = mock(PartidaRonda.class, RETURNS_DEEP_STUBS);

    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);
    when(partida.obtenerRondaActual()).thenReturn(ronda);
    when(partida.getSala().getHost().getId()).thenReturn(HOST_ID);
    when(ronda.getId()).thenReturn(RONDA_ID);
    when(ronda.getEstado()).thenReturn(EstadoRonda.PREGUNTA_EN_CURSO);
  }

  @Test
  void finalizarRondaLanzaExcepcionSiLaPartidaNoExiste() {
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(null);

    assertThrows(
      PartidaNoEncontradaException.class,
      () -> servicio.finalizarRondaSiTodosRespondieron(PARTIDA_ID)
    );
  }

  @Test
  void finalizarRondaDevuelveFalseSiNoHayRondaActual() {
    when(partida.obtenerRondaActual()).thenReturn(null);

    assertFalse(servicio.finalizarRondaSiTodosRespondieron(PARTIDA_ID));
  }

  @Test
  void finalizarRondaDevuelveFalseSiLaRondaNoEstaEnCurso() {
    when(ronda.getEstado()).thenReturn(EstadoRonda.RESULTADO_PREGUNTA);

    assertFalse(servicio.finalizarRondaSiTodosRespondieron(PARTIDA_ID));
    verifyNoInteractions(notificadorSala);
  }

  @Test
  void finalizarRondaDevuelveFalseSiLaRondaNoTienePregunta() {
    when(ronda.getPregunta()).thenReturn(null);

    assertFalse(servicio.finalizarRondaSiTodosRespondieron(PARTIDA_ID));
    verifyNoInteractions(notificadorSala);
  }

  @Test
  void finalizarRondaDevuelveFalseSiNoHayJugadores() {
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID)).thenReturn(new ArrayList<>());

    assertFalse(servicio.finalizarRondaSiTodosRespondieron(PARTIDA_ID));
    verify(ronda, never()).setEstado(any(EstadoRonda.class));
  }

  @Test
  void finalizarRondaDevuelveFalseSiFaltaQueRespondaAlguno() {
    PartidaJugador j1 = jugador(1L);
    PartidaJugador j2 = jugador(2L);
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID)).thenReturn(List.of(j1, j2));
    when(repositorioRespuestaJugador.existeRespuestaEnRonda(RONDA_ID, 1L)).thenReturn(true);
    when(repositorioRespuestaJugador.existeRespuestaEnRonda(RONDA_ID, 2L)).thenReturn(false);

    assertFalse(servicio.finalizarRondaSiTodosRespondieron(PARTIDA_ID));

    verify(ronda, never()).setEstado(any(EstadoRonda.class));
    verifyNoInteractions(notificadorSala);
  }

  @Test
  void finalizarRondaPasaAResultadoYNotificaSiTodosRespondieron() {
    PartidaJugador j1 = jugador(1L);
    PartidaJugador j2 = jugador(2L);
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID)).thenReturn(List.of(j1, j2));
    when(repositorioRespuestaJugador.existeRespuestaEnRonda(eq(RONDA_ID), any())).thenReturn(true);

    assertTrue(servicio.finalizarRondaSiTodosRespondieron(PARTIDA_ID));

    verify(ronda).setEstado(EstadoRonda.RESULTADO_PREGUNTA);
    verify(notificadorSala).rondaResultado(any(), any());
  }

  @Test
  void avanzarRondaLanzaExcepcionSiLaPartidaNoExiste() {
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(null);

    assertThrows(
      PartidaNoEncontradaException.class,
      () -> servicio.avanzarRonda(PARTIDA_ID, HOST_ID)
    );
  }

  @Test
  void avanzarRondaLanzaExcepcionSiNoEsElAnfitrion() {
    assertThrows(IllegalStateException.class, () -> servicio.avanzarRonda(PARTIDA_ID, 999L));

    verify(ronda, never()).setEstado(any(EstadoRonda.class));
    verifyNoInteractions(notificadorSala);
  }

  @Test
  void avanzarRondaLanzaExcepcionSiNoHayRondaActiva() {
    when(partida.obtenerRondaActual()).thenReturn(null);

    assertThrows(IllegalStateException.class, () -> servicio.avanzarRonda(PARTIDA_ID, HOST_ID));
  }

  @Test
  void avanzarRondaLanzaExcepcionSiLaRondaNoTienePregunta() {
    when(ronda.getPregunta()).thenReturn(null);

    assertThrows(IllegalStateException.class, () -> servicio.avanzarRonda(PARTIDA_ID, HOST_ID));
  }

  @Test
  void avanzarRondaNoHaceNadaSiLaRondaNoEstaEnResultado() {
    when(ronda.getEstado()).thenReturn(EstadoRonda.PREGUNTA_EN_CURSO);

    servicio.avanzarRonda(PARTIDA_ID, HOST_ID);

    verify(ronda, never()).setEstado(any(EstadoRonda.class));
    verifyNoInteractions(servicioResultadoPartida);
    verifyNoInteractions(notificadorSala);
  }

  @Test
  void avanzarRondaFinalizaLaPartidaEnLaUltimaRonda() {
    when(ronda.getEstado()).thenReturn(EstadoRonda.RESULTADO_PREGUNTA);
    when(partida.getRondaActual()).thenReturn(5);
    when(partida.getTotalRondas()).thenReturn(5);
    when(partida.getId()).thenReturn(PARTIDA_ID);

    servicio.avanzarRonda(PARTIDA_ID, HOST_ID);

    verify(ronda).setEstado(EstadoRonda.FINALIZADA);
    verify(servicioResultadoPartida).cerrarPartida(PARTIDA_ID);
    verify(partida, never()).setRondaActual(anyInt());
  }

  @Test
  void avanzarRondaCreaLaSiguienteRondaYReiniciaLaVotacionExistente() {
    when(ronda.getEstado()).thenReturn(EstadoRonda.RESULTADO_PREGUNTA);
    when(partida.getRondaActual()).thenReturn(2);
    when(partida.getTotalRondas()).thenReturn(5);
    VotacionCategoria votacion = mock(VotacionCategoria.class);
    when(partida.getVotacionCategoria()).thenReturn(votacion);

    servicio.avanzarRonda(PARTIDA_ID, HOST_ID);

    verify(ronda).setEstado(EstadoRonda.FINALIZADA);
    verify(partida.getRondas()).add(any(PartidaRonda.class));
    verify(partida).setRondaActual(3);
    verify(votacion).reiniciar();
    verify(partida, never()).iniciarVotacionCategoria();
    verifyNoInteractions(servicioResultadoPartida);
    verify(notificadorSala).rondaActualizada(any(), any(), eq("NUEVA_VOTACION_CATEGORIA"));
  }

  @Test
  void avanzarRondaIniciaUnaVotacionNuevaSiNoExistia() {
    when(ronda.getEstado()).thenReturn(EstadoRonda.RESULTADO_PREGUNTA);
    when(partida.getRondaActual()).thenReturn(1);
    when(partida.getTotalRondas()).thenReturn(5);
    when(partida.getVotacionCategoria()).thenReturn(null);

    servicio.avanzarRonda(PARTIDA_ID, HOST_ID);

    verify(partida).setRondaActual(2);
    verify(partida).iniciarVotacionCategoria();
    verify(notificadorSala).rondaActualizada(any(), any(), eq("NUEVA_VOTACION_CATEGORIA"));
  }

  private PartidaJugador jugador(Long id) {
    PartidaJugador j = mock(PartidaJugador.class);
    when(j.getId()).thenReturn(id);
    return j;
  }
}
