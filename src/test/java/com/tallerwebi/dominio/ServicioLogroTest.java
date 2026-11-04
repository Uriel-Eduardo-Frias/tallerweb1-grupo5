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
import com.tallerwebi.dominio.modelo.ProgresoUsuario;
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

  @Test
  public void queEvaluarLogrosDelJugadorDevuelvaListaVaciaSiElJugadorEsNulo() {
    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(null);

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
    thenNoSeInteractuaConLosRepositorios();
  }

  @Test
  public void queEvaluarLogrosDelJugadorDevuelvaListaVaciaSiElJugadorNoTieneUsuario() {
    // preparacion
    PartidaJugador jugador = givenUnJugadorDe(null, 1, 5, 5);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
    thenNoSeInteractuaConLosRepositorios();
  }

  @Test
  public void queEvaluarLogrosDelJugadorDevuelvaListaVaciaSiElUsuarioNoTieneId() {
    // preparacion
    Usuario usuarioSinId = givenUnUsuarioMockeado(null);
    PartidaJugador jugador = givenUnJugadorDe(usuarioSinId, 1, 5, 5);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
    thenNoSeInteractuaConLosRepositorios();
  }

  @Test
  public void queGanarConRachaDeCincoDesbloqueeEnRachaFuegoPuroYCampeonDebutante() {
    // preparacion
    PartidaJugador jugador = givenUnJugador(1, 5, 5);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

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
    PartidaJugador jugador = givenUnJugador(1, 3, 3);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenLosLogrosSeGuardanAsociadosAlUsuario(2, this.usuario); // En racha y Campeon debutante
  }

  @Test
  public void queUnaRachaDeTresSinGanarSoloDesbloqueeEnRacha() {
    // preparacion
    PartidaJugador jugador = givenUnJugador(2, 3, 3);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.EN_RACHA);
  }

  @Test
  public void queUnaRachaDeCuatroNoAlcanceParaFuegoPuro() {
    // preparacion
    PartidaJugador jugador = givenUnJugador(2, 4, 4);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.EN_RACHA);
  }

  @Test
  public void queUnaRachaDeDosYNoGanarNoDesbloqueeNada() {
    // preparacion
    PartidaJugador jugador = givenUnJugador(2, 2, 2);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
    thenNoSeGuardaNingunLogro();
  }

  @Test
  public void queCampeonDebutanteSeDesbloqueeConLaVictoriaAunqueNoHayaRacha() {
    // preparacion
    PartidaJugador jugador = givenUnJugador(1, 1, 1);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.CAMPEON_DEBUTANTE);
  }

  @Test
  public void queEruditoSeDesbloqueeCuandoLosAciertosAcumuladosLleganACincuenta() {
    // preparacion
    givenProgresoPrevio(45, 0);
    PartidaJugador jugador = givenUnJugador(2, 5, 1);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.ERUDITO);
  }

  @Test
  public void queEruditoNoSeDesbloqueeConCuarentaYNueveAciertosAcumulados() {
    // preparacion
    givenProgresoPrevio(44, 0);
    PartidaJugador jugador = givenUnJugador(2, 5, 1);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
  }

  @Test
  public void queEruditoNoSeDesbloqueeSoloConLosAciertosDeLaPartidaActual() {
    // preparacion
    PartidaJugador jugador = givenUnJugador(2, 5, 1);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
  }

  @Test
  public void queImparableSeDesbloqueeAlGanarPorTerceraVezConsecutiva() {
    // preparacion
    givenProgresoPrevio(0, 2);
    givenQueElUsuarioYaTieneLosLogros(USUARIO_ID, TipoLogro.CAMPEON_DEBUTANTE);
    PartidaJugador jugador = givenUnJugador(1, 0, 0);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.IMPARABLE);
  }

  @Test
  public void queImparableNoSeDesbloqueeConSoloDosVictoriasConsecutivas() {
    // preparacion
    givenProgresoPrevio(0, 1);
    givenQueElUsuarioYaTieneLosLogros(USUARIO_ID, TipoLogro.CAMPEON_DEBUTANTE);
    PartidaJugador jugador = givenUnJugador(1, 0, 0);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
  }

  @Test
  public void quePerderReinicieLasVictoriasConsecutivasYNoDesbloqueeImparable() {
    // preparacion
    ProgresoUsuario progreso = givenProgresoPrevio(0, 2);
    PartidaJugador jugador = givenUnJugador(2, 0, 0);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
    thenElProgresoAcumuladoTiene(progreso, 0, 0); // Las victorias consecutivas vuelven a 0
  }

  @Test
  public void queCreeElProgresoSiElUsuarioTodaviaNoTeniaYRegistreLaPartida() {
    // preparacion
    PartidaJugador jugador = givenUnJugador(1, 5, 2);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenSeGuardaElProgresoCon(5, 1, this.usuario);
  }

  @Test
  public void queAcumuleLaPartidaSobreElProgresoExistente() {
    // preparacion
    ProgresoUsuario progreso = givenProgresoPrevio(10, 1);
    PartidaJugador jugador = givenUnJugador(1, 5, 2);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenElProgresoAcumuladoTiene(progreso, 15, 2);
    thenSeGuardaElProgreso(progreso);
  }

  @Test
  public void queUnaPartidaSinPosicionFinalCuenteComoNoGanada() {
    // preparacion
    ProgresoUsuario progreso = givenProgresoPrevio(0, 2);
    PartidaJugador jugador = givenUnJugador(null, 0, 0);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenElProgresoAcumuladoTiene(progreso, 0, 0); // Las victorias consecutivas vuelven a 0
  }

  @Test
  public void queNoVuelvaAOtorgarUnLogroQueElUsuarioYaTiene() {
    // preparacion
    givenQueElUsuarioYaTieneLosLogros(USUARIO_ID, TipoLogro.EN_RACHA);
    PartidaJugador jugador = givenUnJugador(1, 5, 5);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenSeDesbloqueanLosLogros(TipoLogro.FUEGO_PURO, TipoLogro.CAMPEON_DEBUTANTE);
    thenSeGuardanLosLogros(TipoLogro.FUEGO_PURO, TipoLogro.CAMPEON_DEBUTANTE);
  }

  @Test
  public void queSiElUsuarioYaTieneTodosLosLogrosNoCalculeNiAcumuleNada() {
    // preparacion
    givenQueElUsuarioYaTieneTodosLosLogros(USUARIO_ID);
    PartidaJugador jugador = givenUnJugador(1, 5, 5);

    // ejecucion
    whenSeEvaluanLosLogrosDelJugador(jugador);

    // validacion
    thenLaListaDeNuevosLogrosEstaVacia();
    thenNoSeGuardaNingunLogro();
    verify(this.repositorioLogroMock, never()).obtenerProgreso(any());
    verify(this.repositorioLogroMock, never()).guardarProgreso(any());
  }

  @Test
  public void queEvaluarLogroDePartidaValueATodosLosJugadoresDeLaPartida() {
    // preparacion
    Usuario ana = givenUnUsuarioMockeado(10L);
    Usuario beto = givenUnUsuarioMockeado(20L);

    givenQueElUsuarioNoTieneLogrosPrevios(20L);
    givenQueLaPartidaTieneJugadores(
      PARTIDA_ID,
      givenUnJugadorDe(ana, 1, 5, 5),
      givenUnJugadorDe(beto, 2, 1, 1)
    );

    // ejecucion
    whenSeEvaluanLosLogrosDeLaPartida(PARTIDA_ID);

    // validacion
    thenLosLogrosSeGuardanAsociadosAlUsuario(3, ana);
    verify(this.repositorioLogroMock, times(2)).guardarProgreso(any(ProgresoUsuario.class));
  }

  @Test
  public void queEvaluarLogroDePartidaNoConsulteElHistorialDePartidasDeLosUsuarios() {
    // preparacion
    givenQueLaPartidaTieneJugadores(PARTIDA_ID, givenUnJugador(1, 5, 5));

    // ejecucion
    whenSeEvaluanLosLogrosDeLaPartida(PARTIDA_ID);

    // validacion
    verify(this.repositorioPartidaJugadorMock, never()).listarPartidasFinalizadasDelUsuario(any());
  }

  @Test
  public void queEvaluarLogroDePartidaSinJugadoresNoGuardeNada() {
    // preparacion
    givenQueLaPartidaNoTieneJugadores(PARTIDA_ID);

    // ejecucion
    whenSeEvaluanLosLogrosDeLaPartida(PARTIDA_ID);

    // validacion
    thenNoSeInteractuaConLosRepositoriosDeGuardado();
  }

  // ---------- obtenerLogrosDisponibles ----------

  @Test
  public void queObtenerLogrosDisponiblesDevuelvaTodoElCatalogoBloqueadoSiNoHayLogrosObtenidos() {
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
  public void queObtenerLogrosDisponiblesConUsuarioNuloDevuelvaTodoBloqueadoSinConsultarElRepositorio() {
    // preparacion
    this.usuario = null;

    // ejecucion
    this.logrosDisponibles = this.servicio.obtenerLogrosDisponibles(null);

    // validacion
    thenSeDevuelveTodoElCatalogoBloqueado();
    verifyNoInteractions(this.repositorioLogroMock);
  }

  // given

  private Usuario givenUnUsuarioMockeado(Long id) {
    Usuario u = mock(Usuario.class);
    when(u.getId()).thenReturn(id);
    return u;
  }

  private PartidaJugador givenUnJugador(Integer posicionFinal, int aciertos, int mejorRacha) {
    return givenUnJugadorDe(this.usuario, posicionFinal, aciertos, mejorRacha);
  }

  private PartidaJugador givenUnJugadorDe(
    Usuario u,
    Integer posicionFinal,
    int aciertos,
    int mejorRacha
  ) {
    PartidaJugador pj = mock(PartidaJugador.class);
    when(pj.getUsuario()).thenReturn(u);
    when(pj.getPosicionFinal()).thenReturn(posicionFinal);
    when(pj.getAciertos()).thenReturn(aciertos);
    when(pj.getMejorRachaPartida()).thenReturn(mejorRacha);
    return pj;
  }

  private ProgresoUsuario givenProgresoPrevio(int aciertosTotales, int victoriasConsecutivas) {
    ProgresoUsuario progreso = new ProgresoUsuario(this.usuario);
    progreso.setAciertosTotales(aciertosTotales);
    progreso.setVictoriasConsecutivas(victoriasConsecutivas);
    when(this.repositorioLogroMock.obtenerProgreso(USUARIO_ID)).thenReturn(progreso);
    return progreso;
  }

  private void givenQueElUsuarioNoTieneLogrosPrevios(Long usuarioId) {
    when(this.repositorioLogroMock.obtenerLogroPorUsuario(usuarioId)).thenReturn(new ArrayList<>());
  }

  private void givenQueElUsuarioYaTieneLosLogros(Long usuarioId, TipoLogro... tipos) {
    List<UsuarioLogro> obtenidos = new ArrayList<>();
    for (TipoLogro tipo : tipos) {
      obtenidos.add(new UsuarioLogro(this.usuario, tipo));
    }
    when(this.repositorioLogroMock.obtenerLogroPorUsuario(usuarioId)).thenReturn(obtenidos);
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
    givenQueElUsuarioYaTieneLosLogros(usuarioId, TipoLogro.values());
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

  private void whenSeEvaluanLosLogrosDelJugador(PartidaJugador jugador) {
    this.nuevosLogros = this.servicio.evaluarLogrosDelJugador(jugador);
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

  private void thenNoSeInteractuaConLosRepositoriosDeGuardado() {
    verifyNoInteractions(this.repositorioLogroMock);
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

  private void thenSeGuardaElProgresoCon(
    int aciertosTotales,
    int victoriasConsecutivas,
    Usuario usuarioEsperado
  ) {
    ArgumentCaptor<ProgresoUsuario> captor = ArgumentCaptor.forClass(ProgresoUsuario.class);
    verify(this.repositorioLogroMock).guardarProgreso(captor.capture());
    ProgresoUsuario guardado = captor.getValue();
    assertThat(guardado.getUsuario(), sameInstance(usuarioEsperado));
    assertThat(guardado.getAciertosTotales(), equalTo(aciertosTotales));
    assertThat(guardado.getVictoriasConsecutivas(), equalTo(victoriasConsecutivas));
  }

  private void thenElProgresoAcumuladoTiene(
    ProgresoUsuario progreso,
    int aciertosEsperados,
    int victoriasEsperadas
  ) {
    assertThat(progreso.getAciertosTotales(), equalTo(aciertosEsperados));
    assertThat(progreso.getVictoriasConsecutivas(), equalTo(victoriasEsperadas));
  }

  private void thenSeGuardaElProgreso(ProgresoUsuario progreso) {
    verify(this.repositorioLogroMock).guardarProgreso(progreso);
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
