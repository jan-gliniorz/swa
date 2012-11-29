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
public class LieferungTest extends AbstractDomainTest {
	private static final Long ID_VORHANDEN = Long.valueOf(455);
	private static final int TAG = 31;
	private static final int MONAT = Calendar.JANUARY;
	private static final int JAHR = 2001;
	private static final Date SEIT_VORHANDEN = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	
	private static final Date LIEFERUNGSDATUM_NEU = new GregorianCalendar(2012,Calendar.NOVEMBER,31).getTime();
	private static final Date BESTELLDATUM_NEU = new GregorianCalendar(2012,Calendar.DECEMBER,31).getTime();

	
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findLieferungByIdVorhanden() {
		// Given
		final long id = ID_VORHANDEN;
		
		// When
		final TypedQuery<Lieferung> query = getEntityManager().createNamedQuery(Lieferung.LIEFERUNG_BY_ID,
				Lieferung.class);
		query.setParameter(Lieferung.PARAM_ID, id);
		Lieferung lieferung = query.getSingleResult();
		
		// Then
		assertThat(lieferung.getId(), is(id));
	}
	
	@Test
	public void findLieferungByIdFetchLieferungspositionen() {
		// Given
		final long id = ID_VORHANDEN;
		
		// When
		final TypedQuery<Lieferung> query = getEntityManager().createNamedQuery(Lieferung.LIEFERUNG_BY_ID_LIEFERUNGSPOSITIONEN,
				Lieferung.class);
		query.setParameter(Lieferung.PARAM_ID, id);
		final List<Lieferung> lieferungen = query.getResultList();
		
		// Then
		assertThat(lieferungen.size() > 0, is (true));
	}
	
	@Test
	public void createLieferung() {
		// Given
		Lieferung lieferung = new Lieferung();
		lieferung.setBestelldatum(BESTELLDATUM_NEU);
		lieferung.setLieferungsdatum(LIEFERUNGSDATUM_NEU);
		
		// When
		try {
			getEntityManager().persist(lieferung);         // abspeichern einschl. Adresse
		}
		catch (ConstraintViolationException e) {
			// Es gibt Verletzungen bzgl. Bean Validation: auf der Console ausgeben
			final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
				System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
				System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
			}
			
			throw new RuntimeException(e);
		}
		
		// Then
		
		// Den abgespeicherten Kunden ueber eine Named Query ermitteln
		final TypedQuery<Lieferung> query =
				                        getEntityManager().createNamedQuery(Lieferung.LIEFERUNG_BY_BESTELLDATUM,
				                                                            Lieferung.class);
		query.setParameter(Lieferung.PARAM_BESTELLDATUM, BESTELLDATUM_NEU);
		final List<Lieferung> lieferungen = query.getResultList();
		
		// Ueberpruefung des ausgelesenen Objekts
		assertThat(lieferungen.size(), is(1));
		lieferung = (Lieferung) lieferungen.get(0);
		assertThat(lieferung.getId().longValue() > 0, is(true));
		assertThat(lieferung.getBestelldatum(), is(BESTELLDATUM_NEU));
	}
	


	@Test
	public void createLieferungOhneLieferungsdatum() throws HeuristicMixedException, HeuristicRollbackException,
	                                                  SystemException {
		// Given
		final Date bestelldatum = BESTELLDATUM_NEU;
		
		// When
		final Lieferung lieferung = new Lieferung();
		lieferung.setBestelldatum(bestelldatum);
		getEntityManager().persist(lieferung);
		
		// Then
		try {
			getUserTransaction().commit();
		}
		catch (RollbackException e) {
			final PersistenceException cause = (PersistenceException) e.getCause();
			final ConstraintViolationException cause2 = (ConstraintViolationException) cause.getCause();
			final Set<ConstraintViolation<?>> violations = cause2.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				final String msg = v.getMessage();
				if (msg.contains("Ein Kunde muss ein Lieferungsdatum haben")) {
					return;
				}
			}
		}
	}
}
