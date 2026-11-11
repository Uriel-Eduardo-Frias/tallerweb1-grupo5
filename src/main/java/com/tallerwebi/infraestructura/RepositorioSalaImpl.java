package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.enumeration.EstadoSala;
import com.tallerwebi.dominio.modelo.Sala;
import com.tallerwebi.dominio.repositorio.RepositorioSala;
import java.util.ArrayList;
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

  @Override
  public List<Sala> listarSalasPublicasEnEspera() {
    String hql =
      """
      SELECT DISTINCT s
      FROM Sala s
      LEFT JOIN FETCH s.host
      LEFT JOIN FETCH s.jugadores sj
      LEFT JOIN FETCH sj.usuario
      WHERE s.esPrivada = false
      AND s.estado = :estado
      AND s.host IS NOT NULL
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, Sala.class)
      .setParameter("estado", EstadoSala.EN_ESPERA)
      .getResultList();
  }

  @Override
  public List<Sala> listarSalasPublicasConLugar(int cantidad) {
    if (cantidad <= 0) {
      return List.of();
    }

    String hqlCodigos =
      """
      SELECT s.codigo
      FROM Sala s
      WHERE s.esPrivada = false
        AND s.estado = :estado
        AND s.host IS NOT NULL
        AND SIZE(s.jugadores) < s.maxJugadores
      ORDER BY (s.maxJugadores - SIZE(s.jugadores)) ASC, function('RAND')
      """;

    List<String> codigos = sessionFactory
      .getCurrentSession()
      .createQuery(hqlCodigos, String.class)
      .setParameter("estado", EstadoSala.EN_ESPERA)
      .setMaxResults(cantidad)
      .getResultList();

    if (codigos.isEmpty()) {
      return List.of();
    }

    String hqlSalas =
      """
      SELECT DISTINCT s
      FROM Sala s
      LEFT JOIN FETCH s.host
      LEFT JOIN FETCH s.jugadores sj
      LEFT JOIN FETCH sj.usuario
      WHERE s.codigo IN (:codigos)
      """;

    List<Sala> salasDesordenadas = sessionFactory
      .getCurrentSession()
      .createQuery(hqlSalas, Sala.class)
      .setParameter("codigos", codigos)
      .getResultList();

    java.util.Map<String, Sala> mapaSalas = new java.util.HashMap<>();
    for (Sala sala : salasDesordenadas) {
      mapaSalas.put(sala.getCodigo(), sala);
    }

    List<Sala> salasOrdenadas = new ArrayList<>();
    for (String codigo : codigos) {
      salasOrdenadas.add(mapaSalas.get(codigo));
    }

    return salasOrdenadas;
  }
}
