package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.modelo.EstadisticaUsuario;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.impl.ServicioEstadisticaImpl;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioEstadisticaTest {

  private static final Long ID_ANA = 1L;
  private static final Long ID_BETO = 2L;
  private static final Long ID_CARLA = 3L;

  private RepositorioPartidaJugador repositorioPartidaJugador;
  private ServicioEstadisticaImpl servicio;

  @BeforeEach
  void preparar() {
    repositorioPartidaJugador = mock(RepositorioPartidaJugador.class);
    servicio = new ServicioEstadisticaImpl(repositorioPartidaJugador);
  }

  @Test
  void conIdNuloDevuelveEstadisticaVaciaSinConsultarLaBase() {
    EstadisticaUsuario estadistica = servicio.obtenerEstadisticaUsuario(null);

    assertEquals(0, estadistica.getPartidasJugadas());
    verifyNoInteractions(repositorioPartidaJugador);
  }

  @Test
  void sinPartidasFinalizadasDevuelveTodoEnCero() {
    when(repositorioPartidaJugador.listarPartidasFinalizadasDelUsuario(ID_ANA))
      .thenReturn(Collections.emptyList());

    EstadisticaUsuario estadistica = servicio.obtenerEstadisticaUsuario(ID_ANA);

    assertEquals(0, estadistica.getPartidasJugadas());
    assertEquals(0, estadistica.getRachaActualRespuestas());
    assertEquals(0.0, estadistica.getPromedioAciertos(), 0.001);
  }

  @Test
  void sumaLasPartidasYTomaLaRachaActualDeLaMasReciente() {
    PartidaJugador reciente = jugador(ID_ANA, "ana", 3, 1, 3, 2, 1);
    PartidaJugador vieja = jugador(ID_ANA, "ana", 1, 2, 1, 0, 2);

    when(repositorioPartidaJugador.listarPartidasFinalizadasDelUsuario(ID_ANA))
      .thenReturn(List.of(reciente, vieja));

    EstadisticaUsuario estadistica = servicio.obtenerEstadisticaUsuario(ID_ANA);

    assertEquals(2, estadistica.getPartidasJugadas());
    assertEquals(1, estadistica.getPartidasGanadas());
    assertEquals(4, estadistica.getPreguntasAcertadas());
    assertEquals(3, estadistica.getPreguntasErradas());
    assertEquals(3, estadistica.getMejorRachaRespuestas());
    assertEquals(2, estadistica.getRachaActualRespuestas());
    assertEquals(57.1, estadistica.getPromedioAciertos(), 0.001);
  }

  @Test
  void elLeaderboardAgrupaTodasLasPartidasDeCadaUsuario() {
    when(repositorioPartidaJugador.listarTodasLasPartidasFinalizadas())
      .thenReturn(partidasDeTres());

    List<EstadisticaUsuario> ranking = servicio.obtenerLeaderBoard(10);

    assertEquals(3, ranking.size());

    EstadisticaUsuario ana = buscar(ranking, "ana");
    assertEquals(2, ana.getPartidasJugadas());
    assertEquals(1, ana.getPartidasGanadas());
    assertEquals(50.0, ana.getPromedioAciertos(), 0.001);
  }

  @Test
  void elLeaderboardOrdenaPorVictoriasYLuegoPorPromedio() {
    when(repositorioPartidaJugador.listarTodasLasPartidasFinalizadas())
      .thenReturn(partidasDeTres());

    List<EstadisticaUsuario> ranking = servicio.obtenerLeaderBoard(10);

    assertEquals("carla", ranking.get(0).getUsername());
    assertEquals("ana", ranking.get(1).getUsername());
    assertEquals("beto", ranking.get(2).getUsername());
  }

  @Test
  void elLeaderboardRespetaElLimite() {
    when(repositorioPartidaJugador.listarTodasLasPartidasFinalizadas())
      .thenReturn(partidasDeTres());

    List<EstadisticaUsuario> ranking = servicio.obtenerLeaderBoard(2);

    assertEquals(2, ranking.size());
    assertEquals("carla", ranking.get(0).getUsername());
    assertEquals("ana", ranking.get(1).getUsername());
  }

  @Test
  void elLeaderboardSinPartidasEstaVacio() {
    when(repositorioPartidaJugador.listarTodasLasPartidasFinalizadas())
      .thenReturn(Collections.emptyList());

    assertEquals(0, servicio.obtenerLeaderBoard(5).size());
  }

  private List<PartidaJugador> partidasDeTres() {
    return List.of(
      jugador(ID_ANA, "ana", 4, 1, 3, 1, 1),
      jugador(ID_BETO, "beto", 2, 3, 1, 0, 2),
      jugador(ID_ANA, "ana", 1, 4, 1, 0, 2),
      jugador(ID_CARLA, "carla", 5, 0, 5, 5, 1)
    );
  }

  private EstadisticaUsuario buscar(List<EstadisticaUsuario> ranking, String username) {
    for (EstadisticaUsuario estadistica : ranking) {
      if (username.equals(estadistica.getUsername())) {
        return estadistica;
      }
    }

    throw new AssertionError("No está " + username + " en el ranking");
  }

  private PartidaJugador jugador(
    Long usuarioId,
    String username,
    int aciertos,
    int errores,
    int mejorRacha,
    int rachaFinal,
    Integer posicion
  ) {
    Usuario usuario = new Usuario();
    usuario.setId(usuarioId);
    usuario.setUsername(username);

    PartidaJugador jugador = new PartidaJugador(null, usuario);
    jugador.setAciertos(aciertos);
    jugador.setErrores(errores);
    jugador.setMejorRachaPartida(mejorRacha);
    jugador.setRachaRespuestasPartida(rachaFinal);
    jugador.setPosicionFinal(posicion);
    return jugador;
  }
}
