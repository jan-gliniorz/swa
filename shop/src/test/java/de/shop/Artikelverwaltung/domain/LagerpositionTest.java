package de.shop.Artikelverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import de.shop.Artikelverwaltung.domain.Lagerposition;

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
public class LagerpositionTest extends AbstractDomainTest {
	private static final Long ID_VORHANDEN = Long.valueOf(400);	
	private static final int ANZAHL_NEU = 60;
	private static final int LAGER_VORHANDEN_ID = 350;
	private static final Long ARTIKEL_VORHANDEN_ID = Long.valueOf(300);
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findLagerpositionByIdVorhanden() {
		// Given
		final long id = ID_VORHANDEN;
		
		// When
 TypedQuery<Lagerposition> query = getEntityManager().createNamedQuery(Lagerposition.FIND_Lagerposition_BY_ID,
				Lagerposition.class);
		query.setParameter(Lagerposition.PARAM_ID, id);
		Lagerposition lagerposition = query.getSingleResult();
		
		// Then
		assertThat(lagerposition.getId(), is(id));
	}
	
	@Ignore
	@Test
	public void createLagerpositionOhneAnzahl() throws HeuristicMixedException, HeuristicRollbackException,
	                                                  SystemException {
		// Given
		Lager lager = getEntityManager().createNamedQuery(Lager.FIND_Lager_BY_ID, Lager.class)
					  					.setParameter(Lager.PARAM_ID, LAGER_VORHANDEN_ID)
					  					.getSingleResult();
		Artikel artikel = getEntityManager().createNamedQuery(Artikel.FIND_Artikel_BY_Artikel_ID, Artikel.class)
						  .setParameter(Artikel.PARAM_ID, ARTIKEL_VORHANDEN_ID)
						  .getSingleResult();
		
		// When
		final Lagerposition lagerposition = new Lagerposition();
		lagerposition.setArtikel(artikel);
		lager.addLagerpositionen(lagerposition);
		getEntityManager().persist(lagerposition);
		
		
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
				if (msg.contains("Die Lagerposition-Anzahl muss mindestens 1 sein.")) {
					return;
				}
			}
		}
	}
}

