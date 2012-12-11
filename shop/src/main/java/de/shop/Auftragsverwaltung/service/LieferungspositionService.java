package de.shop.Auftragsverwaltung.service;

import static de.shop.Util.Constants.KEINE_ID;
import static java.util.logging.Level.FINER;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.Auftragsverwaltung.domain.Lieferung;
import de.shop.Auftragsverwaltung.domain.Lieferung_;
import de.shop.Auftragsverwaltung.domain.Lieferungsposition;
import de.shop.Auftragsverwaltung.domain.Lieferungsposition_;
import de.shop.Auftragsverwaltung.service.LieferungService.FetchType;
import de.shop.Auftragsverwaltung.service.LieferungService.OrderType;
import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Artikel_;

import de.shop.Util.IdGroup;
import de.shop.Util.Log;
import de.shop.Util.ValidationService;


@Log
public class LieferungspositionService implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	public enum OrderType {
		KEINE,
		ID
	}
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private ValidationService validationService;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}

	/**
	 */
	public List<Lieferungsposition> findLieferungspositionenAll(FetchType fetch, OrderType order) {
		
		List<Lieferungsposition> lieferungspositionen;
		
		lieferungspositionen = OrderType.ID.equals(order)
				         ? em.createNamedQuery(Lieferungsposition.LIEFERUNGSPOSITION_BY_ID, Lieferungsposition.class)
				             .getResultList()
				         : em.createNamedQuery(Lieferungsposition.LIEFERUNGSPOSITIONEN_ALL, Lieferungsposition.class)
				             .getResultList();
		
		return lieferungspositionen;
	}
	/**
	 */
	public Lieferungsposition findLieferungspositionById(Long id, Locale locale) {
	
		validateLieferungspositionId(id, locale);
	
		Lieferungsposition lieferungsposition = null;
	
		try {
			lieferungsposition = em.find(Lieferungsposition.class, id);	
		}
		catch (NoResultException e) {
			return null;
		}

		return lieferungsposition;
	}
	
	private void validateLieferungspositionId(Long lieferungspositionId, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Lieferungsposition>> violations = validator.validateValue(Lieferungsposition.class,
			                                                                           "id",
			                                                                           lieferungspositionId,
			                                                                           IdGroup.class);
	
		if (!violations.isEmpty())
			throw new LieferungspositionInvalidIdException(lieferungspositionId, violations);
	}
	
	public Lieferungsposition createLieferungsposition(Lieferungsposition lieferungsposition, Locale locale) {
		if (lieferungsposition == null) {
			return lieferungsposition;
		}

		// Werden alle Constraints beim Einfuegen gewahrt?
		validateLieferungsposition(lieferungsposition, locale);
		
		lieferungsposition.setId(KEINE_ID);
		em.persist(lieferungsposition);
		return lieferungsposition;		
	}
	
	private void validateLieferungsposition (Lieferungsposition lieferungsposition, Locale locale) {
		

		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Lieferungsposition>> violations = validator.validate(lieferungsposition);
		if (!violations.isEmpty()) {
			throw new LieferungspositionValidationException(lieferungsposition, violations);
		}
	}
	
	public Lieferungsposition updateLieferungsposition (Lieferungsposition lieferungsposition, Locale locale) {
		if (lieferungsposition == null) {
				return null;
		}

		// Werden alle Constraints beim Modifizieren gewahrt?
		validateLieferungsposition(lieferungsposition, locale);
			
		
		try {
			final Lieferungsposition vorhandeneLieferungsposition = em.createNamedQuery(Lieferungsposition.LIEFERUNGSPOSITION_BY_ID,
						                                               Lieferungsposition.class)
						                              .setParameter(Lieferungsposition.PARAM_ID, lieferungsposition.getId())
						                              .getSingleResult();
				
		
			if (vorhandeneLieferungsposition.getId().longValue() != lieferungsposition.getId().longValue()) {
				throw new LieferungspositionIdExistsException(lieferungsposition.getId());
			}
		}
		catch (NoResultException e) {
			LOGGER.finest("Neue Lieferungsposition");
		}

		em.merge(lieferungsposition);
		return lieferungsposition;
	}
	
	public void deleteLieferungsposition(Lieferungsposition lieferungsposition) {
		if (lieferungsposition == null) {
			return;
		}
		
		
		try {
			lieferungsposition = findLieferungspositionById(lieferungsposition.getId(), Locale.getDefault());
		}
		catch (LieferungspositionInvalidIdException e) {
			return;
		}
		
		if (lieferungsposition == null) {
			return;
		}

		em.remove(lieferungsposition);
	}	
}
