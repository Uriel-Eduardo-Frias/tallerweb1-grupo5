package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.repositorio.RepositorioOpcion;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioOpcionImpl implements RepositorioOpcion {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioOpcionImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Opcion obtenerPorId(Long opcionId) {
    return sessionFactory.getCurrentSession().get(Opcion.class, opcionId);
  }
}
