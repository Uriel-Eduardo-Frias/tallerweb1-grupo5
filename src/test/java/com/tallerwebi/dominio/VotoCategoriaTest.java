package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.tallerwebi.dominio.modelo.Categoria;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.modelo.VotacionCategoria;
import com.tallerwebi.dominio.modelo.VotoCategoria;
import org.junit.jupiter.api.Test;

public class VotoCategoriaTest {

  @Test
  public void dadoQueCreoUnVotoVacioCuandoConsultoSusAtributosObtengoValoresIniciales() {
    VotoCategoria voto = new VotoCategoria();

    assertNull(voto.getId());
    assertNull(voto.getVotacion());
    assertNull(voto.getUsuario());
    assertNull(voto.getCategoria());
  }

  @Test
  public void dadoQueCreoUnVotoConUsuarioYCategoriaCuandoConsultoObtengoLosMismosObjetos() {
    Usuario usuario = new Usuario();
    Categoria categoria = new Categoria();

    VotoCategoria voto = new VotoCategoria(usuario, categoria);

    assertSame(usuario, voto.getUsuario());
    assertSame(categoria, voto.getCategoria());
    assertNull(voto.getId());
    assertNull(voto.getVotacion());
  }

  @Test
  public void dadoQueTengoUnVotoCuandoAsignoUnaVotacionObtengoLaVotacionAsignada() {
    VotoCategoria voto = new VotoCategoria();
    VotacionCategoria votacion = new VotacionCategoria();

    voto.setVotacion(votacion);

    assertSame(votacion, voto.getVotacion());
  }

  @Test
  public void dadoQueTengoUnVotoCuandoCambioElUsuarioObtengoElUsuarioNuevo() {
    VotoCategoria voto = new VotoCategoria();
    Usuario usuario = new Usuario();

    voto.setUsuario(usuario);

    assertSame(usuario, voto.getUsuario());
  }

  @Test
  public void dadoQueTengoUnVotoCuandoCambioLaCategoriaObtengoLaCategoriaNueva() {
    VotoCategoria voto = new VotoCategoria();
    Categoria categoria = new Categoria();

    voto.setCategoria(categoria);

    assertSame(categoria, voto.getCategoria());
  }

  @Test
  public void dadoQueTengoUnVotoCuandoAsignoValoresNulosObtengoValoresNulos() {
    VotoCategoria voto = new VotoCategoria();

    voto.setVotacion(null);
    voto.setUsuario(null);
    voto.setCategoria(null);

    assertNull(voto.getVotacion());
    assertNull(voto.getUsuario());
    assertNull(voto.getCategoria());
  }
}
