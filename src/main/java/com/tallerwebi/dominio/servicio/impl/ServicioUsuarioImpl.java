package com.tallerwebi.dominio.servicio.impl;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.modelo.PerfilUsuario;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ServicioUsuarioImpl implements ServicioUsuario {

  private final RepositorioUsuario repositorioUsuario;

  // Constructor
  public ServicioUsuarioImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public Usuario registrarUsuario(
    String username,
    String email,
    String password,
    String nombreCompleto
  ) throws UsuarioExistente {
    if (
      repositorioUsuario.buscarPorUsername(username) != null ||
      repositorioUsuario.buscar(email) != null
    ) {
      throw new UsuarioExistente();
    }

    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    usuario.setEmail(email);
    usuario.setPassword(password);
    usuario.setNombreCompleto(nombreCompleto);
    usuario.setRol("COMUN");

    PerfilUsuario perfil = new PerfilUsuario();
    perfil.setUsuario(usuario);
    usuario.setPerfil(perfil);

    // El ID lo genera Hibernate; Usuario tiene cascade hacia PerfilUsuario.
    repositorioUsuario.guardar(usuario);
    return usuario;
  }

  @Override
  public Usuario autenticarUsuario(String username, String password) {
    if (username == null || password == null) {
      return null;
    }

    return repositorioUsuario.buscarPorUsernameYPassword(username, password);
  }

  @Override
  public Usuario buscarUsuarioPorId(Long id) {
    if (id == null) {
      return null;
    }

    Usuario usuario = repositorioUsuario.buscarPorId(id);
    usuario = asegurarPerfil(usuario);

    return usuario;
  }

  @Override
  public Usuario buscarUsuarioPorUsername(String username) {
    if (username == null) {
      return null;
    }

    return repositorioUsuario.buscarPorUsername(username);
  }

  @Override
  public List<Usuario> buscarUsuariosPorNombre(String terminoBusqueda) {
    if (terminoBusqueda == null || terminoBusqueda.isBlank()) {
      return Collections.emptyList();
    }

    return repositorioUsuario.buscarUsuariosPorNombre(terminoBusqueda.trim());
  }

  @Override
  public void actualizarPerfil(Long idUsuario, String biografia, String avatarUrl) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    usuario = asegurarPerfil(usuario);

    if (usuario == null) {
      return;
    }

    PerfilUsuario perfil = usuario.getPerfil();
    perfil.setBiografia(biografia);

    if (avatarUrl == null || avatarUrl.isBlank()) {
      perfil.setAvatarUrl(null);
    } else {
      perfil.setAvatarUrl(avatarUrl);
    }
  }

  private Usuario asegurarPerfil(Usuario usuario) {
    if (usuario == null) {
      return null;
    }

    if (usuario.getPerfil() == null) {
      PerfilUsuario perfilNuevo = new PerfilUsuario();
      perfilNuevo.setUsuario(usuario);
      usuario.setPerfil(perfilNuevo);
    }

    return usuario;
  }
}
