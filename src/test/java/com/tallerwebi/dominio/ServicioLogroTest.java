package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.enumeration.TipoLogro;
import com.tallerwebi.dominio.modelo.Logro;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.modelo.UsuarioLogro;
import com.tallerwebi.dominio.repositorio.RepositorioLogro;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.impl.ServicioLogroImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioLogroTest {

  private static final Long PARTIDA_ID = 1L;
  private static final Long USUARIO_ID = 10L;

  private ServicioLogroImpl servicio;
  private RepositorioLogro repositorioLogroMock;
  private RepositorioPartidaJugador repositorioPartidaJugadorMock;

  private Usuario usuario;
  private List<TipoLogro> nuevosLogros;
  private List<Logro> logrosDisponibles;

  @BeforeEach
  public void init() {
    this.repositorioLogroMock = mock(RepositorioLogro.class);
    this.repositorioPartidaJugadorMock = mock(RepositorioPartidaJugador.class);
    this.servicio =
      new ServicioLogroImpl(this.repositorioLogroMock, this.repositorioPartidaJugadorMock);

    this.usuario = givenUnUsuarioMockeado(USUARIO_ID);
    givenQueElUsuarioNoTieneLogrosPrevios(USUARIO_ID);
  }

  // ---------- evaluarLogrosDelUsuario: casos borde ----------

  @Test
  public void queEvaluarLogrosDelUsuarioDevuelvaListaVaciaSiElUsuarioEsNulo() {
    // preparacion
    givenUnUsuarioNulo();

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
    thenNoSeInteractuaConLosRepositorios();
  }

  @Test
  public void queEvaluarLogrosDelUsuarioDevuelvaListaVaciaSiElUsuarioNoTieneId() {
    // preparacion
    givenUnUsuarioSinId();

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
    thenNoSeInteractuaConLosRepositorios();
  }

  @Test
  public void queEvaluarLogrosDelUsuarioSinPartidasFinalizadasNoDesbloqueeNada() {
    // preparacion
    givenQueElUsuarioNoTienePartidasFinalizadas(USUARIO_ID);

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
    thenSeBuscanLasPartidasFinalizadasDelUsuario(USUARIO_ID);
    thenNoSeGuardaNingunLogro();
  }

  // ---------- En Racha / Fuego Puro / Campeón Debutante ----------

  @Test
  public void queGanarConRachaDeCincoDesbloqueeEnRachaFuegoPuroYCampeonDebutante() {
    // preparacion
    givenQueElUsuarioTienePartidasFinalizadas(USUARIO_ID, givenUnaPartidaFinalizada(1, 5, 5));

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenSeDesbloqueanLosLogros(
      TipoLogro.EN_RACHA,
      TipoLogro.FUEGO_PURO,
      TipoLogro.CAMPEON_DEBUTANTE
    );
    thenSeGuardanLosLogros(TipoLogro.EN_RACHA, TipoLogro.FUEGO_PURO, TipoLogro.CAMPEON_DEBUTANTE);
  }

  @Test
  public void queLosLogrosSeGuardenAsociadosAlUsuario() {
    // preparacion
    givenQueElUsuarioTienePartidasFinalizadas(USUARIO_ID, givenUnaPartidaFinalizada(1, 3, 3));

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenLosLogrosSeGuardanAsociadosAlUsuario(2, this.usuario); // En racha y Campeon debutante
  }

  @Test
  public void queUnaRachaDeTresSinGanarSoloDesbloqueeEnRacha() {
    // preparacion
    givenQueElUsuarioTienePartidasFinalizadas(USUARIO_ID, givenUnaPartidaFinalizada(2, 3, 3));

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.EN_RACHA);
  }

  @Test
  public void queUnaRachaDeCuatroNoAlcanceParaFuegoPuro() {
    // preparacion
    givenQueElUsuarioTienePartidasFinalizadas(USUARIO_ID, givenUnaPartidaFinalizada(2, 4, 4));

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.EN_RACHA);
  }

  @Test
  public void queUnaRachaDeDosYUnaDerrotaNoDesbloqueeNada() {
    // preparacion
    givenQueElUsuarioTienePartidasFinalizadas(USUARIO_ID, givenUnaPartidaFinalizada(2, 2, 2));

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
    thenNoSeGuardaNingunLogro();
  }

  @Test
  public void queCampeonDebutanteSeDesbloqueeConLaPrimeraVictoriaAunqueNoHayaRacha() {
    // preparacion
    givenQueElUsuarioTienePartidasFinalizadas(USUARIO_ID, givenUnaPartidaFinalizada(1, 1, 1));

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.CAMPEON_DEBUTANTE);
  }

  // ---------- Erudito ----------

  @Test
  public void queEruditoSeDesbloqueeAlAcumularCincuentaAciertosEnElHistorial() {
    // preparacion
    givenQueElUsuarioTienePartidasFinalizadas(
      USUARIO_ID,
      givenUnaPartidaFinalizada(2, 25, 1),
      givenUnaPartidaFinalizada(2, 25, 1)
    );

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.ERUDITO);
  }

  @Test
  public void queEruditoNoSeDesbloqueeConCuarentaYNueveAciertos() {
    // preparacion
    givenQueElUsuarioTienePartidasFinalizadas(
      USUARIO_ID,
      givenUnaPartidaFinalizada(2, 25, 1),
      givenUnaPartidaFinalizada(2, 24, 1)
    );

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
  }

  // ---------- Imparable ----------

  @Test
  public void queImparableSeDesbloqueeConTresVictoriasConsecutivas() {
    // preparacion
    givenQueElUsuarioTienePartidasFinalizadas(
      USUARIO_ID,
      givenUnaPartidaFinalizada(1, 0, 0),
      givenUnaPartidaFinalizada(1, 0, 0),
      givenUnaPartidaFinalizada(1, 0, 0)
    );

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.CAMPEON_DEBUTANTE, TipoLogro.IMPARABLE);
  }

  @Test
  public void queImparableNoSeDesbloqueeConSoloDosVictoriasConsecutivas() {
    // preparacion
    givenQueElUsuarioTienePartidasFinalizadas(
      USUARIO_ID,
      givenUnaPartidaFinalizada(1, 0, 0),
      givenUnaPartidaFinalizada(1, 0, 0)
    );

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.CAMPEON_DEBUTANTE);
  }

  @Test
  public void queImparableCuenteLasVictoriasDesdeLaPartidaMasRecienteYSeCorteConUnaDerrota() {
    // preparacion - La lista viene de la más reciente a la más vieja: 1, 1, derrota, 1
    givenQueElUsuarioTienePartidasFinalizadas(
      USUARIO_ID,
      givenUnaPartidaFinalizada(1, 0, 0),
      givenUnaPartidaFinalizada(1, 0, 0),
      givenUnaPartidaFinalizada(2, 0, 0),
      givenUnaPartidaFinalizada(1, 0, 0)
    );

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.CAMPEON_DEBUTANTE);
  }

  @Test
  public void queImparableSeCorteSiUnaPartidaNoTienePosicionFinal() {
    // preparacion
    givenQueElUsuarioTienePartidasFinalizadas(
      USUARIO_ID,
      givenUnaPartidaFinalizada(1, 0, 0),
      givenUnaPartidaFinalizada(null, 0, 0),
      givenUnaPartidaFinalizada(1, 0, 0),
      givenUnaPartidaFinalizada(1, 0, 0)
    );

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.CAMPEON_DEBUTANTE);
  }

  // ---------- Logros ya desbloqueados ----------

  @Test
  public void queNoVuelvaAOtorgarUnLogroQueElUsuarioYaTiene() {
    // preparacion
    givenQueElUsuarioYaTieneElLogro(USUARIO_ID, TipoLogro.EN_RACHA);
    givenQueElUsuarioTienePartidasFinalizadas(USUARIO_ID, givenUnaPartidaFinalizada(1, 5, 5));

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.FUEGO_PURO, TipoLogro.CAMPEON_DEBUTANTE);
    thenSeGuardanLosLogros(TipoLogro.FUEGO_PURO, TipoLogro.CAMPEON_DEBUTANTE);
  }

  @Test
  public void queSiElUsuarioYaTieneTodosLosLogrosNoGuardeNada() {
    // preparacion
    givenQueElUsuarioYaTieneTodosLosLogros(USUARIO_ID);
    givenQueElUsuarioTienePartidasFinalizadas(USUARIO_ID, givenUnaPartidaFinalizada(1, 5, 5));

    // ejecucion
    whenSeEvaluanLosLogrosDelUsuario();

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
    thenNoSeGuardaNingunLogro();
  }

  // ---------- evaluarLogroDePartida ----------

  @Test
  public void queEvaluarLogroDePartidaValueATodosLosJugadoresDeLaPartida() {
    // preparacion
    Usuario ana = givenUnUsuarioMockeado(10L);
    Usuario beto = givenUnUsuarioMockeado(20L);

    givenQueElUsuarioNoTieneLogrosPrevios(20L);
    givenQueLaPartidaTieneJugadores(PARTIDA_ID, givenJugadorDe(ana), givenJugadorDe(beto));

    givenQueElUsuarioTienePartidasFinalizadas(10L, givenUnaPartidaFinalizada(1, 5, 5)); // ana: 3 logros
    givenQueElUsuarioTienePartidasFinalizadas(20L, givenUnaPartidaFinalizada(2, 1, 1)); // beto: ninguno

    // ejecucion
    whenSeEvaluanLosLogrosDeLaPartida(PARTIDA_ID);

    // validacion
    thenSeBuscanLasPartidasFinalizadasDelUsuario(10L);
    thenSeBuscanLasPartidasFinalizadasDelUsuario(20L);
    thenLosLogrosSeGuardanAsociadosAlUsuario(3, ana);
  }

  @Test
  public void queEvaluarLogroDePartidaSinJugadoresNoGuardeNada() {
    // preparacion
    givenQueLaPartidaNoTieneJugadores(PARTIDA_ID);

    // ejecucion
    whenSeEvaluanLosLogrosDeLaPartida(PARTIDA_ID);

    // validacion
    thenNoSeGuardaNingunLogro();
    verify(this.repositorioPartidaJugadorMock, never()).listarPartidasFinalizadasDelUsuario(any());
  }

  // ---------- obtenerLogrosDisponibles ----------

  @Test
  public void queObtenerLogrosDisponiblesDevuelvaTodoElCatalogoBloqueadoSiNoHayLogrosObtenidos() {
    // preparacion
    // el init() ya definió que el usuario no tiene logros previos

    // ejecucion
    whenSeObtienenLosLogrosDisponibles();

    // validacion
    thenSeDevuelveTodoElCatalogoBloqueado();
  }

  @Test
  public void queObtenerLogrosDisponiblesMarqueComoDesbloqueadosSoloLosObtenidosConSuFecha() {
    // preparacion
    givenQueElUsuarioTieneElLogroConFecha(
      USUARIO_ID,
      TipoLogro.FUEGO_PURO,
      LocalDateTime.of(2026, 10, 26, 12, 0)
    );

    // ejecucion
    whenSeObtienenLosLogrosDisponibles();

    // validacion
    thenElLogroApareceDesbloqueadoConFecha(TipoLogro.FUEGO_PURO, "26/10/2026");
    thenElLogroApareceBloqueado(TipoLogro.EN_RACHA);
    thenElLogroApareceBloqueado(TipoLogro.IMPARABLE);
  }

  @Test
  public void queObtenerLogrosDisponiblesConUsuarioNuloDevuelvaTodoBloqueadoSinConsultarRepositorio() {
    // preparacion
    givenUnUsuarioNulo();

    // ejecucion
    this.logrosDisponibles = this.servicio.obtenerLogrosDisponibles(null);

    // validacion
    thenSeDevuelveTodoElCatalogoBloqueado();
    verifyNoInteractions(this.repositorioLogroMock);
  }

  // given

  private void givenUnUsuarioNulo() {
    this.usuario = null;
  }

  private void givenUnUsuarioSinId() {
    this.usuario = givenUnUsuarioMockeado(null);
  }

  private Usuario givenUnUsuarioMockeado(Long id) {
    Usuario u = mock(Usuario.class);
    when(u.getId()).thenReturn(id);
    return u;
  }

  private PartidaJugador givenUnaPartidaFinalizada(
    Integer posicionFinal,
    int aciertos,
    int mejorRacha
  ) {
    PartidaJugador pj = mock(PartidaJugador.class);
    when(pj.getPosicionFinal()).thenReturn(posicionFinal);
    when(pj.getAciertos()).thenReturn(aciertos);
    when(pj.getErrores()).thenReturn(0);
    when(pj.getMejorRachaPartida()).thenReturn(mejorRacha);
    return pj;
  }

  private PartidaJugador givenJugadorDe(Usuario u) {
    PartidaJugador pj = mock(PartidaJugador.class);
    when(pj.getUsuario()).thenReturn(u);
    return pj;
  }

  private void givenQueElUsuarioNoTienePartidasFinalizadas(Long usuarioId) {
    when(this.repositorioPartidaJugadorMock.listarPartidasFinalizadasDelUsuario(usuarioId))
      .thenReturn(new ArrayList<>());
  }

  private void givenQueElUsuarioTienePartidasFinalizadas(
    Long usuarioId,
    PartidaJugador... partidas
  ) {
    when(this.repositorioPartidaJugadorMock.listarPartidasFinalizadasDelUsuario(usuarioId))
      .thenReturn(new ArrayList<>(Arrays.asList(partidas)));
  }

  private void givenQueElUsuarioNoTieneLogrosPrevios(Long usuarioId) {
    when(this.repositorioLogroMock.obtenerLogroPorUsuario(usuarioId)).thenReturn(new ArrayList<>());
  }

  private void givenQueElUsuarioYaTieneElLogro(Long usuarioId, TipoLogro tipo) {
    UsuarioLogro yaTiene = new UsuarioLogro(this.usuario, tipo);
    when(this.repositorioLogroMock.obtenerLogroPorUsuario(usuarioId)).thenReturn(List.of(yaTiene));
  }

  private void givenQueElUsuarioTieneElLogroConFecha(
    Long usuarioId,
    TipoLogro tipo,
    LocalDateTime fecha
  ) {
    UsuarioLogro obtenido = new UsuarioLogro(this.usuario, tipo);
    obtenido.setFechaDesbloqueo(fecha);
    when(this.repositorioLogroMock.obtenerLogroPorUsuario(usuarioId)).thenReturn(List.of(obtenido));
  }

  private void givenQueElUsuarioYaTieneTodosLosLogros(Long usuarioId) {
    List<UsuarioLogro> todos = new ArrayList<>();
    for (TipoLogro tipo : TipoLogro.values()) {
      todos.add(new UsuarioLogro(this.usuario, tipo));
    }
    when(this.repositorioLogroMock.obtenerLogroPorUsuario(usuarioId)).thenReturn(todos);
  }

  private void givenQueLaPartidaTieneJugadores(Long partidaId, PartidaJugador... jugadores) {
    when(this.repositorioPartidaJugadorMock.obtenerPorPartida(partidaId))
      .thenReturn(Arrays.asList(jugadores));
  }

  private void givenQueLaPartidaNoTieneJugadores(Long partidaId) {
    when(this.repositorioPartidaJugadorMock.obtenerPorPartida(partidaId))
      .thenReturn(new ArrayList<>());
  }

  // when

  private void whenSeEvaluanLosLogrosDelUsuario() {
    this.nuevosLogros = this.servicio.evaluarLogrosDelUsuario(this.usuario);
  }

  private void whenSeEvaluanLosLogrosDeLaPartida(Long partidaId) {
    this.servicio.evaluarLogroDePartida(partidaId);
  }

  private void whenSeObtienenLosLogrosDisponibles() {
    this.logrosDisponibles = this.servicio.obtenerLogrosDisponibles(USUARIO_ID);
  }

  // then

  private void thenLaListaDeNuevosLogrosEstaVacia() {
    assertThat(this.nuevosLogros.isEmpty(), is(true));
  }

  private void thenNoSeInteractuaConLosRepositorios() {
    verifyNoInteractions(this.repositorioLogroMock, this.repositorioPartidaJugadorMock);
  }

  private void thenSeBuscanLasPartidasFinalizadasDelUsuario(Long usuarioId) {
    verify(this.repositorioPartidaJugadorMock).listarPartidasFinalizadasDelUsuario(usuarioId);
  }

  private void thenNoSeGuardaNingunLogro() {
    verify(this.repositorioLogroMock, never()).guardarLogro(any());
  }

  private void thenSeDesbloqueanLosLogros(TipoLogro... esperados) {
    assertThat(this.nuevosLogros, equalTo(List.of(esperados)));
  }

  private void thenSeGuardanLosLogros(TipoLogro... esperados) {
    ArgumentCaptor<UsuarioLogro> captor = ArgumentCaptor.forClass(UsuarioLogro.class);
    verify(this.repositorioLogroMock, times(esperados.length)).guardarLogro(captor.capture());

    List<TipoLogro> tiposGuardados = new ArrayList<>();
    for (UsuarioLogro guardado : captor.getAllValues()) {
      tiposGuardados.add(guardado.getTipoLogro());
    }
    assertThat(tiposGuardados, equalTo(List.of(esperados)));
  }

  private void thenLosLogrosSeGuardanAsociadosAlUsuario(
    int vecesEsperadas,
    Usuario usuarioEsperado
  ) {
    ArgumentCaptor<UsuarioLogro> captor = ArgumentCaptor.forClass(UsuarioLogro.class);
    verify(this.repositorioLogroMock, times(vecesEsperadas)).guardarLogro(captor.capture());

    for (UsuarioLogro guardado : captor.getAllValues()) {
      assertThat(guardado.getUsuario(), sameInstance(usuarioEsperado));
    }
  }

  private void thenSeDevuelveTodoElCatalogoBloqueado() {
    assertThat(this.logrosDisponibles.size(), equalTo(TipoLogro.values().length));
    for (int i = 0; i < this.logrosDisponibles.size(); i++) {
      assertThat(this.logrosDisponibles.get(i).getCodigo(), equalTo(TipoLogro.values()[i].name()));
      assertThat(this.logrosDisponibles.get(i).isDesbloqueado(), is(false));
    }
  }

  private void thenElLogroApareceDesbloqueadoConFecha(TipoLogro tipo, String fechaEsperada) {
    Logro logro = buscarLogro(this.logrosDisponibles, tipo);
    assertThat(logro.isDesbloqueado(), is(true));
    assertThat(logro.getFechaFormateada(), equalTo(fechaEsperada));
  }

  private void thenElLogroApareceBloqueado(TipoLogro tipo) {
    Logro logro = buscarLogro(this.logrosDisponibles, tipo);
    assertThat(logro.isDesbloqueado(), is(false));
  }

  private Logro buscarLogro(List<Logro> logros, TipoLogro tipo) {
    for (Logro logro : logros) {
      if (logro.getCodigo().equals(tipo.name())) {
        return logro;
      }
    }
    throw new AssertionError("No se encontró el logro " + tipo);
  }
}
