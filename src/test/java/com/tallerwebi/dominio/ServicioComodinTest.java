package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.enumeration.TipoComodin;
import com.tallerwebi.dominio.modelo.Comodin;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.PartidaJugadorComodin;
import com.tallerwebi.dominio.modelo.PartidaRonda;
import com.tallerwebi.dominio.modelo.Pregunta;
import com.tallerwebi.dominio.repositorio.RepositorioComodin;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.ServicioComodin;
import com.tallerwebi.dominio.servicio.impl.ServicioComodinImpl;
import com.tallerwebi.presentacion.dto.Comodin5050DTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioComodinTest {

  private static final Long PARTIDA_ID = 1L;
  private static final Long USUARIO_ID = 100L;
  private static final Long JUGADOR_ID = 10L;
  private static final Long RONDA_ID = 50L;

  private RepositorioComodin repositorioComodin;
  private RepositorioPartida repositorioPartida;
  private RepositorioPartidaJugador repositorioPartidaJugador;
  private ServicioComodin servicioComodin;

  private PartidaJugador jugador;
  private Partida partida;
  private PartidaRonda ronda;

  @BeforeEach
  public void init() {
    repositorioComodin = mock(RepositorioComodin.class);
    repositorioPartida = mock(RepositorioPartida.class);
    repositorioPartidaJugador = mock(RepositorioPartidaJugador.class);

    servicioComodin =
      new ServicioComodinImpl(repositorioComodin, repositorioPartida, repositorioPartidaJugador);

    inicializarEscenarioBase();
  }

  @Test
  public void cincuentaCincuentaDeberiaDescartarDosIncorrectasYConsumirStock() {
    PartidaJugadorComodin comodinJugador = givenTengoUnComodinDisponible(
      TipoComodin.CINCUENTA_CINCUENTA,
      1
    );
    Long idOpcionCorrecta = 1L;
    givenTengoUnaPreguntaConCuatroOpcionesEnLaRonda(idOpcionCorrecta);

    Comodin5050DTO resultado = whenAplicoCincuentaCincuenta(PARTIDA_ID, USUARIO_ID);

    thenSeDescartanOpcionesIncorrectas(resultado, 2, idOpcionCorrecta);
    thenSePenalizanSegundos(resultado, 5);
    thenElComodinFueConsumido(comodinJugador);
  }

  @Test
  public void tiempoExtraDeberiaRetornarDiezSegundos() {
    PartidaJugadorComodin comodinJugador = givenTengoUnComodinDisponible(
      TipoComodin.TIEMPO_EXTRA,
      1
    );

    int segundos = whenAplicoTiempoExtra(PARTIDA_ID, USUARIO_ID);

    thenObtengoSegundosExtra(segundos, 10);
    thenElComodinFueConsumido(comodinJugador);
  }

  @Test
  public void comodinSinStockDeberiaLanzarExcepcion() {
    givenTengoUnComodinSinStock(TipoComodin.TIEMPO_EXTRA);

    Throwable excepcion = whenAplicoTiempoExtraCapturandoExcepcion(PARTIDA_ID, USUARIO_ID);

    thenSeLanzaExcepcion(excepcion, IllegalStateException.class);
  }

  private PartidaJugadorComodin givenTengoUnComodinDisponible(TipoComodin tipo, int stock) {
    Comodin comodin = new Comodin(tipo, tipo.getEtiqueta(), tipo.getDescripcion(), "icon");
    PartidaJugadorComodin pjc = new PartidaJugadorComodin(jugador, comodin, stock);
    when(repositorioComodin.buscarPorJugadorYTipo(JUGADOR_ID, tipo)).thenReturn(pjc);
    return pjc;
  }

  private void givenTengoUnComodinSinStock(TipoComodin tipo) {
    givenTengoUnComodinDisponible(tipo, 0);
  }

  private void givenTengoUnaPreguntaConCuatroOpcionesEnLaRonda(Long idCorrecta) {
    Pregunta pregunta = new Pregunta();
    pregunta.setOpciones(
      List.of(
        crearOpcion(1L, idCorrecta.equals(1L)),
        crearOpcion(2L, idCorrecta.equals(2L)),
        crearOpcion(3L, idCorrecta.equals(3L)),
        crearOpcion(4L, idCorrecta.equals(4L))
      )
    );
    ronda.setPregunta(pregunta);
  }

  private Opcion crearOpcion(Long id, boolean esCorrecta) {
    Opcion opcion = new Opcion();
    opcion.setId(id);
    opcion.setEsCorrecta(esCorrecta);
    return opcion;
  }

  private Comodin5050DTO whenAplicoCincuentaCincuenta(Long partidaId, Long usuarioId) {
    return servicioComodin.aplicarCincuentaCincuenta(partidaId, usuarioId);
  }

  private int whenAplicoTiempoExtra(Long partidaId, Long usuarioId) {
    return servicioComodin.aplicarTiempoExtra(partidaId, usuarioId);
  }

  private Throwable whenAplicoTiempoExtraCapturandoExcepcion(Long partidaId, Long usuarioId) {
    try {
      servicioComodin.aplicarTiempoExtra(partidaId, usuarioId);
      return null;
    } catch (Throwable ex) {
      return ex;
    }
  }

  private void thenSeDescartanOpcionesIncorrectas(
    Comodin5050DTO resultado,
    int cantidadEsperada,
    Long idCorrecta
  ) {
    assertThat(resultado, is(notNullValue()));
    assertThat(resultado.getOpcionesDescartadas().size(), equalTo(cantidadEsperada));
    assertThat(resultado.getOpcionesDescartadas(), not(hasItem(idCorrecta)));
  }

  private void thenSePenalizanSegundos(Comodin5050DTO resultado, int penalizacionEsperada) {
    assertThat(resultado.getPenalizacionSegundos(), equalTo(penalizacionEsperada));
  }

  private void thenElComodinFueConsumido(PartidaJugadorComodin comodinJugador) {
    assertThat(comodinJugador.getCantidadDisponible(), equalTo(0));
    verify(repositorioComodin).actualizar(comodinJugador);
  }

  private void thenObtengoSegundosExtra(int segundosObtenidos, int segundosEsperados) {
    assertThat(segundosObtenidos, equalTo(segundosEsperados));
  }

  private void thenSeLanzaExcepcion(Throwable excepcion, Class<? extends Throwable> tipoEsperado) {
    assertThat(excepcion, is(notNullValue()));
    assertThat(excepcion, instanceOf(tipoEsperado));
  }

  private void inicializarEscenarioBase() {
    jugador = new PartidaJugador();
    jugador.setId(JUGADOR_ID);

    partida = new Partida();
    partida.setId(PARTIDA_ID);

    ronda = new PartidaRonda(partida, 1);
    ronda.setId(RONDA_ID);
    partida.getRondas().add(ronda);

    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(PARTIDA_ID, USUARIO_ID))
      .thenReturn(jugador);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);
  }
}
