package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class ServicioUsuarioImpl implements ServicioUsuario {

  // Atributos
  private final List<Usuario> usuariosMock;
  private Long generarId = 5L;

  // Constructor
  public ServicioUsuarioImpl() {
    this.usuariosMock = new ArrayList<Usuario>();

    Usuario admin = new Usuario();
    admin.setId(1L);
    admin.setUsername("admin");
    admin.setEmail("admin@trivia.com");
    admin.setPassword("1234");
    admin.setNombreCompleto("Administrador del sistema");

    PerfilUsuario perfil = new PerfilUsuario();
    perfil.setNivel(99);
    perfil.setExperiencia(9999);
    perfil.setUsuario(admin);
    admin.setPerfil(perfil);

    Usuario pepito = new Usuario();
    pepito.setId(2L);
    pepito.setUsername("pepito");
    pepito.setEmail("pepito@trivia.com");
    pepito.setPassword("1234");
    pepito.setNombreCompleto("Pepito Jugador");

    PerfilUsuario perfilPepito = new PerfilUsuario();
    perfilPepito.setNivel(12);
    perfilPepito.setExperiencia(1500);
    perfilPepito.setUsuario(pepito);
    pepito.setPerfil(perfilPepito);

    Usuario sofia = new Usuario();
    sofia.setId(3L);
    sofia.setUsername("sofia");
    sofia.setEmail("sofia@trivia.com");
    sofia.setPassword("1234");
    sofia.setNombreCompleto("Sofia Experta");

    PerfilUsuario perfilSofia = new PerfilUsuario();
    perfilSofia.setNivel(35);
    perfilSofia.setExperiencia(8200);
    perfilSofia.setUsuario(sofia);
    sofia.setPerfil(perfilSofia);

    this.usuariosMock.add(sofia);
    this.usuariosMock.add(pepito);
    this.usuariosMock.add(admin);
  }

  @Override
  public Usuario registrarUsuario(
    String username,
    String email,
    String password,
    String nombreCompleto
  ) throws UsuarioExistente {
    // Verifico si el usuario existe
    if (buscarUsuarioPorUsername(username) != null) {
      throw new RuntimeException("El nombre de usuario ya esta en uso");
    }

    // Creacion del nuevo usuario y asignacion del perfil
    Usuario nuevoUsuario = new Usuario();
    nuevoUsuario.setId(generarId);
    generarId++;
    nuevoUsuario.setUsername(username);
    nuevoUsuario.setEmail(email);
    nuevoUsuario.setPassword(password);
    nuevoUsuario.setNombreCompleto(nombreCompleto);

    PerfilUsuario nuevoPerfil = new PerfilUsuario();
    nuevoPerfil.setUsuario(nuevoUsuario);
    nuevoUsuario.setPerfil(nuevoPerfil);

    this.usuariosMock.add(nuevoUsuario);

    return nuevoUsuario;
  }

  @Override
  public Usuario autenticarUsuario(String username, String password) {
    Usuario usuario = buscarUsuarioPorUsername(username);

    if (usuario != null && usuario.getPassword().equals(password)) {
      return usuario;
    }
    return null;
  }

  @Override
  public Usuario buscarUsuarioPorId(Long id) {
    for (Usuario usuario : usuariosMock) {
      if (usuario.getId().equals(id)) {
        return usuario;
      }
    }
    return null;
  }

  @Override
  public Usuario buscarUsuarioPorUsername(String username) {
    for (Usuario usuario : usuariosMock) {
      if (usuario.getUsername().equals(username)) {
        return usuario;
      }
    }
    return null;
  }

  @Override
  public List<Usuario> buscarUsuariosPorNombre(String terminoBusqueda) {
    List<Usuario> usuariosEncontrados = new ArrayList<>();

    if (terminoBusqueda == null || terminoBusqueda.isEmpty()) {
      return new ArrayList<>();
    }

    String busquedaMinuscula = terminoBusqueda.toLowerCase(Locale.ROOT);

    for (Usuario usuario : usuariosMock) {
      if (usuario.getUsername() != null) {
        String nombreUsuario = usuario.getUsername().toLowerCase(Locale.ROOT);

        if (nombreUsuario.contains(busquedaMinuscula)) {
          usuariosEncontrados.add(usuario);
        }
      }
    }

    return usuariosEncontrados;
  }

  @Override
  public void actualizarPerfil(Long idUsuario, String biografia, String avatarUrl) {
    Usuario usuario = buscarUsuarioPorId(idUsuario);
    if (usuario != null && usuario.getPerfil() != null) {
      usuario.getPerfil().setBiografia(biografia);

      if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
        usuario.getPerfil().setAvatarUrl(null);
      } else {
        usuario.getPerfil().setAvatarUrl(avatarUrl);
      }
    }
  }
}
