package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.servicio.ServicioComodin;
import com.tallerwebi.presentacion.dto.Comodin5050DTO;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

public class ControladorComodinTest {

    private static final Long PARTIDA_ID = 1L;
    private static final Long USUARIO_ID = 10L;
    private static final String ID_USUARIO = "ID_USUARIO";

    private ServicioComodin servicioComodin;
    private ControladorComodin controlador;

    @BeforeEach
    public void init() {
        servicioComodin = mock(ServicioComodin.class);
        controlador = new ControladorComodin(servicioComodin);
    }

    @Test
    public void tiempoExtraDeberiaRetornarDiezSegundosSiUsuarioEstaAutenticado() {
        HttpSession session = givenTengoUnaSesionValida(USUARIO_ID);
        givenServicioRetornaSegundosExtra(PARTIDA_ID, USUARIO_ID, 10);

        ResponseEntity<Map<String, Integer>> respuesta = whenSolicitoTiempoExtra(PARTIDA_ID, session);

        thenLaRespuestaTieneStatus(respuesta, HttpStatus.OK);
        thenLosSegundosExtraSon(respuesta, 10);
        verify(servicioComodin).aplicarTiempoExtra(PARTIDA_ID, USUARIO_ID);
    }

    @Test
    public void tiempoExtraDeberiaRetornarUnauthorizedSiNoHaySesion() {
        HttpSession sessionSinUsuario = givenTengoUnaSesionInvalida();

        ResponseEntity<Map<String, Integer>> respuesta = whenSolicitoTiempoExtra(PARTIDA_ID, sessionSinUsuario);

        thenLaRespuestaTieneStatus(respuesta, HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void cincuentaCincuentaDeberiaRetornarDosOpcionesDescartadasYPenalizacion() {
        HttpSession session = givenTengoUnaSesionValida(USUARIO_ID);
        Comodin5050DTO dtoEsperado = new Comodin5050DTO(List.of(2L, 3L), 5);
        givenServicioRetornaCincuentaCincuenta(PARTIDA_ID, USUARIO_ID, dtoEsperado);

        ResponseEntity<Comodin5050DTO> respuesta = whenSolicito5050(PARTIDA_ID, session);

        thenLaRespuestaTieneStatus(respuesta, HttpStatus.OK);
        thenElResultado5050EsValido(respuesta, 2, 5);
        verify(servicioComodin).aplicarCincuentaCincuenta(PARTIDA_ID, USUARIO_ID);
    }

    @Test
    public void cincuentaCincuentaDeberiaRetornarUnauthorizedSiNoHaySesion() {
        HttpSession sessionSinUsuario = givenTengoUnaSesionInvalida();

        ResponseEntity<Comodin5050DTO> respuesta = whenSolicito5050(PARTIDA_ID, sessionSinUsuario);

        thenLaRespuestaTieneStatus(respuesta, HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void dobleChanceDeberiaActivarComodinYRetornarOkSiUsuarioEstaAutenticado() {
        HttpSession session = givenTengoUnaSesionValida(USUARIO_ID);

        ResponseEntity<Map<String, Boolean>> respuesta = whenSolicitoDobleChance(PARTIDA_ID, session);

        thenLaRespuestaTieneStatus(respuesta, HttpStatus.OK);
        thenDobleChanceEstaActivado(respuesta);
        verify(servicioComodin).activarDobleChance(PARTIDA_ID, USUARIO_ID);
    }

    @Test
    public void dobleChanceDeberiaRetornarUnauthorizedSiNoHaySesion() {
        HttpSession sessionSinUsuario = givenTengoUnaSesionInvalida();

        ResponseEntity<Map<String, Boolean>> respuesta = whenSolicitoDobleChance(PARTIDA_ID, sessionSinUsuario);

        thenLaRespuestaTieneStatus(respuesta, HttpStatus.UNAUTHORIZED);
    }

    private HttpSession givenTengoUnaSesionValida(Long usuarioId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(ID_USUARIO, usuarioId);
        return session;
    }

    private HttpSession givenTengoUnaSesionInvalida() {
        return new MockHttpSession();
    }

    private void givenServicioRetornaSegundosExtra(Long partidaId, Long usuarioId, int segundos) {
        when(servicioComodin.aplicarTiempoExtra(partidaId, usuarioId)).thenReturn(segundos);
    }

    private void givenServicioRetornaCincuentaCincuenta(Long partidaId, Long usuarioId, Comodin5050DTO dto) {
        when(servicioComodin.aplicarCincuentaCincuenta(partidaId, usuarioId)).thenReturn(dto);
    }

    private ResponseEntity<Map<String, Integer>> whenSolicitoTiempoExtra(Long partidaId, HttpSession session) {
        return controlador.tiempoExtra(partidaId, session);
    }

    private ResponseEntity<Comodin5050DTO> whenSolicito5050(Long partidaId, HttpSession session) {
        return controlador.cincuentaCincuenta(partidaId, session);
    }

    private ResponseEntity<Map<String, Boolean>> whenSolicitoDobleChance(Long partidaId, HttpSession session) {
        return controlador.dobleChance(partidaId, session);
    }

    private void thenLaRespuestaTieneStatus(ResponseEntity<?> respuesta, HttpStatus statusEsperado) {
        assertThat(respuesta, notNullValue());
        assertThat(respuesta.getStatusCode(), equalTo(statusEsperado));
    }

    private void thenLosSegundosExtraSon(ResponseEntity<Map<String, Integer>> respuesta, int segundosEsperados) {
        assertThat(respuesta.getBody(), notNullValue());
        assertThat(respuesta.getBody().get("segundosExtra"), equalTo(segundosEsperados));
    }

    private void thenElResultado5050EsValido(ResponseEntity<Comodin5050DTO> respuesta, int cantidadDescartadas, int penalizacionEsperada) {
        Comodin5050DTO cuerpo = respuesta.getBody();
        assertThat(cuerpo, notNullValue());
        assertThat(cuerpo.getOpcionesDescartadas(), hasSize(cantidadDescartadas));
        assertThat(cuerpo.getPenalizacionSegundos(), equalTo(penalizacionEsperada));
    }

    private void thenDobleChanceEstaActivado(ResponseEntity<Map<String, Boolean>> respuesta) {
        assertThat(respuesta.getBody(), notNullValue());
        assertThat(respuesta.getBody().get("activado"), equalTo(Boolean.TRUE));
    }
}