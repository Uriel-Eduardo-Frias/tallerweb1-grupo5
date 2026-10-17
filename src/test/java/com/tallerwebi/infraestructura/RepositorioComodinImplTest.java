package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.enumeration.TipoComodin;
import com.tallerwebi.dominio.modelo.Comodin;
import com.tallerwebi.dominio.modelo.Partida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.modelo.PartidaJugadorComodin;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioComodin;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import java.util.List;
import java.util.UUID;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringWebTestConfig.class, HibernateTestConfig.class })
public class RepositorioComodinImplTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private RepositorioComodin repositorio;


    @Test
    @Transactional
    @Rollback
    public void deberiaGuardarUnComodinDeJugador() {
        Usuario usuario = givenTengoUnUsuario();
        Partida partida = givenTengoUnaPartida(usuario);
        PartidaJugador jugador = givenTengoUnJugador(partida, usuario);
        Comodin comodin = givenTengoUnComodin(TipoComodin.TIEMPO_EXTRA);
        PartidaJugadorComodin comodinJugador = givenCreoUnComodinParaJugador(jugador, comodin, 1);

        whenGuardoComodinDeJugador(comodinJugador);

        thenElComodinDeJugadorQuedaPersistido(comodinJugador);
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaBuscarComodinPorJugadorYTipo() {
        Usuario usuario = givenTengoUnUsuario();
        Partida partida = givenTengoUnaPartida(usuario);
        PartidaJugador jugador = givenTengoUnJugador(partida, usuario);
        Comodin comodin = givenTengoUnComodin(TipoComodin.CINCUENTA_CINCUENTA);
        PartidaJugadorComodin comodinGuardado = givenTengoUnComodinParaJugador(jugador, comodin, 1);

        limpiarCacheDeHibernate();

        PartidaJugadorComodin resultado = whenBuscoComodinPorJugadorYTipo(jugador.getId(), TipoComodin.CINCUENTA_CINCUENTA);

        thenObtengoElComodinDeJugador(resultado, comodinGuardado.getId(), TipoComodin.CINCUENTA_CINCUENTA, 1);
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaRetornarNullCuandoElJugadorNoTieneEseTipoDeComodin() {
        Usuario usuario = givenTengoUnUsuario();
        Partida partida = givenTengoUnaPartida(usuario);
        PartidaJugador jugador = givenTengoUnJugador(partida, usuario);

        limpiarCacheDeHibernate();

        PartidaJugadorComodin resultado = whenBuscoComodinPorJugadorYTipo(jugador.getId(), TipoComodin.DOBLE_CHANCE);

        thenNoObtengoComodinDeJugador(resultado);
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaActualizarCantidadDisponibleCuandoSeConsumeElComodin() {
        Usuario usuario = givenTengoUnUsuario();
        Partida partida = givenTengoUnaPartida(usuario);
        PartidaJugador jugador = givenTengoUnJugador(partida, usuario);
        Comodin comodin = givenTengoUnComodin(TipoComodin.PASAR_PREGUNTA);
        PartidaJugadorComodin comodinJugador = givenTengoUnComodinParaJugador(jugador, comodin, 1);

        whenConsumoElComodin(comodinJugador);

        thenLaCantidadDisponibleEs(comodinJugador.getId(), 0);
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaObtenerTodosLosComodinesDeUnJugador() {
        Usuario usuario = givenTengoUnUsuario();
        Partida partida = givenTengoUnaPartida(usuario);
        PartidaJugador jugador = givenTengoUnJugador(partida, usuario);

        Comodin comodinUno = givenTengoUnComodin(TipoComodin.CINCUENTA_CINCUENTA);
        Comodin comodinDos = givenTengoUnComodin(TipoComodin.TIEMPO_EXTRA);
        givenTengoUnComodinParaJugador(jugador, comodinUno, 1);
        givenTengoUnComodinParaJugador(jugador, comodinDos, 1);

        limpiarCacheDeHibernate();

        List<PartidaJugadorComodin> resultado = whenObtengoComodinesPorPartidaJugador(jugador.getId());

        thenObtengoCantidadDeComodines(resultado, 2);
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaRetornarListaVaciaCuandoElJugadorNoTieneComodinesAsignados() {
        Usuario usuario = givenTengoUnUsuario();
        Partida partida = givenTengoUnaPartida(usuario);
        PartidaJugador jugador = givenTengoUnJugador(partida, usuario);

        limpiarCacheDeHibernate();

        List<PartidaJugadorComodin> resultado = whenObtengoComodinesPorPartidaJugador(jugador.getId());

        thenObtengoListaVacia(resultado);
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaBuscarComodinPorCodigo() {
        TipoComodin codigo = TipoComodin.DOBLE_CHANCE;
        givenTengoUnComodin(codigo);

        limpiarCacheDeHibernate();

        Comodin resultado = whenBuscoComodinPorCodigo(codigo);

        thenObtengoComodin(resultado, codigo);
    }

    private Usuario givenTengoUnUsuario() {
        String identificador = UUID.randomUUID().toString();

        Usuario usuario = new Usuario();
        usuario.setUsername("jugador-" + identificador.substring(0, 8));
        usuario.setEmail(identificador + "@test.com");

        sessionFactory.getCurrentSession().persist(usuario);
        sessionFactory.getCurrentSession().flush();

        return usuario;
    }

    private Partida givenTengoUnaPartida(Usuario host) {
        Sala sala = new Sala(generarCodigoSala(), "Sala de Prueba", host);
        sessionFactory.getCurrentSession().persist(sala);
        sessionFactory.getCurrentSession().flush();

        Partida partida = new Partida(sala, EstadoPartida.EN_CURSO);
        sessionFactory.getCurrentSession().persist(partida);
        sessionFactory.getCurrentSession().flush();

        return partida;
    }

    private PartidaJugador givenTengoUnJugador(Partida partida, Usuario usuario) {
        PartidaJugador jugador = new PartidaJugador(partida, usuario);
        sessionFactory.getCurrentSession().persist(jugador);
        sessionFactory.getCurrentSession().flush();

        return jugador;
    }

    private Comodin givenTengoUnComodin(TipoComodin codigo) {
        Comodin comodin = new Comodin(codigo, codigo.getEtiqueta(), codigo.getDescripcion(), null);
        sessionFactory.getCurrentSession().persist(comodin);
        sessionFactory.getCurrentSession().flush();

        return comodin;
    }

    private PartidaJugadorComodin givenCreoUnComodinParaJugador(PartidaJugador jugador, Comodin comodin, int cantidad) {
        return new PartidaJugadorComodin(jugador, comodin, cantidad);
    }

    private PartidaJugadorComodin givenTengoUnComodinParaJugador(PartidaJugador jugador, Comodin comodin, int cantidad) {
        PartidaJugadorComodin comodinJugador = givenCreoUnComodinParaJugador(jugador, comodin, cantidad);
        sessionFactory.getCurrentSession().persist(comodinJugador);
        sessionFactory.getCurrentSession().flush();

        return comodinJugador;
    }

    private String generarCodigoSala() {
        return "S" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }



    private void whenGuardoComodinDeJugador(PartidaJugadorComodin comodinJugador) {
        repositorio.guardar(comodinJugador);
        sessionFactory.getCurrentSession().flush();
    }

    private PartidaJugadorComodin whenBuscoComodinPorJugadorYTipo(Long partidaJugadorId, TipoComodin tipo) {
        return repositorio.buscarPorJugadorYTipo(partidaJugadorId, tipo);
    }

    private void whenConsumoElComodin(PartidaJugadorComodin comodinJugador) {
        comodinJugador.setCantidadDisponible(comodinJugador.getCantidadDisponible() - 1);
        comodinJugador.setVecesUsado(comodinJugador.getVecesUsado() + 1);
        repositorio.actualizar(comodinJugador);
        sessionFactory.getCurrentSession().flush();
    }

    private List<PartidaJugadorComodin> whenObtengoComodinesPorPartidaJugador(Long partidaJugadorId) {
        return repositorio.obtenerPorPartidaJugador(partidaJugadorId);
    }

    private Comodin whenBuscoComodinPorCodigo(TipoComodin codigo) {
        return repositorio.buscarComodinPorCodigo(codigo);
    }


    private void thenElComodinDeJugadorQuedaPersistido(PartidaJugadorComodin comodinJugador) {
        assertThat(comodinJugador.getId(), is(notNullValue()));

        limpiarCacheDeHibernate();

        PartidaJugadorComodin buscado = sessionFactory
                .getCurrentSession()
                .get(PartidaJugadorComodin.class, comodinJugador.getId());

        assertThat(buscado, is(notNullValue()));
        assertThat(buscado.getId(), is(equalTo(comodinJugador.getId())));
    }

    private void thenObtengoElComodinDeJugador(
            PartidaJugadorComodin resultado,
            Long idEsperado,
            TipoComodin codigoEsperado,
            int cantidadEsperada
    ) {
        assertThat(resultado, is(notNullValue()));
        assertThat(resultado.getId(), is(equalTo(idEsperado)));
        assertThat(resultado.getComodin().getCodigo(), is(equalTo(codigoEsperado)));
        assertThat(resultado.getCantidadDisponible(), is(equalTo(cantidadEsperada)));
    }

    private void thenNoObtengoComodinDeJugador(PartidaJugadorComodin resultado) {
        assertThat(resultado, is(nullValue()));
    }

    private void thenLaCantidadDisponibleEs(Long comodinJugadorId, int cantidadEsperada) {
        limpiarCacheDeHibernate();

        PartidaJugadorComodin actualizado = sessionFactory
                .getCurrentSession()
                .get(PartidaJugadorComodin.class, comodinJugadorId);

        assertThat(actualizado, is(notNullValue()));
        assertThat(actualizado.getCantidadDisponible(), is(equalTo(cantidadEsperada)));
    }

    private void thenObtengoCantidadDeComodines(List<PartidaJugadorComodin> resultados, int cantidadEsperada) {
        assertThat(resultados, is(notNullValue()));
        assertThat(resultados.size(), is(equalTo(cantidadEsperada)));
    }

    private void thenObtengoListaVacia(List<?> resultados) {
        assertThat(resultados, is(notNullValue()));
        assertThat(resultados.isEmpty(), is(true));
    }

    private void thenObtengoComodin(Comodin resultado, TipoComodin codigoEsperado) {
        assertThat(resultado, is(notNullValue()));
        assertThat(resultado.getCodigo(), is(equalTo(codigoEsperado)));
    }



    private void limpiarCacheDeHibernate() {
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
    }
}