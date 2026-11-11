package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.modelo.Pregunta;
import com.tallerwebi.dominio.repositorio.RepositorioPregunta;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPreguntaImpl implements RepositorioPregunta {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioPreguntaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<Pregunta> buscarAleatorias(int cantidad) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("SELECT p FROM Pregunta p ORDER BY function('RAND')", Pregunta.class)
      .setMaxResults(cantidad)
      .getResultList();
  }
}
