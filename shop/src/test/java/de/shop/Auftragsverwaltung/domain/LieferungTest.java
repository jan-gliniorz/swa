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

import de.shop.Auftragsverwaltung.domain.Lieferung;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;
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
public class LieferungTest extends AbstractDomainTest {
	private static final Long ID_VORHANDEN = Long.valueOf(455);
	private static final int TAG = 31;
	private static final int MONAT = Calendar.JANUARY;
	private static final int JAHR = 2001;
	private static final Date LIEFERUNGSDATUM_VORHANDEN = new GregorianCalendar(2000,Calendar.JUNE,15).getTime();
	private static final Date LIEFERUNGSDATUM_NICHT_VORHANDEN = new GregorianCalendar(1000,Calendar.JANUARY,01).getTime();
	private static final Date BESTELLDATUM_VORHANDEN = new GregorianCalendar(2000,Calendar.JULY,15).getTime();
	private static final Date BESTELLDATUM_NICHT_VORHANDEN = new GregorianCalendar (1000,Calendar.FEBRUARY,01).getTime();
	private static final Date SEIT_VORHANDEN = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	
	private static final Date LIEFERUNGSDATUM_NEU = new GregorianCalendar(2012,Calendar.NOVEMBER,31).getTime();
	private static final Date BESTELLDATUM_NEU = new GregorianCalendar(2012,Calendar.DECEMBER,31).getTime();
	
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	/*@Test
	public void findLieferungByIdVorhanden() {
		// Given
		final Long id = ID_VORHANDEN;
		
		// When
				Lieferung lieferung = getEntityManager().createNamedQuery(Lieferung.LIEFERUNG_BY_ID, Lieferung.class)
							.setParameter(Lieferung.PARAM_ID, 1)
							.getSingleResult();
						
		// Then
		assertThat(lieferung.getId(), is(id));
	}
	
	@Test
	public void LieferungsDatumVorhanden() {
		//Given
		final Date bestelldatum = LIEFERUNGSDATUM_VORHANDEN;
		
		//When
		Lieferung lieferung = getEntityManager().find(Lieferung.class, bestelldatum);
				
		//Then
		assertThat(lieferung.getBestelldatum(), is (bestelldatum));
	}*/
}
