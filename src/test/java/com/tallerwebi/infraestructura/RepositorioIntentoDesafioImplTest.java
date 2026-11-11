package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.modelo.IntentoDesafio;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioIntentoDesafioImplTest {

  private SessionFactory sessionFactory;
  private Session session;
  private RepositorioIntentoDesafioImpl repositorio;

  @BeforeEach
  public void preparar() {
    sessionFactory = mock(SessionFactory.class);
    session = mock(Session.class);

    when(sessionFactory.getCurrentSession()).thenReturn(session);

    repositorio = new RepositorioIntentoDesafioImpl(sessionFactory);
  }

  @Test
  public void buscarIntentoPendienteDevuelveElPrimeroDeLaLista() {
    IntentoDesafio intento = mock(IntentoDesafio.class);

    @SuppressWarnings("unchecked")
    Query<IntentoDesafio> query = mock(Query.class);

    when(session.createQuery(anyString(), eq(IntentoDesafio.class))).thenReturn(query);
    when(query.setParameter("usuarioId", 2L)).thenReturn(query);
    when(query.setParameter("desafioId", 3L)).thenReturn(query);
    when(query.setMaxResults(1)).thenReturn(query);
    when(query.getResultList()).thenReturn(Collections.singletonList(intento));

    IntentoDesafio resultado = repositorio.buscarIntentoPendiente(2L, 3L);

    assertSame(intento, resultado);
    verify(query).setMaxResults(1);
  }

  @Test
  public void buscarIntentoPendienteDevuelveNullSiNoEncuentraResultados() {
    @SuppressWarnings("unchecked")
    Query<IntentoDesafio> query = mock(Query.class);

    when(session.createQuery(anyString(), eq(IntentoDesafio.class))).thenReturn(query);
    when(query.setParameter("usuarioId", 2L)).thenReturn(query);
    when(query.setParameter("desafioId", 3L)).thenReturn(query);
    when(query.setMaxResults(1)).thenReturn(query);
    when(query.getResultList()).thenReturn(Collections.emptyList());

    IntentoDesafio resultado = repositorio.buscarIntentoPendiente(2L, 3L);

    assertNull(resultado);
  }

  @Test
  public void buscarFinalizadosPorDesafioDevuelveLaLista() {
    List<IntentoDesafio> intentos = Arrays.asList(
      mock(IntentoDesafio.class),
      mock(IntentoDesafio.class)
    );

    @SuppressWarnings("unchecked")
    Query<IntentoDesafio> query = mock(Query.class);

    when(session.createQuery(anyString(), eq(IntentoDesafio.class))).thenReturn(query);
    when(query.setParameter("desafioId", 3L)).thenReturn(query);
    when(query.getResultList()).thenReturn(intentos);

    List<IntentoDesafio> resultado = repositorio.buscarFinalizadosPorDesafio(3L);

    assertSame(intentos, resultado);
  }

  @Test
  public void buscarFinalizadosPorUsuarioDevuelveLaLista() {
    List<IntentoDesafio> intentos = Collections.singletonList(mock(IntentoDesafio.class));

    @SuppressWarnings("unchecked")
    Query<IntentoDesafio> query = mock(Query.class);

    when(session.createQuery(anyString(), eq(IntentoDesafio.class))).thenReturn(query);
    when(query.setParameter("usuarioId", 2L)).thenReturn(query);
    when(query.getResultList()).thenReturn(intentos);

    List<IntentoDesafio> resultado = repositorio.buscarFinalizadosPorUsuario(2L);

    assertSame(intentos, resultado);
  }

  @Test
  public void buscarRankingDelegaEnBusquedaDeFinalizados() {
    RepositorioIntentoDesafioImpl repositorioEspia = spy(repositorio);
    List<IntentoDesafio> intentos = Collections.singletonList(mock(IntentoDesafio.class));

    doReturn(intentos).when(repositorioEspia).buscarFinalizadosPorDesafio(3L);

    List<IntentoDesafio> resultado = repositorioEspia.buscarRankingPorDesafio(3L);

    assertSame(intentos, resultado);
    verify(repositorioEspia).buscarFinalizadosPorDesafio(3L);
  }

  @Test
  public void buscarHistorialDelegaEnBusquedaDeFinalizados() {
    RepositorioIntentoDesafioImpl repositorioEspia = spy(repositorio);
    List<IntentoDesafio> intentos = Collections.singletonList(mock(IntentoDesafio.class));

    doReturn(intentos).when(repositorioEspia).buscarFinalizadosPorUsuario(2L);

    List<IntentoDesafio> resultado = repositorioEspia.buscarHistorialPorUsuario(2L);

    assertSame(intentos, resultado);
    verify(repositorioEspia).buscarFinalizadosPorUsuario(2L);
  }

  @Test
  public void guardarPersisteElIntento() {
    IntentoDesafio intento = mock(IntentoDesafio.class);

    repositorio.guardar(intento);

    verify(session).persist(intento);
  }

  @Test
  public void buscarPorUsuarioYDesafioDevuelveElIntentoEncontrado() {
    IntentoDesafio intento = mock(IntentoDesafio.class);

    @SuppressWarnings("unchecked")
    Query<IntentoDesafio> query = mock(Query.class);

    when(session.createQuery(anyString(), eq(IntentoDesafio.class))).thenReturn(query);
    when(query.setParameter("usuarioId", 2L)).thenReturn(query);
    when(query.setParameter("desafioId", 3L)).thenReturn(query);
    when(query.uniqueResult()).thenReturn(intento);

    IntentoDesafio resultado = repositorio.buscarPorUsuarioYDesafio(2L, 3L);

    assertSame(intento, resultado);
    verify(query).uniqueResult();
  }
}
