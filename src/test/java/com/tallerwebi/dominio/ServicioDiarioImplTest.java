package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.modelo.DesafioDiario;
import com.tallerwebi.dominio.modelo.IntentoDesafio;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioDesafioDiario;
import com.tallerwebi.dominio.repositorio.RepositorioIntentoDesafio;
import com.tallerwebi.dominio.repositorio.RepositorioOpcion;
import com.tallerwebi.dominio.repositorio.RepositorioPregunta;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.impl.ServicioDesafioDiarioImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioDiarioImplTest {

  private RepositorioDesafioDiario repositorioDesafio;
  private RepositorioIntentoDesafio repositorioIntento;
  private RepositorioUsuario repositorioUsuario;
  private RepositorioPregunta repositorioPregunta;
  private RepositorioOpcion repositorioOpcion;

  private ServicioDesafioDiarioImpl servicio;
  private DesafioDiario desafio;

  @BeforeEach
  void setUp() {
    repositorioDesafio = mock(RepositorioDesafioDiario.class);
    repositorioIntento = mock(RepositorioIntentoDesafio.class);
    repositorioUsuario = mock(RepositorioUsuario.class);
    repositorioPregunta = mock(RepositorioPregunta.class);
    repositorioOpcion = mock(RepositorioOpcion.class);

    servicio =
      new ServicioDesafioDiarioImpl(
        repositorioDesafio,
        repositorioIntento,
        repositorioUsuario,
        repositorioPregunta,
        repositorioOpcion
      );

    desafio = new DesafioDiario(LocalDate.now());

    when(repositorioDesafio.buscarPorFecha(any(LocalDate.class))).thenReturn(desafio);
  }

  @Test
  void debeContinuarIntentoPendienteEnLugarDeCrearOtro() {
    Long usuarioId = 10L;
    IntentoDesafio intentoPendiente = mock(IntentoDesafio.class);

    // El servicio inicializa las opciones del desafío del intento: no puede ser null
    when(intentoPendiente.getDesafio()).thenReturn(desafio);
    when(repositorioIntento.buscarIntentoPendiente(usuarioId, desafio.getId()))
      .thenReturn(intentoPendiente);

    IntentoDesafio resultado = servicio.iniciarOContinuarIntento(usuarioId);

    assertSame(intentoPendiente, resultado);
    verify(repositorioIntento, never()).guardar(any(IntentoDesafio.class));
  }

  @Test
  void debeCrearIntentoNuevoCuandoNoHayUnoPendiente() {
    Long usuarioId = 10L;
    Usuario usuario = mock(Usuario.class);

    when(repositorioIntento.buscarIntentoPendiente(usuarioId, desafio.getId())).thenReturn(null);
    when(repositorioUsuario.buscarPorId(usuarioId)).thenReturn(usuario);

    IntentoDesafio resultado = servicio.iniciarOContinuarIntento(usuarioId);

    assertNotNull(resultado);
    assertSame(usuario, resultado.getUsuario());
    assertSame(desafio, resultado.getDesafio());
    assertNotNull(resultado.getFechaInicio());

    verify(repositorioIntento).guardar(resultado);
  }

  @Test
  void rankingDebeConservarElMejorIntentoDeCadaUsuario() {
    Usuario jugador = crearUsuario(1L);
    Usuario otroJugador = crearUsuario(2L);

    IntentoDesafio intentoPeor = crearIntento(
      jugador,
      3,
      LocalDateTime.now().minusMinutes(3),
      LocalDateTime.now().minusMinutes(2)
    );

    IntentoDesafio mejorIntento = crearIntento(
      jugador,
      5,
      LocalDateTime.now().minusMinutes(2),
      LocalDateTime.now().minusMinutes(1)
    );

    IntentoDesafio intentoOtroJugador = crearIntento(
      otroJugador,
      4,
      LocalDateTime.now().minusMinutes(4),
      LocalDateTime.now().minusMinutes(2)
    );

    when(repositorioIntento.buscarFinalizadosPorDesafio(desafio.getId()))
      .thenReturn(Arrays.asList(intentoPeor, intentoOtroJugador, mejorIntento));

    List<IntentoDesafio> ranking = servicio.obtenerRankingDeHoy();

    assertEquals(2, ranking.size());
    assertSame(mejorIntento, ranking.get(0));
    assertSame(intentoOtroJugador, ranking.get(1));
  }

  @Test
  void siEmpatanEnPuntajeDebeElegirElIntentoMasRapidoDelJugador() {
    Usuario jugador = crearUsuario(1L);

    IntentoDesafio intentoLento = crearIntento(
      jugador,
      5,
      LocalDateTime.of(2026, 11, 10, 10, 0),
      LocalDateTime.of(2026, 11, 10, 10, 2)
    );

    IntentoDesafio intentoRapido = crearIntento(
      jugador,
      5,
      LocalDateTime.of(2026, 11, 10, 11, 0),
      LocalDateTime.of(2026, 11, 10, 11, 1)
    );

    when(repositorioIntento.buscarFinalizadosPorDesafio(desafio.getId()))
      .thenReturn(Arrays.asList(intentoLento, intentoRapido));

    List<IntentoDesafio> ranking = servicio.obtenerRankingDeHoy();

    assertEquals(1, ranking.size());
    assertSame(intentoRapido, ranking.get(0));
  }

  @Test
  void historialDebeDevolverTodosLosIntentosFinalizadosDelUsuario() {
    Long usuarioId = 10L;
    List<IntentoDesafio> intentos = Arrays.asList(
      mock(IntentoDesafio.class),
      mock(IntentoDesafio.class)
    );

    when(repositorioIntento.buscarFinalizadosPorUsuario(usuarioId)).thenReturn(intentos);

    List<IntentoDesafio> historial = servicio.obtenerHistorial(usuarioId);

    assertSame(intentos, historial);
    assertEquals(2, historial.size());
    verify(repositorioIntento).buscarFinalizadosPorUsuario(usuarioId);
  }

  private Usuario crearUsuario(Long id) {
    Usuario usuario = mock(Usuario.class);
    when(usuario.getId()).thenReturn(id);
    return usuario;
  }

  private IntentoDesafio crearIntento(
    Usuario usuario,
    int puntaje,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin
  ) {
    IntentoDesafio intento = mock(IntentoDesafio.class);

    when(intento.getUsuario()).thenReturn(usuario);
    when(intento.getPuntaje()).thenReturn(puntaje);
    when(intento.getFechaInicio()).thenReturn(fechaInicio);
    when(intento.getFechaFin()).thenReturn(fechaFin);

    return intento;
  }
}
