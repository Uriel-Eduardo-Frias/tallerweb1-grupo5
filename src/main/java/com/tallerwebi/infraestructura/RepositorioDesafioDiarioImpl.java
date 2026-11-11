package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.modelo.DesafioDiario;
import com.tallerwebi.dominio.modelo.Pregunta;
import com.tallerwebi.dominio.repositorio.RepositorioDesafioDiario;
import java.time.LocalDate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioDesafioDiarioImpl implements RepositorioDesafioDiario {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioDesafioDiarioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public DesafioDiario buscarPorFecha(LocalDate fecha) {
    DesafioDiario desafio = sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT DISTINCT d " +
        "FROM DesafioDiario d " +
        "LEFT JOIN FETCH d.preguntas pd " +
        "LEFT JOIN FETCH pd.pregunta " +
        "WHERE d.fecha = :fecha",
        DesafioDiario.class
      )
      .setParameter("fecha", fecha)
      .uniqueResult();

    if (desafio != null) {
      cargarOpcionesDePreguntas(desafio);
    }

    return desafio;
  }

  @Override
  public void guardar(DesafioDiario desafio) {
    sessionFactory.getCurrentSession().persist(desafio);
  }

  private void cargarOpcionesDePreguntas(DesafioDiario desafio) {
    sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT DISTINCT p " +
        "FROM Pregunta p " +
        "LEFT JOIN FETCH p.opciones " +
        "WHERE p.identificador IN (" +
        "    SELECT pd.pregunta.identificador " +
        "    FROM PreguntaDesafio pd " +
        "    WHERE pd.desafio.id = :desafioId" +
        ")",
        Pregunta.class
      )
      .setParameter("desafioId", desafio.getId())
      .getResultList();
  }
}
