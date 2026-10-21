package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.enumeration.EstadoPartida;
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPartidaJugadorImpl implements RepositorioPartidaJugador {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioPartidaJugadorImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public PartidaJugador obtenerPorPartidaYUsuario(Long partidaId, Long usuarioId) {
    String hql =
      """
      SELECT pj
      FROM PartidaJugador pj
      WHERE pj.partida.id = :partidaId
        AND pj.usuario.id = :usuarioId
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, PartidaJugador.class)
      .setParameter("partidaId", partidaId)
      .setParameter("usuarioId", usuarioId)
      .uniqueResult();
  }

  @Override
  public void guardar(PartidaJugador jugador) {
    sessionFactory.getCurrentSession().persist(jugador);
  }

  @Override
  public List<PartidaJugador> obtenerPorPartida(Long partidaId) {
    String hql =
      """
      SELECT pj
      FROM PartidaJugador pj
      WHERE pj.partida.id = :partidaId
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, PartidaJugador.class)
      .setParameter("partidaId", partidaId)
      .getResultList();
  }

  @Override
  public List<PartidaJugador> listarPartidasFinalizadasDelUsuario(Long usuarioId) {
    String hql =
      """
      SELECT pj
      FROM PartidaJugador pj
      JOIN FETCH pj.partida p
      WHERE pj.usuario.id = :usuarioId
      AND p.estado = :estado
      ORDER BY p.fechaInicio DESC
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, PartidaJugador.class)
      .setParameter("usuarioId", usuarioId)
      .setParameter("estado", EstadoPartida.FINALIZADA)
      .getResultList();
  }

  @Override
  public List<PartidaJugador> listarTodasLasPartidasFinalizadas() {
    String hql =
      """
      SELECT pj
      FROM PartidaJugador pj
      JOIN FETCH pj.usuario u
      JOIN FETCH pj.partida p
      WHERE p.estado = :estado
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, PartidaJugador.class)
      .setParameter("estado", EstadoPartida.FINALIZADA)
      .getResultList();
  }
}
