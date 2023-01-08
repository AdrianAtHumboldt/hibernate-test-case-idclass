package org.hibernate.bugs;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private static EntityManagerFactory entityManagerFactory;

	private static long s_id;

	@BeforeClass
	public static void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		EntityV v1 = new EntityV();
		entityManager.persist(v1);
		EntityS s1 = new EntityS();
		entityManager.persist(s1);
		Link l = new Link();
		l.v = v1;
		l.s = s1;
		l.attribute = 1;
		entityManager.persist(l);

		EntityX x1 = new EntityX();
		x1.link = l;
		entityManager.persist(x1);

		s_id = s1.id;

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@AfterClass
	public static void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void lookupByEntityTest() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		entityManager.getTransaction().begin();

		EntityS s2 = entityManager.find(EntityS.class, s_id);
		TypedQuery<EntityX> q = entityManager.createQuery(
				"select x from EntityX x where x.link.attribute = ?1 and x.link.s = ?2",
				EntityX.class);
		q.setParameter(1, 1);
		// Fails with "Parameter value [org.hibernate.bugs.EntityS@2d22d3b1] did not match expected type [java.lang.Long (n/a)]"
		q.setParameter(2, s2);
		EntityX x2 = q.getSingleResult();

		assertNotNull(x2);
		assertEquals(1, x2.link.attribute);
		assertEquals(s_id, x2.link.s.id);

		entityManager.getTransaction().commit();

		entityManager.close();
	}

	@Test
	public void lookupByIdTest() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		entityManager.getTransaction().begin();

		TypedQuery<EntityX> q = entityManager.createQuery(
				"select x from EntityX x where x.link.attribute = ?1 and x.link.s.id = ?2",
				EntityX.class);
		q.setParameter(1, 1);
		q.setParameter(2, s_id);
		// Fails with "No entity found for query"
		EntityX x2 = q.getSingleResult();

		assertNotNull(x2);
		assertEquals(1, x2.link.attribute);
		assertEquals(s_id, x2.link.s.id);

		entityManager.getTransaction().commit();

		entityManager.close();
	}
}
