package de.shop.Artikelverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import de.shop.Artikelverwaltung.domain.Artikel;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hamcrest.core.Is;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.Util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class ArtikelTest extends AbstractDomainTest {
	private static final Long id_VORHANDEN = Long.valueOf(300);
	private static final String bezeichnung_VORHANDEN = "Chino";
	private static final String bezeichnung_NichtVORHANDEN = "Nicht";
		
	private static final String BEZEICHNUNG_CREATE = "Hose";
	private static final String BESCHREIBUNG_CREATE = "Chino";
	private static final BigDecimal PREIS_CREATE = BigDecimal.valueOf(23.0);	
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	//Ok
	@Test
	public void findArtikelByIdVorhanden() {
		// Given
		final Long id = id_VORHANDEN;
		
		// When		
		Artikel artikel = getEntityManager().createNamedQuery(Artikel.FIND_Artikel_BY_Artikel_ID, Artikel.class)
							.setParameter(Artikel.PARAM_ID, id)
							.getSingleResult();
				
		// Then
		assertThat(artikel.getId(), is(id));
	}
	
	
	@Test
	public void findArtikelByBezeichnung() {
		// Given
		final String bezeichnung = bezeichnung_VORHANDEN;
		
		// When
		List<Artikel> artikel = getEntityManager().createNamedQuery(Artikel.FIND_Artikel_BY_Bezeichnung, Artikel.class)
								.setParameter(Artikel.PARAM_Bezeichnung, bezeichnung)
								.getResultList();
				
				
                                                                       
		// Then
		assertThat(artikel.size(), is(1));
		Artikel a1 = artikel.get(0);
		assertThat(a1.getBezeichnung(), is(bezeichnung));
	}
	
	@Test
	public void findArtikelByBezeichnungNichtVorhanden() {
		// Given
		final String bezeichnung = bezeichnung_NichtVORHANDEN;
		
		// When / Then
		thrown.expect(NoResultException.class);
		getEntityManager().createNamedQuery(Artikel.FIND_Artikel_BY_Bezeichnung, Artikel.class)
					.setParameter(Artikel.PARAM_Bezeichnung, bezeichnung)
					.getSingleResult();
                                                       
	}

	@Ignore
	@Test
	public void createArtikel() {
		// Given
		Artikel artikel = new Artikel();
		artikel.setBezeichnung(BEZEICHNUNG_CREATE);
		artikel.setBeschreibung(BESCHREIBUNG_CREATE);
		artikel.setPreis(PREIS_CREATE);
		
		
		// When
		try {
			getEntityManager().persist(artikel);         // abspeichern einschl. Adresse
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
		List<Artikel> artikel_neu = getEntityManager().createNamedQuery(Artikel.FIND_Artikel_BY_Bezeichnung, Artikel.class)
									.setParameter(Artikel.PARAM_Bezeichnung, BEZEICHNUNG_CREATE)
									.getResultList();
		
		// Ueberpruefung des ausgelesenen Objekts
		assertThat(artikel_neu.size(), is(1));
		artikel = (Artikel) artikel_neu.get(0);
		assertThat(artikel.getId().longValue() > 0, is(true));
		assertThat(artikel.getBezeichnung(), is(BEZEICHNUNG_CREATE));
	}
	

	
}
