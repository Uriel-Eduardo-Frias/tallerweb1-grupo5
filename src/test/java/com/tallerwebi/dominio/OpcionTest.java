package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Pregunta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpcionTest {
    @Test
    void dadoQueCreoUnaOpcionCuandoIndicoTextoYCorreccionEntoncesLosAsigna() {
        Opcion opcion = new Opcion("Buenos Aires", true);

        assertEquals("Buenos Aires", opcion.getTexto());
        assertTrue(opcion.getEsCorrecta());
    }

    @Test
    void dadoQueCreoUnaOpcionConConstructorVacioEntoncesLosValoresPrimitivosSonIniciales() {
        Opcion opcion = new Opcion();

        assertNull(opcion.getTexto());
        assertFalse(opcion.getEsCorrecta());
        assertNull(opcion.getPregunta());
    }

    @Test
    void dadoQueTengoUnaOpcionCuandoModificoSusDatosEntoncesDevuelveLosValoresActualizados() {
        Opcion opcion = new Opcion();
        Pregunta pregunta = new Pregunta();

        opcion.setId(8L);
        opcion.setTexto("Respuesta actualizada");
        opcion.setEsCorrecta(true);
        opcion.setPregunta(pregunta);

        assertEquals(8L, opcion.getId());
        assertEquals("Respuesta actualizada", opcion.getTexto());
        assertTrue(opcion.getEsCorrecta());
        assertSame(pregunta, opcion.getPregunta());
    }
}
