package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.PartidaJugador;
import com.tallerwebi.dominio.RepositorioPartidaJugador;
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
}
