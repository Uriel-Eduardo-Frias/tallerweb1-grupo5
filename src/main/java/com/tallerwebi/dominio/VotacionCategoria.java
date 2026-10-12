package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "votaciones_categoria")
public class VotacionCategoria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "partida_id", nullable = false, unique = true)
  private Partida partida;

  @OneToMany(mappedBy = "votacion", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<VotoCategoria> votosCategoria = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoria_elegida_id")
  private Categoria categoriaElegida;

  public VotacionCategoria() {}

  public VotacionCategoria(Partida partida) {
    this.partida = partida;
  }

  public void agregarVoto(VotoCategoria voto) {
    voto.setVotacion(this);
    votosCategoria.add(voto);
  }

  public void setPartida(Partida partida) {
    this.partida = partida;
  }

  public boolean yaVoto(Long usuarioId) {
    for (VotoCategoria voto : votosCategoria) {
      if (voto.getUsuario().getId().equals(usuarioId)) {
        return true;
      }
    }

    return false;
  }

  public Long getId() {
    return id;
  }

  public Partida getPartida() {
    return partida;
  }

  public List<VotoCategoria> getVotosCategoria() {
    return votosCategoria;
  }

  public Categoria getCategoriaElegida() {
    return categoriaElegida;
  }

  public void setCategoriaElegida(Categoria categoriaElegida) {
    this.categoriaElegida = categoriaElegida;
  }

  public boolean todosVotaron(int cantidadJugadores) {
    return cantidadJugadores > 0 && votosCategoria.size() >= cantidadJugadores;
  }
}
