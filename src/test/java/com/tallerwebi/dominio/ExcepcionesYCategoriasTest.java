package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tallerwebi.dominio.excepcion.Categorias;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import org.junit.jupiter.api.Test;

public class ExcepcionesYCategoriasTest {

  @Test
  public void usuarioExistenteDeberiaPoderLanzarse() {
    assertThrows(
      UsuarioExistente.class,
      () -> {
        throw new UsuarioExistente();
      }
    );
  }

  @Test
  public void usuarioNoEncontradoDeberiaPoderLanzarse() {
    assertThrows(
      UsuarioNoEncontrado.class,
      () -> {
        throw new UsuarioNoEncontrado();
      }
    );
  }

  @Test
  public void categoriasDeberiaTenerSeisValores() {
    assertEquals(6, Categorias.values().length);
    assertEquals(Categorias.ARTE, Categorias.valueOf("ARTE"));
    assertEquals(Categorias.HISTORIA, Categorias.valueOf("HISTORIA"));
  }
}
