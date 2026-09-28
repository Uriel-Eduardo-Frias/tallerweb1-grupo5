package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServicioPartidaImpl implements ServicioPartida {

    private Map<Long, Categoria> tablaCategorias = new HashMap<>();
    private Map<Long, Pregunta> tablaPreguntas = new HashMap<>();

    public ServicioPartidaImpl() {
        inicializarDatosMock();
    }

    private void inicializarDatosMock() {

        Categoria categoriaHistoria = new Categoria("Historia");
        categoriaHistoria.setId(1L);
        tablaCategorias.put(categoriaHistoria.getId(), categoriaHistoria);

        Pregunta preguntaHistoria = new Pregunta("¿En qué año se descubrió América?", categoriaHistoria);
        preguntaHistoria.setIdentificador(100L);

        Opcion opcionHistoriaUno = new Opcion("1492", true);
        opcionHistoriaUno.setId(1L);
        preguntaHistoria.agregarOpcion(opcionHistoriaUno);

        Opcion opcionHistoriaDos = new Opcion("1810", false);
        opcionHistoriaDos.setId(2L);
        preguntaHistoria.agregarOpcion(opcionHistoriaDos);

        Opcion opcionHistoriaTres = new Opcion("1776", false);
        opcionHistoriaTres.setId(3L);
        preguntaHistoria.agregarOpcion(opcionHistoriaTres);

        Opcion opcionHistoriaCuatro = new Opcion("1914", false);
        opcionHistoriaCuatro.setId(4L);
        preguntaHistoria.agregarOpcion(opcionHistoriaCuatro);

        tablaPreguntas.put(categoriaHistoria.getId(), preguntaHistoria);


        Categoria categoriaCiencia = new Categoria("Ciencia");
        categoriaCiencia.setId(2L);
        tablaCategorias.put(categoriaCiencia.getId(), categoriaCiencia);

        Pregunta preguntaCiencia = new Pregunta("¿Cuál es el símbolo químico del agua?", categoriaCiencia);
        preguntaCiencia.setIdentificador(101L);

        Opcion opcionCienciaUno = new Opcion("CO2", false);
        opcionCienciaUno.setId(5L);
        preguntaCiencia.agregarOpcion(opcionCienciaUno);

        Opcion opcionCienciaDos = new Opcion("H2O", true);
        opcionCienciaDos.setId(6L);
        preguntaCiencia.agregarOpcion(opcionCienciaDos);

        Opcion opcionCienciaTres = new Opcion("O2", false);
        opcionCienciaTres.setId(7L);
        preguntaCiencia.agregarOpcion(opcionCienciaTres);

        Opcion opcionCienciaCuatro = new Opcion("HO", false);
        opcionCienciaCuatro.setId(8L);
        preguntaCiencia.agregarOpcion(opcionCienciaCuatro);

        tablaPreguntas.put(categoriaCiencia.getId(), preguntaCiencia);
    }

    @Override
    public Map<Long, Categoria> obtenerCategorias() {
        return tablaCategorias;
    }

    @Override
    public Pregunta obtenerPreguntaPorCategoria(Long identificadorCategoria) {
        return tablaPreguntas.get(identificadorCategoria);
    }
}