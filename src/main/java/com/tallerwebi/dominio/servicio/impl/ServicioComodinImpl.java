package com.tallerwebi.dominio.servicio.impl;
import com.tallerwebi.dominio.enumeration.TipoComodin;
import com.tallerwebi.dominio.modelo.Comodin;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.PartidaJugadorComodin;
import com.tallerwebi.dominio.modelo.PartidaRonda;
import com.tallerwebi.dominio.modelo.Pregunta;
import com.tallerwebi.dominio.repositorio.RepositorioComodin;
import com.tallerwebi.dominio.repositorio.RepositorioPartida;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import com.tallerwebi.dominio.servicio.ServicioComodin;
import com.tallerwebi.dominio.servicio.ServicioPartidaPregunta;
import com.tallerwebi.presentacion.dto.Comodin5050DTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioComodin")
@Transactional
public class ServicioComodinImpl implements ServicioComodin {

    private final RepositorioComodin repositorioComodin;
    private final RepositorioPartida repositorioPartida;
    private final RepositorioPartidaJugador repositorioPartidaJugador;
    private final ServicioPartidaPregunta servicioPartidaPregunta;

    @Autowired
    public ServicioComodinImpl(
            RepositorioComodin repositorioComodin,
            RepositorioPartida repositorioPartida,
            RepositorioPartidaJugador repositorioPartidaJugador,
            ServicioPartidaPregunta servicioPartidaPregunta
    ) {
        this.repositorioComodin = repositorioComodin;
        this.repositorioPartida = repositorioPartida;
        this.repositorioPartidaJugador = repositorioPartidaJugador;
        this.servicioPartidaPregunta = servicioPartidaPregunta;
    }

    @Override
    public void inicializarComodinesParaJugador(PartidaJugador jugador) {
        List<Comodin> comodines = repositorioComodin.listarTodos();
        for (Comodin comodin : comodines) {
            PartidaJugadorComodin pjc = new PartidaJugadorComodin(jugador, comodin, 1);
            repositorioComodin.guardar(pjc);
        }
    }

    @Override
    public List<PartidaJugadorComodin> obtenerComodinesDeJugador(Long partidaId, Long usuarioId) {
        PartidaJugador jugador = obtenerJugador(partidaId, usuarioId);
        return repositorioComodin.obtenerPorPartidaJugador(jugador.getId());
    }

    @Override
    public int aplicarTiempoExtra(Long partidaId, Long usuarioId) {
        consumirComodinValido(partidaId, usuarioId, TipoComodin.TIEMPO_EXTRA);
        return 10;
    }

    @Override
    public Comodin5050DTO aplicarCincuentaCincuenta(Long partidaId, Long usuarioId) {
        consumirComodinValido(partidaId, usuarioId, TipoComodin.CINCUENTA_CINCUENTA);

        Partida partida = repositorioPartida.obtenerPorIdConRondas(partidaId);
        PartidaRonda ronda = partida.obtenerRondaActual();
        List<Opcion> opciones = ronda.getPregunta().getOpciones();

        List<Long> incorrectas = new ArrayList<>();
        for (Opcion op : opciones) {
            if (!Boolean.TRUE.equals(op.getEsCorrecta())) {
                incorrectas.add(op.getId());
            }
        }

        Collections.shuffle(incorrectas);
        List<Long> descartadas = incorrectas.subList(0, 2);
        int penalizacionSegundos = 5;

        return new Comodin5050DTO(descartadas, penalizacionSegundos);
    }

    @Override
    public Pregunta aplicarPasarPregunta(Long partidaId, Long usuarioId) {
        consumirComodinValido(partidaId, usuarioId, TipoComodin.PASAR_PREGUNTA);

        Partida partida = repositorioPartida.obtenerPorIdConRondas(partidaId);
        PartidaRonda ronda = partida.obtenerRondaActual();
        Long categoriaId = ronda.getPregunta().getCategoria().getId();

        List<Long> idsUsadas = new ArrayList<>();
        for (PartidaRonda r : partida.getRondas()) {
            if (r.getPregunta() != null) {
                idsUsadas.add(r.getPregunta().getIdentificador());
            }
        }

        Pregunta nuevaPregunta = servicioPartidaPregunta.obtenerPreguntaPorCategoria(categoriaId, idsUsadas);
        ronda.setPregunta(nuevaPregunta);

        return nuevaPregunta;
    }

    @Override
    public void activarDobleChance(Long partidaId, Long usuarioId) {
        consumirComodinValido(partidaId, usuarioId, TipoComodin.DOBLE_CHANCE);

        Partida partida = repositorioPartida.obtenerPorIdConRondas(partidaId);
        PartidaJugador jugador = obtenerJugador(partidaId, usuarioId);
        PartidaRonda ronda = partida.obtenerRondaActual();

        jugador.setDobleChanceRondaId(ronda.getId());
        repositorioPartidaJugador.guardar(jugador);
    }

    private PartidaJugadorComodin consumirComodinValido(Long partidaId, Long usuarioId, TipoComodin tipo) {
        PartidaJugador jugador = obtenerJugador(partidaId, usuarioId);
        PartidaJugadorComodin pjc = repositorioComodin.buscarPorJugadorYTipo(jugador.getId(), tipo);

        if (pjc == null || pjc.getCantidadDisponible() <= 0) {
            throw new IllegalStateException("El comodín " + tipo + " ya no se encuentra disponible.");
        }

        pjc.setCantidadDisponible(pjc.getCantidadDisponible() - 1);
        pjc.setVecesUsado(pjc.getVecesUsado() + 1);
        repositorioComodin.actualizar(pjc);
        return pjc;
    }

    private PartidaJugador obtenerJugador(Long partidaId, Long usuarioId) {
        PartidaJugador pj = repositorioPartidaJugador.obtenerPorPartidaYUsuario(partidaId, usuarioId);
        if (pj == null) {
            throw new IllegalArgumentException("El jugador no participa en la partida.");
        }
        return pj;
    }
}