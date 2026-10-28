package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.enumeration.EstadoRonda;
import com.tallerwebi.dominio.enumeration.EstadoSala;
import com.tallerwebi.dominio.excepcion.PartidaNoEncontradaException;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.PartidaRonda;
import com.tallerwebi.dominio.modelo.ResultadoPartida;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.ServicioLogro;
import com.tallerwebi.dominio.servicio.impl.ServicioResultadoPartidaImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioResultadoPartidaImplTest {

  private static final Long PARTIDA_ID = 1L;

  private RepositorioPartida repositorioPartida;
  private RepositorioPartidaJugador repositorioPartidaJugador;
  private NotificadorSala notificadorSala;
  private ServicioResultadoPartidaImpl servicio;
  private ServicioLogro servicioLogro;

  private Partida partida;

  @BeforeEach
  void setUp() {
    repositorioPartida = mock(RepositorioPartida.class);
    repositorioPartidaJugador = mock(RepositorioPartidaJugador.class);
    notificadorSala = mock(NotificadorSala.class);
    servicioLogro = mock(ServicioLogro.class);
    servicio =
      new ServicioResultadoPartidaImpl(
        repositorioPartida,
        repositorioPartidaJugador,
        notificadorSala,
        servicioLogro
      );

    // Deep stubs: evita tener que importar/armar Sala, Usuario, etc.
    partida = mock(Partida.class, RETURNS_DEEP_STUBS);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);
  }

  // ---------- cerrarPartida ----------

  @Test
  void cerrarPartidaLanzaExcepcionSiLaPartidaNoExiste() {
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(null);

    assertThrows(PartidaNoEncontradaException.class, () -> servicio.cerrarPartida(PARTIDA_ID));
  }

  @Test
  void cerrarPartidaEsIdempotenteSiYaEstabaFinalizada() {
    when(partida.getEstado()).thenReturn(EstadoPartida.FINALIZADA);

    servicio.cerrarPartida(PARTIDA_ID);

    verify(repositorioPartidaJugador, never()).obtenerPorPartida(any());
    verifyNoInteractions(notificadorSala);
  }

  @Test
  void cerrarPartidaMarcaLaPartidaLaSalaYLaRondaComoFinalizadasYNotifica() {
    when(partida.getEstado()).thenReturn(null);
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID)).thenReturn(new ArrayList<>());
    PartidaRonda ronda = mock(PartidaRonda.class);
    when(ronda.getEstado()).thenReturn(EstadoRonda.PREGUNTA_EN_CURSO);
    when(partida.obtenerRondaActual()).thenReturn(ronda);

    servicio.cerrarPartida(PARTIDA_ID);

    verify(partida).setEstado(EstadoPartida.FINALIZADA);
    verify(partida).setFechaFin(any(LocalDateTime.class));
    verify(partida.getSala()).setEstado(EstadoSala.FINALIZADA);
    verify(ronda).setEstado(EstadoRonda.FINALIZADA);
    verify(ronda).setFechaFin(any(LocalDateTime.class));
    verify(notificadorSala).rondaActualizada(any(), any(), eq("PARTIDA_FINALIZADA"));
  }

  @Test
  void cerrarPartidaNoTocaLaRondaSiYaEstabaFinalizada() {
    when(partida.getEstado()).thenReturn(null);
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID)).thenReturn(new ArrayList<>());
    PartidaRonda ronda = mock(PartidaRonda.class);
    when(ronda.getEstado()).thenReturn(EstadoRonda.FINALIZADA);
    when(partida.obtenerRondaActual()).thenReturn(ronda);

    servicio.cerrarPartida(PARTIDA_ID);

    verify(ronda, never()).setEstado(any(EstadoRonda.class));
    verify(ronda, never()).setFechaFin(any(LocalDateTime.class));
  }

  @Test
  void cerrarPartidaFuncionaSiNoHayRondaActual() {
    when(partida.getEstado()).thenReturn(null);
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID)).thenReturn(new ArrayList<>());
    when(partida.obtenerRondaActual()).thenReturn(null);

    assertDoesNotThrow(() -> servicio.cerrarPartida(PARTIDA_ID));

    verify(partida).setEstado(EstadoPartida.FINALIZADA);
  }

  @Test
  void cerrarPartidaAsignaPosicionesOrdenandoPorPuntajeLuegoAciertosLuegoRacha() {
    when(partida.getEstado()).thenReturn(null);
    PartidaJugador primero = jugador(1L, "ana", 200, 1, 1, null);
    PartidaJugador segundo = jugador(2L, "beto", 100, 5, 1, null);
    PartidaJugador tercero = jugador(3L, "caro", 100, 3, 9, null);
    PartidaJugador cuarto = jugador(4L, "dani", 100, 3, 2, null);
    // desordenados a propósito
    List<PartidaJugador> jugadores = List.of(cuarto, primero, tercero, segundo);
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID)).thenReturn(jugadores);

    servicio.cerrarPartida(PARTIDA_ID);

    assertEquals(Integer.valueOf(1), primero.getPosicionFinal());
    assertEquals(Integer.valueOf(2), segundo.getPosicionFinal());
    assertEquals(Integer.valueOf(3), tercero.getPosicionFinal());
    assertEquals(Integer.valueOf(4), cuarto.getPosicionFinal());
  }

  @Test
  void cerrarPartidaDaLaMismaPosicionALosEmpatadosYSaltaElSiguiente() {
    when(partida.getEstado()).thenReturn(null);
    PartidaJugador a = jugador(1L, "ana", 100, 5, 3, null);
    PartidaJugador b = jugador(2L, "beto", 100, 5, 3, null);
    PartidaJugador c = jugador(3L, "caro", 50, 2, 1, null);
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID)).thenReturn(List.of(c, a, b));

    servicio.cerrarPartida(PARTIDA_ID);

    assertEquals(Integer.valueOf(1), a.getPosicionFinal());
    assertEquals(Integer.valueOf(1), b.getPosicionFinal());
    assertEquals(Integer.valueOf(3), c.getPosicionFinal()); // 1, 1, 3
  }

  // ---------- obtenerResultado ----------

  @Test
  void obtenerResultadoLanzaExcepcionSiLaPartidaNoExiste() {
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(null);

    assertThrows(
      PartidaNoEncontradaException.class,
      () -> servicio.obtenerResultado(PARTIDA_ID, 1L)
    );
  }

  @Test
  void obtenerResultadoLanzaExcepcionSiLaPartidaNoFinalizo() {
    when(partida.getEstado()).thenReturn(null);

    assertThrows(IllegalStateException.class, () -> servicio.obtenerResultado(PARTIDA_ID, 1L));
  }

  @Test
  void obtenerResultadoLanzaExcepcionSiElUsuarioNoParticipo() {
    when(partida.getEstado()).thenReturn(EstadoPartida.FINALIZADA);
    PartidaJugador ana = jugador(1L, "ana", 100, 1, 1, 1);
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID)).thenReturn(List.of(ana));

    assertThrows(IllegalArgumentException.class, () -> servicio.obtenerResultado(PARTIDA_ID, 99L));
  }

  @Test
  void obtenerResultadoArmaElRankingConPosicionesYaCalculadas() {
    when(partida.getEstado()).thenReturn(EstadoPartida.FINALIZADA);
    when(partida.getFechaInicio()).thenReturn(LocalDateTime.of(2026, 1, 1, 10, 0, 0));
    when(partida.getFechaFin()).thenReturn(LocalDateTime.of(2026, 1, 1, 10, 12, 30));
    when(partida.getRondaActual()).thenReturn(5);
    when(partida.getTotalRondas()).thenReturn(5);

    PartidaJugador segundo = jugador(2L, "beto", 50, 2, 1, 2);
    PartidaJugador primero = jugador(1L, "ana", 100, 4, 3, 1);
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID))
      .thenReturn(List.of(segundo, primero));

    ResultadoPartida resultado = servicio.obtenerResultado(PARTIDA_ID, 1L);

    assertNotNull(resultado);
    // TODO: asserts con los getters reales de ResultadoPartida / JugadorResultado:
    //  - ranking.get(0) es "ana", posición 1, esUsuarioActual = true
    //  - ranking.get(1) es "beto", posición 2, esUsuarioActual = false
    //  - duración = "12 min 30 s"
    //  - rondaActual = 5, totalRondas = 5
    // Las posiciones que ya estaban cargadas no se recalculan:
    verify(primero, never()).setPosicionFinal(any());
    verify(segundo, never()).setPosicionFinal(any());
  }

  @Test
  void obtenerResultadoCompletaLasPosicionesFaltantesDePartidasViejas() {
    when(partida.getEstado()).thenReturn(EstadoPartida.FINALIZADA);

    PartidaJugador mejor = jugador(1L, "ana", 100, 4, 3, null); // posicionFinal nula
    PartidaJugador peor = jugador(2L, "beto", 50, 2, 1, null);
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID)).thenReturn(List.of(peor, mejor));

    ResultadoPartida resultado = servicio.obtenerResultado(PARTIDA_ID, 2L);

    assertNotNull(resultado);
    assertEquals(Integer.valueOf(1), mejor.getPosicionFinal());
    assertEquals(Integer.valueOf(2), peor.getPosicionFinal());
  }

  @Test
  void obtenerResultadoSinFechasNoFallaYDevuelveResultado() {
    when(partida.getEstado()).thenReturn(EstadoPartida.FINALIZADA);
    when(partida.getFechaInicio()).thenReturn(null);
    when(partida.getFechaFin()).thenReturn(null);
    PartidaJugador ana = jugador(1L, "ana", 100, 4, 3, 1);
    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID))
      .thenReturn(Collections.singletonList(ana));

    assertNotNull(servicio.obtenerResultado(PARTIDA_ID, 1L));
  }

  // ---------- helpers ----------

  /**
   * Jugador mock que recuerda su posicionFinal (para poder verificar lo que asigna el servicio).
   */
  private PartidaJugador jugador(
    Long usuarioId,
    String username,
    int puntaje,
    int aciertos,
    int racha,
    Integer posicionInicial
  ) {
    PartidaJugador j = mock(PartidaJugador.class, RETURNS_DEEP_STUBS);
    AtomicReference<Integer> posicion = new AtomicReference<>(posicionInicial);

    when(j.getUsuario().getId()).thenReturn(usuarioId);
    when(j.getUsuario().getUsername()).thenReturn(username);
    when(j.getPuntajeAcumulado()).thenReturn(puntaje);
    when(j.getAciertos()).thenReturn(aciertos);
    when(j.getErrores()).thenReturn(0);
    when(j.getMejorRachaPartida()).thenReturn(racha);

    when(j.getPosicionFinal()).thenAnswer(inv -> posicion.get());
    doAnswer(inv -> {
        posicion.set(inv.getArgument(0));
        return null;
      })
      .when(j)
      .setPosicionFinal(any());

    return j;
  }
}
