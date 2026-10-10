package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.repositorio.RepositorioSala;
import com.tallerwebi.dominio.modelo.Sala;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorio sala")
public class RepositorioSalaImpl implements RepositorioSala {

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Sala obtenerSalaPorCodigo(String codigo) {
    String query = "FROM Sala s WHERE s.codigo = :codigo";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Sala.class)
      .setParameter("codigo", codigo)
      .uniqueResult();
  }

  @Override
  public void guardar(Sala sala) {
    sessionFactory.getCurrentSession().persist(sala);
  }

  @Override
  public List<Sala> listarSalas() {
    String hql =
      """
      SELECT DISTINCT s
      FROM Sala s
      LEFT JOIN FETCH s.host
      LEFT JOIN FETCH s.jugadores sj
      LEFT JOIN FETCH sj.usuario
      """;

    return sessionFactory.getCurrentSession().createQuery(hql, Sala.class).getResultList();
  }

  @Override
  public Sala obtenerPorCodigoConJugadores(String codigo) {
    String hql =
      """
      SELECT DISTINCT s
      FROM Sala s
      LEFT JOIN FETCH s.host
      LEFT JOIN FETCH s.jugadores sj
      LEFT JOIN FETCH sj.usuario
      WHERE s.codigo = :codigo
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, Sala.class)
      .setParameter("codigo", codigo)
      .uniqueResult();
  }
}
