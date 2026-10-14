package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.tallerwebi.dominio.modelo.Opcion;
import com.tallerwebi.dominio.repositorio.RepositorioOpcion;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringWebTestConfig.class, HibernateTestConfig.class })
public class RepositorioOpcionImplTest {

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private RepositorioOpcion repositorio;

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarOpcionPorId() {
    Opcion opcion = givenTengoUnaOpcion("1492", true);

    limpiarCacheDeHibernate();

    Opcion buscada = whenBuscoOpcionPorId(opcion.getId());

    thenObtengoLaOpcion(buscada, "1492", true);
  }

  private Opcion givenTengoUnaOpcion(String texto, boolean esCorrecta) {
    Opcion opcion = new Opcion(texto, esCorrecta);
    sessionFactory.getCurrentSession().persist(opcion);
    sessionFactory.getCurrentSession().flush();
    return opcion;
  }

  private Opcion whenBuscoOpcionPorId(Long opcionId) {
    return repositorio.obtenerPorId(opcionId);
  }

  private void thenObtengoLaOpcion(
    Opcion buscada,
    String textoEsperado,
    boolean esCorrectaEsperada
  ) {
    assertThat(buscada, is(notNullValue()));
    assertThat(buscada.getTexto(), is(equalTo(textoEsperado)));
    assertThat(buscada.getEsCorrecta(), is(equalTo(esCorrectaEsperada)));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaRetornarNullCuandoNoExisteLaOpcion() {
    Long idInexistente = -999L;

    Opcion resultado = whenBuscoOpcionPorId(idInexistente);

    thenNoObtengoLaOpcion(resultado);
  }

  private void thenNoObtengoLaOpcion(Opcion resultado) {
    assertThat(resultado, is(nullValue()));
  }

  private void limpiarCacheDeHibernate() {
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();
  }
}
