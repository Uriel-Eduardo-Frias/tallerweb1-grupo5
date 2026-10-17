package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.enumeration.TipoComodin;
import com.tallerwebi.dominio.modelo.Comodin;
import com.tallerwebi.dominio.modelo.PartidaJugadorComodin;
import com.tallerwebi.dominio.repositorio.RepositorioComodin;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("repositorioComodin")
public class RepositorioComodinImpl implements RepositorioComodin {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioComodinImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(PartidaJugadorComodin comodinJugador) {
        sessionFactory.getCurrentSession().persist(comodinJugador);
    }

    @Override
    public void actualizar(PartidaJugadorComodin comodinJugador) {
        sessionFactory.getCurrentSession().merge(comodinJugador);
    }

    @Override
    public PartidaJugadorComodin buscarPorJugadorYTipo(Long partidaJugadorId, TipoComodin tipo) {
        String hql = """
            SELECT pjc FROM PartidaJugadorComodin pjc
            JOIN FETCH pjc.comodin c
            WHERE pjc.partidaJugador.id = :partidaJugadorId
              AND c.codigo = :tipo
            """;
        return sessionFactory.getCurrentSession()
                .createQuery(hql, PartidaJugadorComodin.class)
                .setParameter("partidaJugadorId", partidaJugadorId)
                .setParameter("tipo", tipo)
                .uniqueResult();
    }

    @Override
    public List<PartidaJugadorComodin> obtenerPorPartidaJugador(Long partidaJugadorId) {
        String hql = """
            SELECT pjc FROM PartidaJugadorComodin pjc
            JOIN FETCH pjc.comodin c
            WHERE pjc.partidaJugador.id = :partidaJugadorId
            """;
        return sessionFactory.getCurrentSession()
                .createQuery(hql, PartidaJugadorComodin.class)
                .setParameter("partidaJugadorId", partidaJugadorId)
                .getResultList();
    }

    @Override
    public Comodin buscarComodinPorCodigo(TipoComodin tipo) {
        String hql = "FROM Comodin c WHERE c.codigo = :tipo";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Comodin.class)
                .setParameter("tipo", tipo)
                .uniqueResult();
    }

    @Override
    public List<Comodin> listarTodos() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Comodin", Comodin.class)
                .getResultList();
    }
}