package com.tallerwebi.dominio;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.never;

import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.enumeration.ModoJuego;
import com.tallerwebi.dominio.excepcion.PartidaNoEncontradaException;
import com.tallerwebi.dominio.excepcion.UsuarioNoEsHostException;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.ServicioResultadoPartida;
import com.tallerwebi.dominio.servicio.ServicioRonda;
import com.tallerwebi.dominio.servicio.ServicioSala;
import com.tallerwebi.dominio.servicio.ServicioVotacionCategoria;
import com.tallerwebi.dominio.servicio.impl.ServicioPartidaImp;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

class ServicioPartidaImpRevanchaTest {

  private static final Long PARTIDA_ID = 1L;
  private static final Long ID_HOST = 10L;
  private static final Long ID_OTRO_USUARIO = 20L;
  private static final String CODIGO_SALA_NUEVA = "NUEVA1";

  private ServicioSala servicioSala;
  private RepositorioPartida repositorioPartida;
  private ServicioVotacionCategoria servicioVotacionCategoria;
  private RepositorioPartidaJugador repositorioPartidaJugador;
  private ServicioRonda servicioRonda;
  private ServicioResultadoPartida servicioResultadoPartida;

  private ServicioPartidaImp servicio;

  @BeforeEach
  void preparar() {
    servicioSala = mock(ServicioSala.class);
    repositorioPartida = mock(RepositorioPartida.class);
    servicioVotacionCategoria = mock(ServicioVotacionCategoria.class);
    repositorioPartidaJugador = mock(RepositorioPartidaJugador.class);
    servicioRonda = mock(ServicioRonda.class);
    servicioResultadoPartida = mock(ServicioResultadoPartida.class);

    servicio =
      new ServicioPartidaImp(
        servicioSala,
        repositorioPartida,
        servicioVotacionCategoria,
        repositorioPartidaJugador,
        servicioRonda,
        servicioResultadoPartida
      );
  }

  @Test
  void crearRevanchaCreaSalaConLaMismaConfiguracionYGuardaElCodigo() {
    Usuario host = usuarioConId(ID_HOST);
    Sala salaAnterior = salaConHost(host);
    when(salaAnterior.getNombre()).thenReturn("Trivia del viernes");
    when(salaAnterior.getMaxJugadores()).thenReturn(6);
    when(salaAnterior.getTotalRondas()).thenReturn(8);
    when(salaAnterior.getModoJuego()).thenReturn(ModoJuego.TURNO_TODOS);
    when(salaAnterior.isEsPrivada()).thenReturn(true);

    Partida partida = partida(salaAnterior, EstadoPartida.FINALIZADA);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);

    Sala salaNueva = mock(Sala.class);
    when(salaNueva.getCodigo()).thenReturn(CODIGO_SALA_NUEVA);
    when(servicioSala.crearSala("Trivia del viernes", host, 6, 8, ModoJuego.TURNO_TODOS, true))
      .thenReturn(salaNueva);

    String codigo = servicio.crearRevancha(PARTIDA_ID, host);

    assertEquals(CODIGO_SALA_NUEVA, codigo);
    assertEquals(CODIGO_SALA_NUEVA, partida.getSalaRevanchaCodigo());
  }

  @Test
  void crearRevanchaEsIdempotenteYNoCreaOtraSalaSiYaExiste() {
    Usuario host = usuarioConId(ID_HOST);
    Sala salaAnterior = salaConHost(host);

    Partida partida = partida(salaAnterior, EstadoPartida.FINALIZADA);
    partida.setSalaRevanchaCodigo("YAEXISTE");
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);

    String codigo = servicio.crearRevancha(PARTIDA_ID, host);

    assertEquals("YAEXISTE", codigo);
  }

  @Test
  void crearRevanchaLanzaExcepcionSiElSolicitanteNoEsElHost() {
    Usuario host = usuarioConId(ID_HOST);
    Usuario otro = usuarioConId(ID_OTRO_USUARIO);
    Sala salaAnterior = salaConHost(host);

    Partida partida = partida(salaAnterior, EstadoPartida.FINALIZADA);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);

    assertThrows(UsuarioNoEsHostException.class, () -> servicio.crearRevancha(PARTIDA_ID, otro));

    assertNull(partida.getSalaRevanchaCodigo());
  }

  @Test
  void crearRevanchaLanzaExcepcionSiLaPartidaNoFinalizo() {
    // Sin stubs: el estado se valida antes de mirar quién es el host
    Usuario host = mock(Usuario.class);
    Partida partida = partida(mock(Sala.class), EstadoPartida.EN_CURSO);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);

    assertThrows(IllegalStateException.class, () -> servicio.crearRevancha(PARTIDA_ID, host));
  }

  @Test
  void crearRevanchaLanzaExcepcionSiLaPartidaNoExiste() {
    Usuario host = mock(Usuario.class);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(null);

    assertThrows(
      PartidaNoEncontradaException.class,
      () -> servicio.crearRevancha(PARTIDA_ID, host)
    );
  }

  // ---------- unirseARevancha ----------

  @Test
  void unirseARevanchaUneAlJugadorALaSalaNuevaYDevuelveSuCodigo() {
    Partida partida = partida(mock(Sala.class), EstadoPartida.FINALIZADA);
    partida.setSalaRevanchaCodigo(CODIGO_SALA_NUEVA);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);
    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(PARTIDA_ID, ID_OTRO_USUARIO))
      .thenReturn(mock(PartidaJugador.class));

    Sala salaNueva = mock(Sala.class);
    when(salaNueva.getCodigo()).thenReturn(CODIGO_SALA_NUEVA);
    when(servicioSala.unirse(CODIGO_SALA_NUEVA, ID_OTRO_USUARIO)).thenReturn(salaNueva);

    String codigo = servicio.unirseARevancha(PARTIDA_ID, ID_OTRO_USUARIO);

    assertEquals(CODIGO_SALA_NUEVA, codigo);
  }

  @Test
  void unirseARevanchaLanzaExcepcionSiElUsuarioNoParticipoEnLaPartida() {
    Partida partida = partida(mock(Sala.class), EstadoPartida.FINALIZADA);
    partida.setSalaRevanchaCodigo(CODIGO_SALA_NUEVA);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);
    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(PARTIDA_ID, ID_OTRO_USUARIO))
      .thenReturn(null);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicio.unirseARevancha(PARTIDA_ID, ID_OTRO_USUARIO)
    );
  }

  @Test
  void unirseARevanchaLanzaExcepcionSiTodaviaNoSeCreoLaRevancha() {
    Partida partida = partida(mock(Sala.class), EstadoPartida.FINALIZADA);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);
    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(PARTIDA_ID, ID_OTRO_USUARIO))
      .thenReturn(mock(PartidaJugador.class));

    assertThrows(
      IllegalStateException.class,
      () -> servicio.unirseARevancha(PARTIDA_ID, ID_OTRO_USUARIO)
    );
  }

  // ---------- obtenerSalaRevancha ----------

  @Test
  void obtenerSalaRevanchaDevuelveElCodigoSiExiste() {
    Partida partida = partida(mock(Sala.class), EstadoPartida.FINALIZADA);
    partida.setSalaRevanchaCodigo(CODIGO_SALA_NUEVA);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);
    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(PARTIDA_ID, ID_OTRO_USUARIO))
      .thenReturn(mock(PartidaJugador.class));

    assertEquals(CODIGO_SALA_NUEVA, servicio.obtenerSalaRevancha(PARTIDA_ID, ID_OTRO_USUARIO));
  }

  @Test
  void obtenerSalaRevanchaDevuelveNullSiTodaviaNoSeCreo() {
    Partida partida = partida(mock(Sala.class), EstadoPartida.FINALIZADA);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);
    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(PARTIDA_ID, ID_OTRO_USUARIO))
      .thenReturn(mock(PartidaJugador.class));

    assertNull(servicio.obtenerSalaRevancha(PARTIDA_ID, ID_OTRO_USUARIO));
  }

  @Test
  void obtenerSalaRevanchaLanzaExcepcionSiElUsuarioNoParticipo() {
    Partida partida = partida(mock(Sala.class), EstadoPartida.FINALIZADA);
    partida.setSalaRevanchaCodigo(CODIGO_SALA_NUEVA);
    when(repositorioPartida.obtenerPorIdConRondas(PARTIDA_ID)).thenReturn(partida);
    when(repositorioPartidaJugador.obtenerPorPartidaYUsuario(PARTIDA_ID, ID_OTRO_USUARIO))
      .thenReturn(null);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicio.obtenerSalaRevancha(PARTIDA_ID, ID_OTRO_USUARIO)
    );
  }

  // ---------- helpers ----------

  private Usuario usuarioConId(Long id) {
    Usuario usuario = mock(Usuario.class);
    when(usuario.getId()).thenReturn(id);
    return usuario;
  }

  private Sala salaConHost(Usuario host) {
    Sala sala = mock(Sala.class);
    when(sala.getHost()).thenReturn(host);
    return sala;
  }

  private Partida partida(Sala sala, EstadoPartida estado) {
    return new Partida(sala, estado);
  }
}
