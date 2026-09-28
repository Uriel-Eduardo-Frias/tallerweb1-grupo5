package com.tallerwebi.dominio;

public class SalaLlenaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SalaLlenaException(String mensaje){
        super(mensaje);
    }
}
