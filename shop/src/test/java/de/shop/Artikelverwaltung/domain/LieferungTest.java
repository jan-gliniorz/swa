package de.shop.Artikelverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lieferung;

import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.Util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class LieferungTest extends AbstractDomainTest {
	private static final Long ID_VORHANDEN = Long.valueOf(455);
	private static final Date BESTELLDATUM_VORHANDEN = new GregorianCalendar(2011, Calendar.SEPTEMBER, 26).getTime();
	
	private static final Date LIEFERUNGSDATUM_NEU = new GregorianCalendar(2012,Calendar.NOVEMBER,31).getTime();
	private static final Date BESTELLDATUM_NEU = new GregorianCalendar(2012,Calendar.DECEMBER,31).getTime();
	private static final int POS1ANZ_NEU = 3;
	private static final Long POS1ARTIKELID_NEU = Long.valueOf(303);

	
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
	public void findLieferungByBestelldatum() {
		//Given
		final Date bestelldatum = BESTELLDATUM_VORHANDEN;
		
		//When
		final TypedQuery<Lieferung> query = getEntityManager().createNamedQuery(Lieferung.LIEFERUNG_BY_BESTELLDATUM,
				Lieferung.class);
		query.setParameter(Lieferung.PARAM_BESTELLDATUM, bestelldatum);
		final List<Lieferung> lieferungen
		= query.getResultList();
		
		if (lieferungen.isEmpty())
			throw new RuntimeException();
			
		// Then
		assertThat(lieferungen.get(1).getBestelldatum(), is(bestelldatum));
	}
	
	@Test
	public void createLieferung() {
		
		// Given
		final int position1Anz = POS1ANZ_NEU;
		final Long position1ArtikelId = POS1ARTIKELID_NEU;
		final Date bestelldatum = BESTELLDATUM_NEU;
		final Date lieferungsdatum = LIEFERUNGSDATUM_NEU;

		Artikel artikel = getEntityManager().createNamedQuery(Artikel.FIND_Artikel_BY_Artikel_ID, Artikel.class)
											.setParameter(Artikel.PARAM_ID, position1ArtikelId)
											.getSingleResult();
		
		// When
		final Lieferung lieferung = new Lieferung();
		lieferung.setBestelldatum(bestelldatum);
		lieferung.setLieferungsdatum(lieferungsdatum);
		
		final Lieferungsposition position1 = new Lieferungsposition();
		position1.setAnzahl(position1Anz);
		position1.setArtikel(artikel);
		lieferung.addLieferungsposition(position1);
		
		try {
			getEntityManager().persist(lieferung);
			//getEntityManager().persist(position1);
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
				List<Lieferung> lieferungen = getEntityManager().createNamedQuery(Lieferung.LIEFERUNG_BY_BESTELLDATUM, Lieferung.class)
															.setParameter(Lieferung.PARAM_BESTELLDATUM, bestelldatum) 
															.getResultList();
				
				// Ueberpruefung des ausgelesenen Objekts
				assertThat(lieferungen.size(), is(1));
				assertThat(lieferung.getBestelldatum(), is(bestelldatum));
	}
}
