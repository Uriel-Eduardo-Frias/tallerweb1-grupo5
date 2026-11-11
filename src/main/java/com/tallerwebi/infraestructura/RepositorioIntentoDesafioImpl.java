package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.modelo.IntentoDesafio;
import com.tallerwebi.dominio.repositorio.RepositorioIntentoDesafio;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioIntentoDesafioImpl implements RepositorioIntentoDesafio {

  private static final String SELECT_INTENTOS = "SELECT i FROM IntentoDesafio i ";

  private static final String CONDICION_FINALIZADO = "AND i.fechaFin IS NOT NULL ";

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioIntentoDesafioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public IntentoDesafio buscarPorUsuarioYDesafio(Long usuarioId, Long desafioId) {
    String hql =
      "SELECT DISTINCT i FROM IntentoDesafio i " +
      "LEFT JOIN FETCH i.respuestas " +
      "WHERE i.usuario.id = :usuarioId " +
      "AND i.desafio.id = :desafioId";

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, IntentoDesafio.class)
      .setParameter("usuarioId", usuarioId)
      .setParameter("desafioId", desafioId)
      .uniqueResult();
  }

  @Override
  public IntentoDesafio buscarIntentoPendiente(Long usuarioId, Long desafioId) {
    String hql =
      "SELECT i FROM IntentoDesafio i " +
      "WHERE i.usuario.id = :usuarioId " +
      "AND i.desafio.id = :desafioId " +
      "AND i.fechaFin IS NULL " +
      "ORDER BY i.fechaInicio DESC";

    List<IntentoDesafio> intentos = sessionFactory
      .getCurrentSession()
      .createQuery(hql, IntentoDesafio.class)
      .setParameter("usuarioId", usuarioId)
      .setParameter("desafioId", desafioId)
      .setMaxResults(1)
      .getResultList();

    if (intentos.isEmpty()) {
      return null;
    }

    return intentos.get(0);
  }

  @Override
  public List<IntentoDesafio> buscarRankingPorDesafio(Long desafioId) {
    return buscarFinalizadosPorDesafio(desafioId);
  }

  @Override
  public List<IntentoDesafio> buscarHistorialPorUsuario(Long usuarioId) {
    return buscarFinalizadosPorUsuario(usuarioId);
  }

  @Override
  public List<IntentoDesafio> buscarFinalizadosPorDesafio(Long desafioId) {
    String hql =
      SELECT_INTENTOS +
      "JOIN FETCH i.usuario " +
      "WHERE i.desafio.id = :desafioId " +
      CONDICION_FINALIZADO +
      "ORDER BY i.puntaje DESC";

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, IntentoDesafio.class)
      .setParameter("desafioId", desafioId)
      .getResultList();
  }

  @Override
  public List<IntentoDesafio> buscarFinalizadosPorUsuario(Long usuarioId) {
    String hql =
      SELECT_INTENTOS +
      "JOIN FETCH i.desafio " +
      "WHERE i.usuario.id = :usuarioId " +
      CONDICION_FINALIZADO +
      "ORDER BY i.desafio.fecha DESC";

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, IntentoDesafio.class)
      .setParameter("usuarioId", usuarioId)
      .getResultList();
  }

  @Override
  public void guardar(IntentoDesafio intento) {
    sessionFactory.getCurrentSession().persist(intento);
  }
}
