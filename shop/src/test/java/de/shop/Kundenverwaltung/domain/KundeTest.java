package de.shop.Kundenverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import de.shop.Kundenverwaltung.domain.Kunde;

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
public class KundeTest extends AbstractDomainTest {
	private static final String NACHNAME_VORHANDEN = "Alpha";
	private static final String NACHNAME_NICHT_VORHANDEN = "Nicht";
	private static final Long ID_VORHANDEN = Long.valueOf(101);
	private static final String EMAIL_VORHANDEN = "k1@hska.de";
	private static final String EMAIL_NICHT_VORHANDEN = "Nicht";
	private static final int TAG = 31;
	private static final int MONAT = Calendar.JANUARY;
	private static final int JAHR = 2001;
	private static final Date SEIT_VORHANDEN = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	
	private static final String NACHNAME_NEU = "Test";
	private static final String VORNAME_NEU = "Theo";
	private static final String EMAIL_NEU = "theo@test.de";
	private static final float RABATT_NEU = 0.5f;
	private static final double UMSATZ_NEU = 10_001_000;
	private static final String PLZ_NEU = "11111";
	private static final String ORT_NEU = "Testort";
	private static final String STRASSE_NEU = "Testweg";
	private static final String HAUSNR_NEU = "1";
	
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	/*
	@Test
	public void findKundeByIdVorhanden() {
		// Given
		final Long id = ID_VORHANDEN;
		
		// When
		//final AbstractKunde kunde = getEntityManager().find(AbstractKunde.class, id);
		
		Kunde kunde = getEntityManager().createNamedQuery(Kunde.KUNDE_BY_KNR, Kunde.class)
							.setParameter(Kunde.PARAM_KUNDENNUMMER, 12)
							.getSingleResult();
				
				
		// Then
		assertThat(kunde.getKundenNr(), is(id));
	}
	
	
	@Test
	public void findKundeByEmailVorhanden() {
		// Given
		final String email = EMAIL_VORHANDEN;
		
		// When
		Kunde kunde = getEntityManager().createNamedQuery(Kunde.KUNDE_BY_EMAIL, Kunde.class)
								.setParameter(Kunde.PARAM_EMAIL, email)
								.getSingleResult();
				
				
                                                                       
		// Then
		assertThat(kunde.getEmail(), is(email));
	}
	
	@Test
	public void findKundeByEmailNichtVorhanden() {
		// Given
		final String email = EMAIL_NICHT_VORHANDEN;
		
		// When / Then
		thrown.expect(NoResultException.class);
		getEntityManager().createNamedQuery(Kunde.KUNDE_BY_EMAIL, Kunde.class)
					.setParameter(Kunde.PARAM_EMAIL, email)
					.getSingleResult();
                                                       
	}

	@Test
	public void findKundenByNachnameVorhanden() {
		// Given
		final String nachname = NACHNAME_VORHANDEN;
		
		// When
		final List<AbstractKunde> kunden = getEntityManager().createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_NACHNAME,
                                                                               AbstractKunde.class)
                                                            .setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME, nachname)
				                                            .getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (AbstractKunde k : kunden) {
			assertThat(k.getNachname(), is(nachname));
		}
	}
	
	@Test
	public void findKundenByNachnameNichtVorhanden() {
		// Given
		final String nachname = NACHNAME_NICHT_VORHANDEN;
		
		// When
		final List<AbstractKunde> kunden = getEntityManager().createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_NACHNAME,
                                                                               AbstractKunde.class)
                                                            .setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME, nachname)
				                                            .getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(true));
	}
	
	@Test
	public void findKundenByDateVorhanden() {
		// Given
		final Date seit = SEIT_VORHANDEN;
		
		// When
		final List<AbstractKunde> kunden = getEntityManager().createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_DATE,
                                                                               AbstractKunde.class)
                                                             .setParameter(AbstractKunde.PARAM_KUNDE_SEIT, seit,
                                                            		       TemporalType.DATE)
				                                             .getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (AbstractKunde k : kunden) {
			assertThat(k.getSeit(), is(seit));
		}
	}

	@Test
	public void createPrivatkunde() {
		// Given
		Privatkunde kunde = new Privatkunde();
		kunde.setNachname(NACHNAME_NEU);
		kunde.setVorname(VORNAME_NEU);
		kunde.setEmail(EMAIL_NEU);
		kunde.setRabatt(RABATT_NEU);
		kunde.setUmsatz(UMSATZ_NEU);
		kunde.setSeit(SEIT_VORHANDEN);
		
		final Adresse adresse = new Adresse();
		adresse.setPlz(PLZ_NEU);
		adresse.setOrt(ORT_NEU);
		adresse.setStrasse(STRASSE_NEU);
		adresse.setHausnr(HAUSNR_NEU);
		adresse.setKunde(kunde);
		kunde.setAdresse(adresse);
		
		kunde.setNewsletter(true);
		kunde.setGeschlecht(GeschlechtType.WEIBLICH);
		kunde.setFamilienstand(FamilienstandType.VERHEIRATET);
		kunde.setHobbies(Arrays.asList(HobbyType.LESEN, HobbyType.SPORT));
		
		// When
		try {
			getEntityManager().persist(kunde);         // abspeichern einschl. Adresse
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
		final List<AbstractKunde> kunden = getEntityManager().createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_NACHNAME,
                                                                               AbstractKunde.class)
                                                             .setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME,
                                                            		       NACHNAME_NEU)
				                                             .getResultList();
		
		// Ueberpruefung des ausgelesenen Objekts
		assertThat(kunden.size(), is(1));
		kunde = (Privatkunde) kunden.get(0);
		assertThat(kunde.getId().longValue() > 0, is(true));
		assertThat(kunde.getNachname(), is(NACHNAME_NEU));
	}
	
	@Ignore("Beispiel fuer einen unvollstaendigen Test")
	@Test
	public void notYetImplemented() {
		fail();
	}

	@Test
	public void createPrivatkundeOhneAdresse() throws HeuristicMixedException, HeuristicRollbackException,
	                                                  SystemException {
		// Given
		final String nachname = NACHNAME_NEU;
		final String email = EMAIL_NEU;
		
		// When
		final Privatkunde kunde = new Privatkunde();
		kunde.setNachname(nachname);
		kunde.setEmail(email);
		getEntityManager().persist(kunde);
		
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
				if (msg.contains("Ein Kunde muss eine Adresse haben")) {
					return;
				}
			}
		}

	}
	*/
}
