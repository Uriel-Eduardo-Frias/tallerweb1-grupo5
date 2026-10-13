package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.modelo.RespuestaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioRespuestaJugador;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioRespuestaJugadorImpl implements RepositorioRespuestaJugador {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioRespuestaJugadorImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(RespuestaJugador respuesta) {
    sessionFactory.getCurrentSession().persist(respuesta);
  }
}
