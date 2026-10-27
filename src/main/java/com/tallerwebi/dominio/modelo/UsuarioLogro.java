package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enumeration.TipoLogro;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
  name = "usuarios_logros",
  uniqueConstraints = { @UniqueConstraint(columnNames = { "usuario_id", "tipo_logro" }) }
)
public class UsuarioLogro {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_logro", nullable = false, length = 30)
  private TipoLogro tipoLogro;

  @Column(name = "fecha_desbloqueo", nullable = false)
  private LocalDateTime fechaDesbloqueo;

  public UsuarioLogro() {}

  public UsuarioLogro(Usuario usuario, TipoLogro tipoLogro) {
    this.usuario = usuario;
    this.tipoLogro = tipoLogro;
    this.fechaDesbloqueo = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public TipoLogro getTipoLogro() {
    return tipoLogro;
  }

  public void setTipoLogro(TipoLogro tipoLogro) {
    this.tipoLogro = tipoLogro;
  }

  public LocalDateTime getFechaDesbloqueo() {
    return fechaDesbloqueo;
  }

  public void setFechaDesbloqueo(LocalDateTime fechaDesbloqueo) {
    this.fechaDesbloqueo = fechaDesbloqueo;
  }
}
