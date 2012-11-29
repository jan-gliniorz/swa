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

import de.shop.Kundenverwaltung.domain.Kunde;

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
public class AuftragTest extends AbstractDomainTest {
	private static final Long ID_VORHANDEN = Long.valueOf(150);
	private static final Long ID_KUNDE_VORHANDEN = Long.valueOf(13);
	private static final Long ID_NICHTVORHANDEN = Long.valueOf(50);
	
	private static final String NACHNAME_NEU = "Test";
	private static final String VORNAME_NEU = "Theo";
	private static final String EMAIL_NEU = "theo@test.de";
	private static final String PLZ_NEU = "11111";
	private static final String ORT_NEU = "Testort";
	private static final String STRASSE_NEU = "Testweg";
	private static final String HAUSNR_NEU = "1";
	private static final String LAND_NEU = "Deutschland";
	
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findAuftragByIdVorhanden() {
		// Given
		final Long id = ID_VORHANDEN;
		
		// When		
		Auftrag auftrag = getEntityManager().createNamedQuery(Auftrag.FIND_AUFTRAG_BY_ID, Auftrag.class)
							.setParameter(Auftrag.PARAM_ID, id)
							.getSingleResult();
				
		// Then
		assertThat(auftrag.getId(), is(id));
	}
	
	@Test
	public void findAuftragByIdNichtVorhanden() {
		// Given
		final Long id = ID_NICHTVORHANDEN;
		
		// When / Then
		thrown.expect(NoResultException.class);
		getEntityManager().createNamedQuery(Auftrag.FIND_AUFTRAG_BY_ID, Auftrag.class)
							.setParameter(Auftrag.PARAM_ID, id)
							.getSingleResult();
	}
	
	@Test
	public void findAuftragByKundeVorhanden() {
		// Given
		final Long kundeId = ID_KUNDE_VORHANDEN;
		
		// When		
		List<Auftrag> kundenAuftraege = getEntityManager().createNamedQuery(Auftrag.FIND_AUFTRAG_BY_KUNDE, Auftrag.class)
							.setParameter(Auftrag.PARAM_KUNDEID, kundeId)
							.getResultList();
				
		// Then
		assertThat(kundenAuftraege.size(), is(1));
	}
}
