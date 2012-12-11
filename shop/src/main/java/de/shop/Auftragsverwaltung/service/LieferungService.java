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
import de.shop.Auftragsverwaltung.domain.Auftragsposition;
import de.shop.Auftragsverwaltung.domain.Auftragsposition_;
import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Auftragsverwaltung.domain.Auftrag_;
import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Kundenverwaltung.domain.Kunde_;
import de.shop.Kundenverwaltung.service.InvalidKundeIdException;
import de.shop.Util.IdGroup;
import de.shop.Util.Log;
import de.shop.Util.ValidationService;

@Log
public class LieferungService implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	public enum FetchType {
		NUR_LIEFERUNG, 
		MIT_POSITIONEN
	}
	
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
	public List<Lieferung> findLieferungenAll(FetchType fetch, OrderType order) {
		
		List<Lieferung> lieferungen;
		
		switch (fetch) {
			case NUR_LIEFERUNG:
				lieferungen = OrderType.ID.equals(order)
				         ? em.createNamedQuery(Lieferung.LIEFERUNG_BY_ID, Lieferung.class)
				             .getResultList()
				         : em.createNamedQuery(Lieferung.LIEFERUNGEN_ALL, Lieferung.class)
				             .getResultList();
				break;
			
			case MIT_POSITIONEN:
				lieferungen = em.createNamedQuery(Lieferung.LIEFERUNG_BY_ID_LIEFERUNGSPOSITIONEN, Lieferung.class)
						   .getResultList();
				break;

			default:
				lieferungen = OrderType.ID.equals(order)
		         		? em.createNamedQuery(Lieferung.LIEFERUNG_BY_ID, Lieferung.class)
		         			.getResultList()
		             	: em.createNamedQuery(Lieferung.LIEFERUNGEN_ALL, Lieferung.class)
		             		.getResultList();
				break;
		}

		return lieferungen;
	}
	
	/**
	 */
	
	public Lieferung findLieferungById(Long id, FetchType fetch, Locale locale) {
		
		validateLieferungId(id, locale);
		
		Lieferung lieferung = null;
		try {
			switch (fetch) {
				case NUR_LIEFERUNG:
					lieferung = em.find(Lieferung.class, id);
					break;
				
				case MIT_POSITIONEN:
					lieferung = em.createNamedQuery(Lieferung.LIEFERUNG_BY_ID_LIEFERUNGSPOSITIONEN, Lieferung.class)
							  .setParameter(Lieferung.LIEFERUNG_BY_ID, id)
							  .getSingleResult();
					break;
	
				default:
					lieferung = em.find(Lieferung.class, id);
					break;
			}
		}
		catch (NoResultException e) {
			return null;
		}

		return lieferung;
	}
	
	private void validateLieferungId(Long lieferungId, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Lieferung>> violations = validator.validateValue(Lieferung.class,
				                                                                           "id",
				                                                                           lieferungId,
				                                                                           IdGroup.class);
		
		if (!violations.isEmpty())
			throw new LieferungInvalidIdException(lieferungId, violations);
	}

	/**
	 */
	public Lieferung createLieferung(Lieferung lieferung, Locale locale) {
		if (lieferung == null) {
			return lieferung;
		}

		// Werden alle Constraints beim Einfuegen gewahrt?
		validateLieferung(lieferung, locale);
		
		lieferung.setId(KEINE_ID);
		em.persist(lieferung);
		return lieferung;		
	}
	
	private void validateLieferung (Lieferung lieferung, Locale locale) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Lieferung>> violations = validator.validate(lieferung);
		if (!violations.isEmpty()) {
			throw new LieferungValidationException(lieferung, violations);
		}
	}
	
	/**
	 */
	public Lieferung updateLieferung (Lieferung lieferung, Locale locale) {
		if (lieferung == null) {
			return null;
		}

		// Werden alle Constraints beim Modifizieren gewahrt?
		validateLieferung(lieferung, locale);
		
		
		try {
			final Lieferung vorhandeneLieferung = em.createNamedQuery(Lieferung.LIEFERUNG_BY_ID,
					                                                   Lieferung.class)
					                                 .setParameter(Lieferung.PARAM_ID, lieferung.getId())
					                                 .getSingleResult();
			
			
			if (vorhandeneLieferung.getId().longValue() != lieferung.getId().longValue()) {
				throw new LieferungIdExistsException(lieferung.getId());
			}
		}
		catch (NoResultException e) {
			LOGGER.finest("Neue Lieferung");
		}

		em.merge(lieferung);
		return lieferung;
	}

	/**
	 */
	public void deleteLieferung(Lieferung lieferung) {
		if (lieferung == null) {
			return;
		}
		
		try {
			lieferung = findLieferungById(lieferung.getId(), FetchType.MIT_POSITIONEN, Locale.getDefault());
		}
		catch (LieferungInvalidIdException e) {
			return;
		}
		
		if (lieferung == null) {
			return;
		}
		
		// Gibt es Lieferungspositionen?
		if (!lieferung.getLieferungsposition().isEmpty()) {
			throw new LieferungDeletePositionException(lieferung);
		}

		em.remove(lieferung);
	}

	/**
	 */
	public List<Lieferung> findLieferungByBestelldatum(Date bestelldatum) {
		final List<Lieferung> lieferung = em.createNamedQuery(Lieferung.LIEFERUNG_BY_BESTELLDATUM, Lieferung.class)
                                             .setParameter(Lieferung.PARAM_BESTELLDATUM, bestelldatum)
                                             .getResultList();
		return lieferung;
	}
}
