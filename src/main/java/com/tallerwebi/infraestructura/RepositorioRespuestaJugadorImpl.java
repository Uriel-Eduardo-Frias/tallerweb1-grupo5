package com.tallerwebi.infraestructura;

<<<<<<< HEAD
import com.tallerwebi.dominio.modelo.RespuestaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioRespuestaJugador;
=======
import com.tallerwebi.dominio.RepositorioRespuestaJugador;
import com.tallerwebi.dominio.RespuestaJugador;
import java.util.List;
>>>>>>> 3455d42 (se añaden nuevos cambios)
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

  @Override
  public boolean existeRespuesta(Long preguntaId, Long partidaJugadorId) {
    String hql =
      """
      SELECT COUNT(r)
      FROM RespuestaJugador r
      WHERE r.pregunta.id = :preguntaId
        AND r.partidaJugador.id = :partidaJugadorId
      """;

    Long cantidad = sessionFactory
      .getCurrentSession()
      .createQuery(hql, Long.class)
      .setParameter("preguntaId", preguntaId)
      .setParameter("partidaJugadorId", partidaJugadorId)
      .uniqueResult();

    return cantidad != null && cantidad > 0;
  }

  @Override
  public List<RespuestaJugador> obtenerPorPregunta(Long preguntaId) {
    String hql =
      """
      SELECT r
      FROM RespuestaJugador r
      WHERE r.pregunta.id = :preguntaId
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, RespuestaJugador.class)
      .setParameter("preguntaId", preguntaId)
      .getResultList();
  }
}
