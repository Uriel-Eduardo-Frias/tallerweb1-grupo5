package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.modelo.Categoria;
import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.modelo.Pregunta;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaPregunta;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioPartidaPregunta")
public class RepositorioPartidaPreguntaImpl implements RepositorioPartidaPregunta {

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public List<Categoria> obtenerTodasLasCategorias() {
    String hql = "SELECT c FROM Categoria c";

    return sessionFactory.getCurrentSession().createQuery(hql, Categoria.class).getResultList();
  }

  @Override
  public Pregunta buscarPreguntaPorCategoria(Long categoriaId) {
    String hql =
      "SELECT DISTINCT p FROM Pregunta p " +
      "LEFT JOIN FETCH p.opciones o " +
      "WHERE p.categoria.id = :categoriaId";

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, Pregunta.class)
      .setParameter("categoriaId", categoriaId)
      .setMaxResults(1)
      .uniqueResult();
  }

  @Override
  public Opcion buscarOpcionPorId(Long opcionId) {
    String hql = "SELECT o FROM Opcion o WHERE o.id = :opcionId";

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, Opcion.class)
      .setParameter("opcionId", opcionId)
      .uniqueResult();
  }

  @Override
  public Pregunta buscarPreguntaPorOpcionId(Long opcionId) {
    String hql =
      "SELECT DISTINCT p FROM Pregunta p " +
      "LEFT JOIN FETCH p.opciones o " +
      "WHERE p.id = (SELECT op.pregunta.id FROM Opcion op WHERE op.id = :opcionId)";

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, Pregunta.class)
      .setParameter("opcionId", opcionId)
      .uniqueResult();
  }
}
