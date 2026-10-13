package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.modelo.Usuario;

import java.util.List;

public interface RepositorioUsuario {
  Usuario buscarUsuario(String email, String password);
  Usuario buscarPorId(Long id);
  Usuario buscarPorUsername(String username);
  Usuario buscarPorUsernameYPassword(String username, String password);
  List<Usuario> buscarUsuariosPorNombre(String termino);
  void guardar(Usuario usuario);
  Usuario buscar(String email);
  void modificar(Usuario usuario);
}
