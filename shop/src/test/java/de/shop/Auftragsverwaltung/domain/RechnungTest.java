package de.shop.Auftragsverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.persistence.NoResultException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import de.shop.Util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class RechnungTest extends AbstractDomainTest {
	private static final Long ID_VORHANDEN = Long.valueOf(255);	
	private static final Long ID_NICHTVORHANDEN = Long.valueOf(400);
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findRechnungByIdVorhanden() {
		// Given
		final Long rechnungId = ID_VORHANDEN;
		
		// When
		Rechnung rechnung = getEntityManager().createNamedQuery(Rechnung.FIND_RECHNUNG_BY_ID, Rechnung.class)
							.setParameter(Rechnung.PARAM_ID, rechnungId)
							.getSingleResult();
		
		// Then
		assertThat(rechnung.getId(), is(rechnungId));
	}
	
	@Test
	public void findRechnungByIdNichtVorhanden() {
		// Given
		final Long rechnungId = ID_NICHTVORHANDEN;
		
		// When / Then
		thrown.expect(NoResultException.class);
		getEntityManager().createNamedQuery(Rechnung.FIND_RECHNUNG_BY_ID, Rechnung.class)
							.setParameter(Rechnung.PARAM_ID, rechnungId)
							.getSingleResult();
	}
}
