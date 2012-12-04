package de.shop.Auftragsverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import de.shop.Auftragsverwaltung.domain.Lieferungsposition;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.Util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class LieferungspositionTest extends AbstractDomainTest {
	private static final Long ID_VORHANDEN = Long.valueOf(505);
	private static final int TAG = 31;
	private static final int MONAT = Calendar.JANUARY;
	private static final int JAHR = 2001;
	private static final Date SEIT_VORHANDEN = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	
	private static final int ANZAHL_NEU = 5;

	
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findLieferungspositionByIdVorhanden() {
		// Given
		final long id = ID_VORHANDEN;
		
		// When
		final TypedQuery<Lieferungsposition> query = getEntityManager().createNamedQuery(Lieferungsposition.LIEFERUNGSPOSITION_BY_ID,
				Lieferungsposition.class);
		query.setParameter(Lieferungsposition.PARAM_ID, id);
		Lieferungsposition lieferungsposition = query.getSingleResult();
		
		// Then
		assertThat(lieferungsposition.getId(), is(id));
	}
}

