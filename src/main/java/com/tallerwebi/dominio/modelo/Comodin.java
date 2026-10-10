package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.TipoComodin;
import jakarta.persistence.*;

@Entity
@Table(name = "comodines")
public class Comodin {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true, length = 30)
  private TipoComodin codigo;

  @Column(nullable = false, length = 50)
  private String nombre;

  @Column(nullable = false, length = 200)
  private String descripcion;

  @Column(length = 50)
  private String icono;

  public Comodin() {}

  public Comodin(TipoComodin codigo, String nombre, String descripcion, String icono) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.icono = icono;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TipoComodin getCodigo() {
    return codigo;
  }

  public void setCodigo(TipoComodin codigo) {
    this.codigo = codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getIcono() {
    return icono;
  }

  public void setIcono(String icono) {
    this.icono = icono;
  }
}
