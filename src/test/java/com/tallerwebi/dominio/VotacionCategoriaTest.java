package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.modelo.Categoria;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.modelo.VotacionCategoria;
import com.tallerwebi.dominio.modelo.VotoCategoria;
import org.junit.jupiter.api.Test;

public class VotacionCategoriaTest {

  @Test
  public void dadoQueCreoUnaVotacionVaciaCuandoConsultoSusAtributosObtengoValoresIniciales() {
    VotacionCategoria votacion = new VotacionCategoria();

    assertNull(votacion.getId());
    assertNull(votacion.getPartida());
    assertNull(votacion.getCategoriaElegida());
    assertNotNull(votacion.getVotosCategoria());
    assertTrue(votacion.getVotosCategoria().isEmpty());
  }

  @Test
  public void dadoQueCreoUnaVotacionConPartidaCuandoConsultoObtengoLaPartidaAsignada() {
    Partida partida = new Partida();
    VotacionCategoria votacion = new VotacionCategoria(partida);

    assertSame(partida, votacion.getPartida());
  }

  @Test
  public void dadoQueTengoUnaVotacionCuandoAgregoUnVotoObtengoElVotoEnLaListaYLaVotacionAsociada() {
    VotacionCategoria votacion = new VotacionCategoria();
    Usuario usuario = new Usuario();
    Categoria categoria = new Categoria();
    VotoCategoria voto = new VotoCategoria(usuario, categoria);

    votacion.agregarVoto(voto);

    assertEquals(1, votacion.getVotosCategoria().size());
    assertSame(voto, votacion.getVotosCategoria().get(0));
    assertSame(votacion, voto.getVotacion());
  }

  @Test
  public void dadoQueUnUsuarioYaVotoCuandoConsultoSiYaVotoObtengoVerdadero() {
    VotacionCategoria votacion = new VotacionCategoria();
    Usuario usuario = new Usuario();
    usuario.setId(1L);

    votacion.agregarVoto(new VotoCategoria(usuario, new Categoria()));

    assertTrue(votacion.yaVoto(1L));
  }

  @Test
  public void dadoQueUnUsuarioNoVotoCuandoConsultoSiYaVotoObtengoFalso() {
    VotacionCategoria votacion = new VotacionCategoria();
    Usuario usuario = new Usuario();
    usuario.setId(1L);

    votacion.agregarVoto(new VotoCategoria(usuario, new Categoria()));

    assertFalse(votacion.yaVoto(2L));
  }

  @Test
  public void dadoQueNoHayVotosCuandoConsultoSiTodosVotaronObtengoFalso() {
    VotacionCategoria votacion = new VotacionCategoria();

    assertFalse(votacion.todosVotaron(0));
    assertFalse(votacion.todosVotaron(3));
  }

  @Test
  public void dadoQueHayMenosVotosQueJugadoresCuandoConsultoSiTodosVotaronObtengoFalso() {
    VotacionCategoria votacion = new VotacionCategoria();

    votacion.agregarVoto(new VotoCategoria(new Usuario(), new Categoria()));

    assertFalse(votacion.todosVotaron(2));
  }

  @Test
  public void dadoQueHayTantosVotosComoJugadoresCuandoConsultoSiTodosVotaronObtengoVerdadero() {
    VotacionCategoria votacion = new VotacionCategoria();

    votacion.agregarVoto(new VotoCategoria(new Usuario(), new Categoria()));
    votacion.agregarVoto(new VotoCategoria(new Usuario(), new Categoria()));

    assertTrue(votacion.todosVotaron(2));
  }

  @Test
  public void dadoQueHayMasVotosQueJugadoresCuandoConsultoSiTodosVotaronObtengoVerdadero() {
    VotacionCategoria votacion = new VotacionCategoria();

    votacion.agregarVoto(new VotoCategoria(new Usuario(), new Categoria()));
    votacion.agregarVoto(new VotoCategoria(new Usuario(), new Categoria()));
    votacion.agregarVoto(new VotoCategoria(new Usuario(), new Categoria()));

    assertTrue(votacion.todosVotaron(2));
  }

  @Test
  public void dadoQueTengoUnaVotacionCuandoCambioLaPartidaObtengoLaPartidaNueva() {
    VotacionCategoria votacion = new VotacionCategoria();
    Partida partida = new Partida();

    votacion.setPartida(partida);

    assertSame(partida, votacion.getPartida());
  }

  @Test
  public void dadoQueTengoUnaVotacionCuandoSeleccionoUnaCategoriaObtengoLaCategoriaElegida() {
    VotacionCategoria votacion = new VotacionCategoria();
    Categoria categoria = new Categoria();

    votacion.setCategoriaElegida(categoria);

    assertSame(categoria, votacion.getCategoriaElegida());
  }

  @Test
  public void dadoQueTengoUnaCategoriaElegidaCuandoLaReinicioObtengoUnaCategoriaNula() {
    VotacionCategoria votacion = new VotacionCategoria();
    Categoria categoria = new Categoria();

    votacion.setCategoriaElegida(categoria);
    votacion.reiniciar();

    assertNull(votacion.getCategoriaElegida());
  }

  @Test
  public void dadoQueTengoVotosRegistradosCuandoReinicioObtengoUnaListaVacia() {
    VotacionCategoria votacion = new VotacionCategoria();

    votacion.agregarVoto(new VotoCategoria(new Usuario(), new Categoria()));
    votacion.agregarVoto(new VotoCategoria(new Usuario(), new Categoria()));

    votacion.reiniciar();

    assertTrue(votacion.getVotosCategoria().isEmpty());
  }

  @Test
  public void dadoQueTengoVotosYUnaCategoriaElegidaCuandoReinicioSeLimpiaTodaLaVotacion() {
    VotacionCategoria votacion = new VotacionCategoria();

    votacion.agregarVoto(new VotoCategoria(new Usuario(), new Categoria()));
    votacion.setCategoriaElegida(new Categoria());

    votacion.reiniciar();

    assertTrue(votacion.getVotosCategoria().isEmpty());
    assertNull(votacion.getCategoriaElegida());
  }
}
