package com.tallerwebi.dominio;

public class SalaNoEncontradaException extends RuntimeException {
    public SalaNoEncontradaException(String mensaje){
        super(mensaje);
    }
}
