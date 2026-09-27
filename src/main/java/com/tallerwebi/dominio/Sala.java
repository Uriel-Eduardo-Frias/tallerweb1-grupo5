package com.tallerwebi.dominio;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Usuario usuario;

    public Sala(Long id, String nombre, Usuario usuario) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
