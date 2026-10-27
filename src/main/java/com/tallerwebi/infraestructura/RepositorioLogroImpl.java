package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.modelo.UsuarioLogro;
import com.tallerwebi.dominio.repositorio.RepositorioLogro;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioLogro")
public class RepositorioLogroImpl implements RepositorioLogro {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioLogroImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardarLogro(UsuarioLogro logro) {
    sessionFactory.getCurrentSession().persist(logro);
  }

  @Override
  public List<UsuarioLogro> obtenerLogroPorUsuario(Long usuarioId) {
    String hql =
      """
      SELECT ul FROM UsuarioLogro ul
      WHERE ul.usuario.id = :usuarioId
      """;

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, UsuarioLogro.class)
      .setParameter("usuarioId", usuarioId)
      .getResultList();
  }
}
