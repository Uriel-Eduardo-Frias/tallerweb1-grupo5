package com.tallerwebi.infraestructura;

<<<<<<< HEAD
import com.tallerwebi.dominio.modelo.PartidaJugador;
import com.tallerwebi.dominio.repositorio.RepositorioPartidaJugador;
=======
import com.tallerwebi.dominio.PartidaJugador;
import com.tallerwebi.dominio.RepositorioPartidaJugador;
import java.util.List;
>>>>>>> 3455d42 (se añaden nuevos cambios)
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPartidaJugadorImpl implements RepositorioPartidaJugador {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioPartidaJugadorImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public PartidaJugador obtenerPorPartidaYUsuario(Long partidaId, Long usuarioId) {
    String hql =
      """
      SELECT pj
      FROM PartidaJugador pj
      WHERE pj.partida.id = :partidaId
        AND pj.usuario.id = :usuarioId
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, PartidaJugador.class)
      .setParameter("partidaId", partidaId)
      .setParameter("usuarioId", usuarioId)
      .uniqueResult();
  }

  @Override
  public void guardar(PartidaJugador jugador) {
    sessionFactory.getCurrentSession().persist(jugador);
  }

  @Override
  public List<PartidaJugador> obtenerPorPartida(Long partidaId) {
    String hql =
      """
      SELECT pj
      FROM PartidaJugador pj
      WHERE pj.partida.id = :partidaId
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, PartidaJugador.class)
      .setParameter("partidaId", partidaId)
      .getResultList();
  }
}
