package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.tallerwebi.dominio.modelo.Categoria;
import org.junit.jupiter.api.Test;

public class CategoriaTest {

  @Test
  void dadoQueCreoUnaCategoriaCuandoIndicoNombreEntoncesLoAsigna() {
    Categoria categoria = new Categoria("Geografía");

    assertEquals("Geografía", categoria.getNombre());
  }

  @Test
  void dadoQueCreoUnaCategoriaVaciaCuandoConsultoSusDatosEntoncesDevuelveValoresIniciales() {
    Categoria categoria = new Categoria();

    assertNull(categoria.getId());
    assertNull(categoria.getNombre());
  }

  @Test
  void dadoQueTengoUnaCategoriaCuandoModificoSusDatosEntoncesDevuelveLosValoresActualizados() {
    Categoria categoria = new Categoria();

    categoria.setId(4L);
    categoria.setNombre("Deportes");

    assertEquals(4L, categoria.getId());
    assertEquals("Deportes", categoria.getNombre());
  }
}
