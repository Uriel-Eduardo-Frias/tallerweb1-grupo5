package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.modelo.Usuario;

import java.util.List;

public interface ServicioUsuario {
  Usuario registrarUsuario(String username, String email, String password, String nombreCompleto)
    throws UsuarioExistente;

  Usuario autenticarUsuario(String username, String password);

  Usuario buscarUsuarioPorId(Long id);

  Usuario buscarUsuarioPorUsername(String username);

  List<Usuario> buscarUsuariosPorNombre(String nombre);

  void actualizarPerfil(Long idUsuario, String biografia, String avatarUrl);
}
