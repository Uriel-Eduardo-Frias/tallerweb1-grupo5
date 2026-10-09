package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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

    return repositorioUsuario.buscarPorId(id);
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

    if (usuario == null || usuario.getPerfil() == null) {
      return;
    }

    usuario.getPerfil().setBiografia(biografia);

    if (avatarUrl == null || avatarUrl.isBlank()) {
      usuario.getPerfil().setAvatarUrl(null);
    } else {
      usuario.getPerfil().setAvatarUrl(avatarUrl);
    }
    // El usuario está gestionado dentro de la transacción; Hibernate
    // persiste los cambios al confirmarla.
  }
}
