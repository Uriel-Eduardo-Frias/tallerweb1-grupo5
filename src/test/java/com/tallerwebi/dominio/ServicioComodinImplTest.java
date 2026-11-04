package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.ModoJuego;
import com.tallerwebi.dominio.enumeration.TipoComodin;
import com.tallerwebi.dominio.modelo.Comodin;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.PartidaJugadorComodin;
import com.tallerwebi.dominio.modelo.PartidaRonda;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioComodin;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import com.tallerwebi.dominio.servicio.impl.ServicioComodinImpl;
import com.tallerwebi.presentacion.dto.Comodin5050DTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioComodinImplTest {

  private static final Long PARTIDA_ID = 1L;
  private static final Long USUARIO_ID = 2L;
  private static final Long JUGADOR_ID = 10L;

  private RepositorioComodin repositorioComodin;
  private RepositorioPartida repositorioPartida;
  private RepositorioPartidaJugador repositorioPartidaJugador;
  private ServicioEstadistica servicioEstadistica;
  private NotificadorSala notificadorSala;
  private ServicioComodinImpl servicio;
  private PartidaJugador jugador;

  @BeforeEach
  void setUp() {
    repositorioComodin = mock(RepositorioComodin.class);
    repositorioPartida = mock(RepositorioPartida.class);
    repositorioPartidaJugador = mock(RepositorioPartidaJugador.class);
    servicioEstadistica = mock(ServicioEstadistica.class);
    notificadorSala = mock(NotificadorSala.class);

    servicio =
      new ServicioComodinImpl(
        repositorioComodin,
        repositorioPartida,
        repositorioPartidaJugador,
        servicioEstadistica,
        notificadorSala
      );

    Usuario usuario = mock(Usuario.class);
    when(usuario.getId()).thenReturn(USUARIO_ID);

    jugador = mock(PartidaJugador.class);
    when(jugador.getId()).thenReturn(JUGADOR_ID);
    when(jugador.getUsuario()).thenReturn(usuario);

    when(servicioEstadistica.calcularNivelUsuario(USUARIO_ID)).thenReturn(3);

    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(PARTIDA_ID, USUARIO_ID))
      .thenReturn(jugador);
  }

  private PartidaJugadorComodin comodinConStock(TipoComodin tipo, int cantidad) {
    PartidaJugadorComodin pjc = mock(PartidaJugadorComodin.class);
    when(pjc.getCantidadDisponible()).thenReturn(cantidad);
    when(pjc.getVecesUsado()).thenReturn(0);
    when(repositorioComodin.buscarPorJugadorYTipo(JUGADOR_ID, tipo)).thenReturn(pjc);
    return pjc;
  }

  private PartidaJugador configurarEscenarioAtaque(TipoComodin tipo) {
    Partida partida = mock(Partida.class);
    Sala sala = mock(Sala.class);
    when(sala.getCodigo()).thenReturn("SALA1");
    when(partida.getSala()).thenReturn(sala);
    when(partida.getModoJuego()).thenReturn(ModoJuego.PRIMERO_GANA);
    when(jugador.getPartida()).thenReturn(partida);

    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);

    comodinConStock(tipo, 1);

    PartidaJugador oponente = mock(PartidaJugador.class);
    Usuario usuarioOponente = mock(Usuario.class);
    when(usuarioOponente.getId()).thenReturn(99L);
    when(usuarioOponente.getUsername()).thenReturn("rival");
    when(oponente.getUsuario()).thenReturn(usuarioOponente);

    when(repositorioPartidaJugador.obtenerPorPartida(PARTIDA_ID))
      .thenReturn(List.of(jugador, oponente));

    return oponente;
  }

  @Test
  void jugadorNivel1SoloInicializaUnComodinDeTiempoExtra() {
    when(servicioEstadistica.calcularNivelUsuario(USUARIO_ID)).thenReturn(1);

    servicio.inicializarComodinesParaJugador(jugador);

    verify(repositorioComodin, times(1)).guardar(any(PartidaJugadorComodin.class));
  }

  @Test
  void jugadorNivel2InicializaDosComodines() {
    when(servicioEstadistica.calcularNivelUsuario(USUARIO_ID)).thenReturn(2);

    servicio.inicializarComodinesParaJugador(jugador);

    verify(repositorioComodin, times(2)).guardar(any(PartidaJugadorComodin.class));
  }

  @Test
  void jugadorNivel1NoPuedeUsarDobleChanceNi5050() {
    when(servicioEstadistica.calcularNivelUsuario(USUARIO_ID)).thenReturn(1);

    boolean puedeDobleChance = servicio.tieneComodinDisponible(
      PARTIDA_ID,
      USUARIO_ID,
      TipoComodin.DOBLE_CHANCE
    );
    boolean puede5050 = servicio.tieneComodinDisponible(
      PARTIDA_ID,
      USUARIO_ID,
      TipoComodin.CINCUENTA_CINCUENTA
    );

    assertFalse(puedeDobleChance);
    assertFalse(puede5050);
  }

  @Test
  void inicializarNoCreaNadaSiElJugadorYaTieneLosComodines() {
    when(repositorioComodin.buscarPorJugadorYTipo(eq(JUGADOR_ID), any(TipoComodin.class)))
      .thenReturn(mock(PartidaJugadorComodin.class));

    servicio.inicializarComodinesParaJugador(jugador);

    verify(repositorioComodin, never()).guardar(any(PartidaJugadorComodin.class));
    verify(repositorioComodin, never()).guardarComodinMaestro(any(Comodin.class));
  }

  @Test
  void inicializarCreaComodinMaestroYAsignacionSiNoExistenNinguno() {
    when(repositorioComodin.buscarPorJugadorYTipo(eq(JUGADOR_ID), any(TipoComodin.class)))
      .thenReturn(null);
    when(repositorioComodin.buscarComodinPorCodigo(any(TipoComodin.class))).thenReturn(null);

    servicio.inicializarComodinesParaJugador(jugador);

    verify(repositorioComodin, times(3)).guardarComodinMaestro(any(Comodin.class));
    verify(repositorioComodin, times(3)).guardar(any(PartidaJugadorComodin.class));
  }

  @Test
  void inicializarReutilizaElComodinMaestroSiYaExiste() {
    when(repositorioComodin.buscarPorJugadorYTipo(eq(JUGADOR_ID), any(TipoComodin.class)))
      .thenReturn(null);
    when(repositorioComodin.buscarComodinPorCodigo(any(TipoComodin.class)))
      .thenReturn(mock(Comodin.class));

    servicio.inicializarComodinesParaJugador(jugador);

    verify(repositorioComodin, never()).guardarComodinMaestro(any(Comodin.class));
    verify(repositorioComodin, times(3)).guardar(any(PartidaJugadorComodin.class));
  }

  @Test
  void obtenerComodinesDeJugadorInicializaYDevuelveLaLista() {
    when(repositorioComodin.buscarPorJugadorYTipo(eq(JUGADOR_ID), any(TipoComodin.class)))
      .thenReturn(mock(PartidaJugadorComodin.class));
    List<PartidaJugadorComodin> esperados = new ArrayList<>();
    esperados.add(mock(PartidaJugadorComodin.class));
    when(repositorioComodin.obtenerPorPartidaJugador(JUGADOR_ID)).thenReturn(esperados);

    List<PartidaJugadorComodin> resultado = servicio.obtenerComodinesDeJugador(
      PARTIDA_ID,
      USUARIO_ID
    );

    assertSame(esperados, resultado);
  }

  @Test
  void obtenerComodinesDeJugadorLanzaExcepcionSiElJugadorNoParticipa() {
    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(PARTIDA_ID, USUARIO_ID))
      .thenReturn(null);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicio.obtenerComodinesDeJugador(PARTIDA_ID, USUARIO_ID)
    );
  }

  @Test
  void tieneComodinDisponibleDevuelveTrueSiHayStock() {
    comodinConStock(TipoComodin.TIEMPO_EXTRA, 1);

    assertTrue(servicio.tieneComodinDisponible(PARTIDA_ID, USUARIO_ID, TipoComodin.TIEMPO_EXTRA));
  }

  @Test
  void tieneComodinDisponibleDevuelveFalseSiNoHayStock() {
    comodinConStock(TipoComodin.TIEMPO_EXTRA, 0);

    assertFalse(servicio.tieneComodinDisponible(PARTIDA_ID, USUARIO_ID, TipoComodin.TIEMPO_EXTRA));
  }

  @Test
  void tieneComodinDisponibleDevuelveFalseSiElJugadorNoParticipa() {
    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(PARTIDA_ID, USUARIO_ID))
      .thenReturn(null);

    assertFalse(servicio.tieneComodinDisponible(PARTIDA_ID, USUARIO_ID, TipoComodin.TIEMPO_EXTRA));
  }

  @Test
  void aplicarTiempoExtraConsumeElComodinYDevuelveDiezSegundos() {
    PartidaJugadorComodin pjc = comodinConStock(TipoComodin.TIEMPO_EXTRA, 1);

    int segundos = servicio.aplicarTiempoExtra(PARTIDA_ID, USUARIO_ID);

    assertEquals(10, segundos);
    verify(pjc).setCantidadDisponible(0);
    verify(pjc).setVecesUsado(1);
    verify(repositorioComodin).actualizar(pjc);
  }

  @Test
  void aplicarTiempoExtraLanzaExcepcionSiYaSeUso() {
    PartidaJugadorComodin pjc = comodinConStock(TipoComodin.TIEMPO_EXTRA, 0);

    assertThrows(
      IllegalStateException.class,
      () -> servicio.aplicarTiempoExtra(PARTIDA_ID, USUARIO_ID)
    );

    verify(repositorioComodin, never()).actualizar(pjc);
  }

  @Test
  void aplicarCincuentaCincuentaConsumeElComodinYDescartaDosIncorrectas() {
    PartidaJugadorComodin pjc = comodinConStock(TipoComodin.CINCUENTA_CINCUENTA, 1);

    Opcion correcta = opcion(1L, true);
    Opcion incorrecta1 = opcion(2L, false);
    Opcion incorrecta2 = opcion(3L, false);
    Opcion sinDato = opcion(4L, null);
    List<Opcion> opciones = new ArrayList<>(List.of(correcta, incorrecta1, incorrecta2, sinDato));

    Partida partida = mock(Partida.class);
    PartidaRonda ronda = mock(PartidaRonda.class, RETURNS_DEEP_STUBS);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);
    when(partida.obtenerRondaActual()).thenReturn(ronda);
    when(ronda.getPregunta().getOpciones()).thenReturn(opciones);

    Comodin5050DTO dto = servicio.aplicarCincuentaCincuenta(PARTIDA_ID, USUARIO_ID);

    assertNotNull(dto);
    verify(pjc).setCantidadDisponible(0);
    verify(repositorioComodin).actualizar(pjc);
  }

  @Test
  void aplicarCincuentaCincuentaLanzaExcepcionSiNoHayStock() {
    comodinConStock(TipoComodin.CINCUENTA_CINCUENTA, 0);

    assertThrows(
      IllegalStateException.class,
      () -> servicio.aplicarCincuentaCincuenta(PARTIDA_ID, USUARIO_ID)
    );

    verify(repositorioPartida, never()).obtenerPorIdConRondas(any());
  }

  @Test
  void activarDobleChanceGuardaLaRondaEnElJugadorYConsumeElStock() {
    PartidaJugadorComodin pjc = comodinConStock(TipoComodin.DOBLE_CHANCE, 1);

    Partida partida = mock(Partida.class);
    PartidaRonda ronda = mock(PartidaRonda.class);
    when(ronda.getId()).thenReturn(77L);
    when(partida.obtenerRondaActual()).thenReturn(ronda);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);

    servicio.activarDobleChance(PARTIDA_ID, USUARIO_ID);

    verify(pjc).setCantidadDisponible(0);
    verify(pjc).setVecesUsado(1);
    verify(repositorioComodin).actualizar(pjc);
    verify(jugador).setDobleChanceRondaId(77L);
    verify(repositorioPartidaJugador).guardar(jugador);
  }

  @Test
  void activarDobleChanceLanzaExcepcionSiNoHayStock() {
    comodinConStock(TipoComodin.DOBLE_CHANCE, 0);

    assertThrows(
      IllegalStateException.class,
      () -> servicio.activarDobleChance(PARTIDA_ID, USUARIO_ID)
    );

    verify(repositorioPartidaJugador, never()).guardar(any(PartidaJugador.class));
  }

  @Test
  void jugadorNivel1EnModoPrimeroGanaSoloInicializaBorroso() {
    Partida partida = mock(Partida.class);
    when(partida.getModoJuego()).thenReturn(ModoJuego.PRIMERO_GANA);
    when(jugador.getPartida()).thenReturn(partida);
    when(servicioEstadistica.calcularNivelUsuario(USUARIO_ID)).thenReturn(1);

    servicio.inicializarComodinesParaJugador(jugador);

    verify(repositorioComodin, times(1)).guardar(any(PartidaJugadorComodin.class));
  }

  @Test
  void jugadorNivel2EnModoPrimeroGanaInicializaDosComodines() {
    Partida partida = mock(Partida.class);
    when(partida.getModoJuego()).thenReturn(ModoJuego.PRIMERO_GANA);
    when(jugador.getPartida()).thenReturn(partida);
    when(servicioEstadistica.calcularNivelUsuario(USUARIO_ID)).thenReturn(2);

    servicio.inicializarComodinesParaJugador(jugador);

    verify(repositorioComodin, times(2)).guardar(any(PartidaJugadorComodin.class));
  }

  @Test
  void jugadorNivel3EnModoPrimeroGanaInicializaTresComodines() {
    Partida partida = mock(Partida.class);
    when(partida.getModoJuego()).thenReturn(ModoJuego.PRIMERO_GANA);
    when(jugador.getPartida()).thenReturn(partida);
    when(servicioEstadistica.calcularNivelUsuario(USUARIO_ID)).thenReturn(3);

    servicio.inicializarComodinesParaJugador(jugador);

    verify(repositorioComodin, times(3)).guardar(any(PartidaJugadorComodin.class));
  }

  @Test
  void aplicarBloqueoBloqueaAlOponenteYNotifica() {
    PartidaJugador oponente = configurarEscenarioAtaque(TipoComodin.BLOQUEO);

    servicio.aplicarBloqueo(PARTIDA_ID, USUARIO_ID);

    verify(oponente).setBloqueadoHasta(any(LocalDateTime.class));
    verify(repositorioPartidaJugador).guardar(oponente);
    verify(notificadorSala)
      .notificarComodinOponente("SALA1", PARTIDA_ID, TipoComodin.BLOQUEO.name(), "rival");
  }

  @Test
  void aplicarBorrosoConsumeComodinYNotifica() {
    configurarEscenarioAtaque(TipoComodin.BORROSO);

    servicio.aplicarBorroso(PARTIDA_ID, USUARIO_ID);

    verify(notificadorSala)
      .notificarComodinOponente("SALA1", PARTIDA_ID, TipoComodin.BORROSO.name(), "rival");
  }

  @Test
  void aplicarMezclarConsumeComodinYNotifica() {
    configurarEscenarioAtaque(TipoComodin.MEZCLAR);

    servicio.aplicarMezclar(PARTIDA_ID, USUARIO_ID);

    verify(notificadorSala)
      .notificarComodinOponente("SALA1", PARTIDA_ID, TipoComodin.MEZCLAR.name(), "rival");
  }

  @Test
  void aplicarBloqueoLanzaExcepcionSiNoHayStock() {
    Partida partida = mock(Partida.class);
    when(partida.getModoJuego()).thenReturn(ModoJuego.PRIMERO_GANA);
    when(jugador.getPartida()).thenReturn(partida);
    comodinConStock(TipoComodin.BLOQUEO, 0);

    assertThrows(
      IllegalStateException.class,
      () -> servicio.aplicarBloqueo(PARTIDA_ID, USUARIO_ID)
    );

    verify(notificadorSala, never()).notificarComodinOponente(any(), any(), any(), any());
  }

  @Test
  void aplicarBloqueoNoFallaSiNotificadorEsNull() {
    ServicioComodinImpl servicioSinNotificador = new ServicioComodinImpl(
      repositorioComodin,
      repositorioPartida,
      repositorioPartidaJugador,
      servicioEstadistica
    );
    PartidaJugador oponente = configurarEscenarioAtaque(TipoComodin.BLOQUEO);

    servicioSinNotificador.aplicarBloqueo(PARTIDA_ID, USUARIO_ID);

    verify(oponente).setBloqueadoHasta(any(LocalDateTime.class));
    verify(repositorioPartidaJugador).guardar(oponente);
  }

  private Opcion opcion(Long id, Boolean esCorrecta) {
    Opcion opcionMock = mock(Opcion.class);
    when(opcionMock.getId()).thenReturn(id);
    when(opcionMock.getEsCorrecta()).thenReturn(esCorrecta);
    return opcionMock;
  }
}
