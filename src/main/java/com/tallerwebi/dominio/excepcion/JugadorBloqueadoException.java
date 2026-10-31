package com.tallerwebi.dominio.excepcion;

public class JugadorBloqueadoException extends RuntimeException {

    private final long segundosRestantes;

    public JugadorBloqueadoException(long segundosRestantes) {
        super("El jugador se encuentra bloqueado por un comodín.");
        this.segundosRestantes = segundosRestantes;
    }

    public long getSegundosRestantes() {
        return segundosRestantes;
    }
}