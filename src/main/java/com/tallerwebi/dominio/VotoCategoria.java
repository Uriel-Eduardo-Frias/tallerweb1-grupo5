package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
@Table(
  name = "votos_categoria",
  uniqueConstraints = @UniqueConstraint(columnNames = { "votacion_id", "usuario_id" })
)
public class VotoCategoria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "votacion_id", nullable = false)
  private VotacionCategoria votacion;

  @ManyToOne(optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @ManyToOne(optional = false)
  @JoinColumn(name = "categoria_id", nullable = false)
  private Categoria categoria;

  public VotoCategoria() {}

  public VotoCategoria(Usuario usuario, Categoria categoria) {
    this.usuario = usuario;
    this.categoria = categoria;
  }

  public Long getId() {
    return id;
  }

  public VotacionCategoria getVotacion() {
    return votacion;
  }

  public void setVotacion(VotacionCategoria votacion) {
    this.votacion = votacion;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Categoria getCategoria() {
    return categoria;
  }

  public void setCategoria(Categoria categoria) {
    this.categoria = categoria;
  }
}
