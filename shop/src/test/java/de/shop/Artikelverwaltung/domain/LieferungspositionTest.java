package de.shop.Artikelverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.persistence.TypedQuery;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.Util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class LieferungspositionTest extends AbstractDomainTest {
	private static final Long ID_VORHANDEN = Long.valueOf(505);

	
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findLieferungspositionByIdVorhanden() {
		// Given
		final long id = ID_VORHANDEN;
		
		// When
		final TypedQuery<Lieferungsposition> query = getEntityManager()
													 .createNamedQuery(Lieferungsposition.LIEFERUNGSPOSITION_BY_ID,
													 Lieferungsposition.class);
		query.setParameter(Lieferungsposition.PARAM_ID, id);
		Lieferungsposition lieferungsposition = query.getSingleResult();
		
		// Then
		assertThat(lieferungsposition.getId(), is(id));
	}
}

