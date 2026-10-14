package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.NotificadorSala;
import com.tallerwebi.dominio.enumeration.EstadoRonda;
import com.tallerwebi.dominio.modelo.*;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.servicio.ServicioPartidaPregunta;
import com.tallerwebi.dominio.servicio.impl.ServicioVotacionCategoriaImpl;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioVotacionCategoriaImplTest {

  private RepositorioPartida repositorioPartida;
  private ServicioPartidaPregunta servicioPartidaPregunta;
  private NotificadorSala notificadorSala;
  private ServicioVotacionCategoriaImpl servicio;

  private Partida partida;
  private PartidaRonda ronda;
  private Sala sala;
  private VotacionCategoria votacion;
  private SalaJugador jugadorSala;
  private Usuario usuario;
  private Categoria categoria;
  private Pregunta pregunta;

  @BeforeEach
  public void preparar() {
    repositorioPartida = mock(RepositorioPartida.class);
    servicioPartidaPregunta = mock(ServicioPartidaPregunta.class);
    notificadorSala = mock(NotificadorSala.class);

    servicio =
      new ServicioVotacionCategoriaImpl(
        repositorioPartida,
        servicioPartidaPregunta,
        notificadorSala
      );

    partida = mock(Partida.class);
    ronda = mock(PartidaRonda.class);
    sala = mock(Sala.class);
    jugadorSala = mock(SalaJugador.class);
    usuario = mock(Usuario.class);
    categoria = mock(Categoria.class);
    pregunta = mock(Pregunta.class);

    votacion = new VotacionCategoria(partida);

    when(repositorioPartida.obtenerPorId(1L)).thenReturn(partida);
    when(partida.obtenerRondaActual()).thenReturn(ronda);
    when(partida.getVotacionCategoria()).thenReturn(votacion);
    when(partida.getSala()).thenReturn(sala);
    when(partida.getId()).thenReturn(1L);

    when(ronda.getEstado()).thenReturn(EstadoRonda.VOTACION_CATEGORIA);

    when(sala.getJugadores()).thenReturn(new ArrayList<>(Collections.singletonList(jugadorSala)));
    when(sala.getCodigo()).thenReturn("ABC123");

    when(jugadorSala.getUsuario()).thenReturn(usuario);
    when(usuario.getId()).thenReturn(2L);

    when(categoria.getId()).thenReturn(5L);
    when(servicioPartidaPregunta.obtenerCategorias())
      .thenReturn(Collections.singletonMap(5L, categoria));
    when(servicioPartidaPregunta.obtenerPreguntaPorCategoria(5L)).thenReturn(pregunta);
  }

  @Test
  public void dadoQueLaPartidaNoExisteCuandoRegistroUnVotoObtengoExcepcion() {
    when(repositorioPartida.obtenerPorId(1L)).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> servicio.registrarVotoCategoria(1L, 2L, 5L));
  }

  @Test
  public void dadoQueLaVotacionEstaCerradaCuandoRegistroUnVotoObtengoExcepcion() {
    when(ronda.getEstado()).thenReturn(EstadoRonda.PREGUNTA_EN_CURSO);

    assertThrows(IllegalStateException.class, () -> servicio.registrarVotoCategoria(1L, 2L, 5L));
  }

  @Test
  public void dadoQueLaPartidaNoTieneVotacionCuandoRegistroUnVotoObtengoExcepcion() {
    when(partida.getVotacionCategoria()).thenReturn(null);

    assertThrows(IllegalStateException.class, () -> servicio.registrarVotoCategoria(1L, 2L, 5L));
  }

  @Test
  public void dadoQueElUsuarioNoPerteneceALaSalaCuandoVotaObtengoExcepcion() {
    when(sala.getJugadores()).thenReturn(Collections.emptyList());

    assertThrows(
      IllegalArgumentException.class,
      () -> servicio.registrarVotoCategoria(1L, 99L, 5L)
    );
  }

  @Test
  public void dadoQueLaCategoriaNoExisteCuandoRegistroUnVotoObtengoExcepcion() {
    when(servicioPartidaPregunta.obtenerCategorias()).thenReturn(Collections.emptyMap());

    assertThrows(
      IllegalArgumentException.class,
      () -> servicio.registrarVotoCategoria(1L, 2L, 99L)
    );
  }

  @Test
  public void dadoQueElUsuarioYaVotoCuandoIntentaVotarNuevamenteObtengoExcepcion() {
    votacion.agregarVoto(new VotoCategoria(usuario, categoria));

    assertThrows(IllegalStateException.class, () -> servicio.registrarVotoCategoria(1L, 2L, 5L));
  }

  @Test
  public void dadoQueFaltaUnJugadorPorVotarCuandoRegistroUnVotoObtengoFalso() {
    Usuario segundoUsuario = mock(Usuario.class);
    SalaJugador segundoJugador = mock(SalaJugador.class);

    when(segundoUsuario.getId()).thenReturn(3L);
    when(segundoJugador.getUsuario()).thenReturn(segundoUsuario);

    when(sala.getJugadores()).thenReturn(Arrays.asList(jugadorSala, segundoJugador));

    boolean resultado = servicio.registrarVotoCategoria(1L, 2L, 5L);

    assertFalse(resultado);
    assertEquals(1, votacion.getVotosCategoria().size());
    verify(notificadorSala, never()).votacionCategoriaFinalizada(anyString(), anyLong());
  }

  @Test
  public void dadoQueTodosVotaronCuandoRegistroElUltimoVotoObtengoVerdadero() {
    boolean resultado = servicio.registrarVotoCategoria(1L, 2L, 5L);

    assertTrue(resultado);
    assertEquals(1, votacion.getVotosCategoria().size());
    assertSame(categoria, votacion.getCategoriaElegida());
    verify(ronda).setPregunta(pregunta);
    verify(ronda).setEstado(EstadoRonda.PREGUNTA_EN_CURSO);
    verify(notificadorSala).votacionCategoriaFinalizada("ABC123", 1L);
  }

  @Test
  public void dadoQueElSolicitanteNoEsHostCuandoFinalizaLaVotacionObtengoExcepcion() {
    Usuario host = mock(Usuario.class);

    when(host.getId()).thenReturn(10L);
    when(sala.getHost()).thenReturn(host);

    assertThrows(IllegalStateException.class, () -> servicio.finalizarVotacionCategoria(1L, 2L));
  }

  @Test
  public void dadoQueFaltanVotosCuandoElHostIntentaFinalizarObtengoExcepcion() {
    Usuario host = mock(Usuario.class);
    when(host.getId()).thenReturn(2L);
    when(sala.getHost()).thenReturn(host);

    Usuario segundoUsuario = mock(Usuario.class);
    SalaJugador segundoJugador = mock(SalaJugador.class);

    when(segundoUsuario.getId()).thenReturn(3L);
    when(segundoJugador.getUsuario()).thenReturn(segundoUsuario);
    when(sala.getJugadores()).thenReturn(Arrays.asList(jugadorSala, segundoJugador));

    votacion.agregarVoto(new VotoCategoria(usuario, categoria));

    assertThrows(IllegalStateException.class, () -> servicio.finalizarVotacionCategoria(1L, 2L));
  }

  @Test
  public void dadoQueNoHayPreguntaDisponibleCuandoFinalizaLaVotacionObtengoExcepcion() {
    Usuario host = mock(Usuario.class);
    when(host.getId()).thenReturn(2L);
    when(sala.getHost()).thenReturn(host);

    when(servicioPartidaPregunta.obtenerPreguntaPorCategoria(5L)).thenReturn(null);

    votacion.agregarVoto(new VotoCategoria(usuario, categoria));

    assertThrows(IllegalStateException.class, () -> servicio.finalizarVotacionCategoria(1L, 2L));
  }

  @Test
  public void dadoQueLaRondaActualNoExisteCuandoRegistroUnVotoObtengoExcepcion() {
    when(partida.obtenerRondaActual()).thenReturn(null);

    assertThrows(IllegalStateException.class, () -> servicio.registrarVotoCategoria(1L, 2L, 5L));
  }
}
