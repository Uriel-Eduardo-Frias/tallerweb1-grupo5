package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.*;
import java.util.List;
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

  @Override
  public List<Comodin> listarComodines() {
    String hql =
      """
      FROM Comodin c
      """;

    return sessionFactory.getCurrentSession().createQuery(hql, Comodin.class).getResultList();
  }

  @Override
  public Comodin buscarComodinPorCodigo(TipoComodin codigo) {
    if (codigo == null) {
      return null;
    }

    String hql =
      """
      FROM Comodin c
      WHERE c.codigo = :codigo
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, Comodin.class)
      .setParameter("codigo", codigo)
      .uniqueResult();
  }

  @Override
  public List<PartidaJugador> listarPartidasDeUsuario(Long usuarioId) {
    String hql =
      """
      SELECT pj
      FROM PartidaJugador pj
      JOIN FETCH pj.partida p
      WHERE pj.usuario.id = :usuarioId
      ORDER BY p.fechaInicio DESC
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, PartidaJugador.class)
      .setParameter("usuarioId", usuarioId)
      .setMaxResults(20)
      .getResultList();
  }
}
