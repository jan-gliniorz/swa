package de.shop.Artikelverwaltung.service;

import static de.shop.Util.Constants.KEINE_ID;
import static java.util.logging.Level.FINER;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
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

import de.shop.Artikelverwaltung.service.ArtikelService.FetchType;
import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Artikelverwaltung.domain.Lagerposition;

import de.shop.Util.IdGroup;
import de.shop.Util.Log;
import de.shop.Util.ValidationService;


@Log
public class LagerService implements Serializable {
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
//	public List<Lagerposition> findLagerpositionenAll(FetchType fetch, OrderType order) {
//		
//		List<Lagerposition> lagerpositionen;
//		
//		lagerpositionen = OrderType.ID.equals(order)
//				         ? em.createNamedQuery(Lagerposition.FIND_Lagerposition_BY_ID, Lagerposition.class)
//				             .getResultList()
//				         : em.createNamedQuery(Lagerposition.FIND_Lagerposition_ALL, Lagerposition.class)
//				             .getResultList();
//		
//		return lagerpositionen;
//	}
	/**
	 */
	public Lager findLagerById(Long id, Locale locale) {
	
		validateLagerId(id, locale);
	
		Lager lager = null;
	
		try {
			lager = em.find(Lager.class, id);	
		}
		catch (NoResultException e) {
			return null;
		}

		return lager;
	}
	
	public Lagerposition findLagerpositionById(Long id, Locale locale) {
		
		validateLagerpositionId(id, locale);
	
		Lagerposition lagerposition = null;
	
		try {
			lagerposition = em.find(Lagerposition.class, id);	
		}
		catch (NoResultException e) {
			return null;
		}

		return lagerposition;
	}
	
	private void validateLagerId(Long lagerId, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Lager>> violations = validator.validateValue(Lager.class,
			                                                                           "id",
			                                                                           lagerId,
			                                                                           IdGroup.class);
	
		if (!violations.isEmpty())
			throw new LagerInvalidIdException(lagerId, violations);
	}
	
	public Lager createLager(Lager lager, Locale locale) {
		if (lager == null) {
			return lager;
		}

		// Werden alle Constraints beim Einfuegen gewahrt?
		validateLager(lager, locale);
		
		lager.setId(KEINE_ID);
		em.persist(lager);
		return lager;		
	}
	
	private void validateLager (Lager lager, Locale locale) {
		

		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Lager>> violations = validator.validate(lager);
		if (!violations.isEmpty()) {
			throw new LagerValidationException(lager, violations);
		}
	}
	
	
	public Lager updateLager (Lager lager, Locale locale) {
		if (lager == null) {
				return null;
		}

		// Werden alle Constraints beim Modifizieren gewahrt?
		validateLager(lager, locale);
			
		
		try {
			final Lager vorhandeneLager = em.createNamedQuery(Lager.FIND_Lager_BY_ID,
						                                               Lager.class)
						                              .setParameter(Lager.PARAM_ID, lager.getId())
						                              .getSingleResult();
				
		
			if (vorhandeneLager.getId().longValue() != lager.getId().longValue()) {
				throw new LagerIdExistsException(lager.getId());
			}
		}
		catch (NoResultException e) {
			LOGGER.finest("Neue Lager");
		}

		em.merge(lager);
		return lager;
	}
	
	public void deleteLager(Lager lager) {
		if (lager == null) {
			return;
		}
		
		
		try {
			lager = findLagerById(lager.getId(), Locale.getDefault());
		}
		catch (LagerInvalidIdException e) {
			return;
		}
		
		if (lager == null) {
			return;
		}

		em.remove(lager);
	}
	
	private void validateLagerposition (Lagerposition lagerposition, Locale locale) {
		
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Lagerposition>> violations = validator.validate(lagerposition);
		if (!violations.isEmpty()) {
			throw new LagerpositionValidationException(lagerposition, violations);
		}
	}
	
	private void validateLagerpositionId(Long lagerpositionId, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Lagerposition>> violations = validator.validateValue(Lagerposition.class,
			                                                                           "id",
			                                                                           lagerpositionId,
			                                                                           IdGroup.class);
	
		if (!violations.isEmpty())
			throw new LagerpositionInvalidIdException(lagerpositionId, violations);
	}
	
	public Lagerposition createLagerposition(Lagerposition lagerposition, Locale locale) {
		if (lagerposition == null) {
			return lagerposition;
		}

		// Werden alle Constraints beim Einfuegen gewahrt?
		validateLagerposition(lagerposition, locale);
		
		lagerposition.setId(KEINE_ID);
		em.persist(lagerposition);
		return lagerposition;		
	}
	
	
	public void deleteLagerposition(Lagerposition lagerposition) {
		if (lagerposition == null) {
			return;
		}
		
		
		try {
			lagerposition = findLagerpositionById(lagerposition.getId(), Locale.getDefault());
		}
		catch (LagerInvalidIdException e) {
			return;
		}
		
		if (lagerposition == null) {
			return;
		}

		em.remove(lagerposition);
	}
	
	
	
	
	public List<Lagerposition> findLagerpositionByArtikel(Artikel artikel, Locale locale) {
		List<Lagerposition> lagerpositionen = null;
	
		try {
			lagerpositionen = em.createNamedQuery(Lagerposition.FIND_LAGERPOSITION_BY_Artikel, Lagerposition.class)
					        .setParameter(Lagerposition.PARAM_Artikel, artikel.getId())
					        .getResultList();
								
		}
		catch (NoResultException e) {
			return null;
		}

		return lagerpositionen;
	}
	
	public void removeArtikelAusLager(Artikel artikel, int anzahl){
		
			List<Lagerposition> lagerpositionen = null;
			
			try{
				lagerpositionen = em.createNamedQuery(Lagerposition.FIND_LAGERPOSITION_BY_Artikel, Lagerposition.class)
									.setParameter(Lagerposition.PARAM_Artikel, artikel.getId())
									.getResultList();
				
			}
			
			catch (NoResultException e){
				throw new NoArtikelInLagerException(artikel.getId());
			}
		
			for(Lagerposition el : lagerpositionen){
				
				int anz = 0;
				if(el.getAnzahl() > anzahl){
				anz = el.getAnzahl() - anzahl;
				}
				else{
					throw new NoArtikelInLagerException(artikel.getId());
				}
			}
		
		
	}
	
	
	
	
}
