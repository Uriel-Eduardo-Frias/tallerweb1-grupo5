package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.UsuarioLogro;
import java.util.List;

public interface RepositorioLogro {
  void guardarLogro(UsuarioLogro logro);
  List<UsuarioLogro> obtenerLogroPorUsuario(Long usuarioId);
}
