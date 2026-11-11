package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.modelo.DesafioDiario;
import com.tallerwebi.dominio.modelo.Pregunta;
import java.time.LocalDate;
import java.util.Collections;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioDesafioDiarioImplTest {

  private SessionFactory sessionFactory;
  private Session session;
  private RepositorioDesafioDiarioImpl repositorio;

  @BeforeEach
  public void preparar() {
    sessionFactory = mock(SessionFactory.class);
    session = mock(Session.class);

    when(sessionFactory.getCurrentSession()).thenReturn(session);

    repositorio = new RepositorioDesafioDiarioImpl(sessionFactory);
  }

  @Test
  public void buscarPorFechaDevuelveDesafioYCargaOpciones() {
    LocalDate fecha = LocalDate.now();
    DesafioDiario desafio = mock(DesafioDiario.class);

    @SuppressWarnings("unchecked")
    Query<DesafioDiario> queryDesafio = mock(Query.class);

    @SuppressWarnings("unchecked")
    Query<Pregunta> queryPreguntas = mock(Query.class);

    when(session.createQuery(anyString(), eq(DesafioDiario.class))).thenReturn(queryDesafio);
    when(queryDesafio.setParameter("fecha", fecha)).thenReturn(queryDesafio);
    when(queryDesafio.uniqueResult()).thenReturn(desafio);
    when(desafio.getId()).thenReturn(4L);

    when(session.createQuery(anyString(), eq(Pregunta.class))).thenReturn(queryPreguntas);
    when(queryPreguntas.setParameter("desafioId", 4L)).thenReturn(queryPreguntas);
    when(queryPreguntas.getResultList()).thenReturn(Collections.emptyList());

    DesafioDiario resultado = repositorio.buscarPorFecha(fecha);

    assertSame(desafio, resultado);
    verify(queryDesafio).uniqueResult();
    verify(queryPreguntas).getResultList();
  }

  @Test
  public void buscarPorFechaDevuelveNullSiNoExisteDesafio() {
    LocalDate fecha = LocalDate.now();

    @SuppressWarnings("unchecked")
    Query<DesafioDiario> queryDesafio = mock(Query.class);

    when(session.createQuery(anyString(), eq(DesafioDiario.class))).thenReturn(queryDesafio);
    when(queryDesafio.setParameter("fecha", fecha)).thenReturn(queryDesafio);
    when(queryDesafio.uniqueResult()).thenReturn(null);

    DesafioDiario resultado = repositorio.buscarPorFecha(fecha);

    assertNull(resultado);
    verify(session, never()).createQuery(anyString(), eq(Pregunta.class));
  }

  @Test
  public void guardarPersisteElDesafio() {
    DesafioDiario desafio = mock(DesafioDiario.class);

    repositorio.guardar(desafio);

    verify(session).persist(desafio);
  }
}
