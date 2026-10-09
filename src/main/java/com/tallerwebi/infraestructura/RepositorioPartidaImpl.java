package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.RepositorioPartida;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioPartida")
public class RepositorioPartidaImpl implements RepositorioPartida {

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Partida obtenerPorId(Long id) {
    String hql =
      """
      SELECT DISTINCT p
      FROM Partida p
      JOIN FETCH p.sala s
      LEFT JOIN FETCH s.host
      LEFT JOIN FETCH s.jugadores sj
      LEFT JOIN FETCH sj.usuario
      WHERE p.id = :id
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, Partida.class)
      .setParameter("id", id)
      .uniqueResult();
  }

  @Override
  public Partida obtenerPorCodigoSala(String codigoSala) {
    String query =
      """
      FROM Partida p
      WHERE p.sala.codigo = :codigoSala
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Partida.class)
      .setParameter("codigoSala", codigoSala)
      .uniqueResult();
  }

  @Override
  public void guardar(Partida partida) {
    sessionFactory.getCurrentSession().persist(partida);
  }
}
