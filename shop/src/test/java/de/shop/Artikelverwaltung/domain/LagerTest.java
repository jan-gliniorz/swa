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

import de.shop.Artikelverwaltung.domain.Lager;

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

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.Util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class LagerTest extends AbstractDomainTest {
	private static final Long id_VORHANDEN = Long.valueOf(350);
	private static final String bezeichnung_VORHANDEN = "L1";
	private static final String bezeichnung_NichtVORHANDEN = "Bezfalse";
		
	private static final String BEZEICHNUNG_CREATE = "L11";	
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findLagerByIdVorhanden() {
		// Given
		final Long id = id_VORHANDEN;
		
		// When
		//final AbstractKunde kunde = getEntityManager().find(AbstractKunde.class, id);
		
		Lager lager = getEntityManager().createNamedQuery(Lager.FIND_Lager_BY_ID, Lager.class)
							.setParameter(Lager.PARAM_ID, id)
							.getSingleResult();
				
				
		// Then
		assertThat(lager.getId(), is(id));
	}
	
	
	@Test
	public void findLagerByBezeichnung() {
		// Given
		final String bezeichnung = bezeichnung_VORHANDEN;
		
		// When
		Lager lager = getEntityManager().createNamedQuery(Lager.FIND_Lager_BY_Bezeichnung, Lager.class)
								.setParameter(Lager.PARAM_Bezeichnung, bezeichnung)
								.getSingleResult();
				
				
                                                                       
		// Then
		assertThat(lager.getBezeichnung(), is(bezeichnung));
	}
	
	@Test
	public void findLagerByBezeichnungNichtVorhanden() {
		// Given
		final String bezeichnung = bezeichnung_NichtVORHANDEN;
		
		// When / Then
		thrown.expect(NoResultException.class);
		getEntityManager().createNamedQuery(Lager.FIND_Lager_BY_Bezeichnung, Lager.class)
					.setParameter(Lager.PARAM_Bezeichnung, bezeichnung)
					.getSingleResult();
                                                       
	}


	@Test
	public void createLager() {
		// Given
		Lager lager = new Lager();
		//lager.setId(ID_CREATE);
		lager.setBezeichnung(BEZEICHNUNG_CREATE);
		//lager.setErstelltAm(ERSTELLT_AM_CREATE);
		//lager.setGeaendertAm(GEANDERT_AM_CREATE);
		
		
		// When
		try {
			getEntityManager().persist(lager);         // abspeichern einschl. Adresse
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
		
		final String bezeichnung = BEZEICHNUNG_CREATE;
		
		// Den abgespeicherten Kunden ueber eine Named Query ermitteln
		List<Lager> lager_neu = getEntityManager().createNamedQuery(Lager.FIND_Lager_BY_Bezeichnung, Lager.class)
									.setParameter(Lager.PARAM_Bezeichnung, bezeichnung )
									.getResultList();
		
		// Ueberpruefung des ausgelesenen Objekts
		assertThat(lager_neu.size(), is(1));
		lager = (Lager) lager_neu.get(0);
		assertThat(lager.getId().longValue() > 0, is(true));
		assertThat(lager.getBezeichnung(), is(BEZEICHNUNG_CREATE));
	}
}
