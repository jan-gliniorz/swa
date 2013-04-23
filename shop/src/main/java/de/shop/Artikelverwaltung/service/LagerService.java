package de.shop.Artikelverwaltung.service;

import static de.shop.Util.Constants.KEINE_ID;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.jboss.logging.Logger;

import de.shop.Artikelverwaltung.domain.Artikel;
//import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Artikelverwaltung.domain.Lagerposition;
import de.shop.Util.IdGroup;
import de.shop.Util.Log;
import de.shop.Util.ValidatorProvider;

@Log
public class LagerService implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private transient Logger logger;
	
	public enum OrderType {
		KEINE,
		ID
	}
	
	public enum FetchType {
		NUR_Lager, 
		MIT_POSITIONEN
	}
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private ValidatorProvider validatorProvider;
	
	@PostConstruct
	private void postConstruct() {
		logger.debugf("CDI-faehiges Bean %s {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		logger.debugf("CDI-faehiges Bean %s {0} wird geloescht", this);
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
	 * @throws Exception 
	 */
//	public Lager findLagerById(Long id, Locale locale) {
//		validateLagerId(id, locale);
//		Lager lager = null;
//	
//		try {
//			lager = em.find(Lager.class, id);	
//		}
//		catch (NoResultException e) {
//			//throw new Exception("kein Lager mit Id:" + id + "gefunden");
//			return null;
//		}
//
//		return lager;
//	}
//	
//	public List<Lager> findLagerAll(FetchType fetch, OrderType order) {
//		
//		List<Lager> lager; 
//		
//		switch (fetch) {
//		case NUR_Lager:
//			lager = OrderType.ID.equals(order)
//			         ? em.createNamedQuery(Lager.FIND_LAGER_ALL, Lager.class)
//			             .getResultList()
//			         : em.createNamedQuery(Lager.FIND_LAGER_ALL, Lager.class)
//			             .getResultList();
//			break;
//		
//		case MIT_POSITIONEN:
//			lager = em.createNamedQuery(Lager.FIND_LAGER_ALL_LAGERPOSITIONEN, Lager.class)
//					   .getResultList();
//			break;
//
//		default:
//			lager = OrderType.ID.equals(order)
//	         		? em.createNamedQuery(Lager.FIND_LAGER_ALL, Lager.class)
//	         			.getResultList()
//	             	: em.createNamedQuery(Lager.FIND_LAGER_ALL, Lager.class)
//	             		.getResultList();
//			break;
//		}
//		
//		return lager;
//		
//	}
	
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
	
//	public Lagerposition findLagerpositionByLagerId(Long id, Locale locale) {
//		validateLagerId(id, locale);
//		Lagerposition lagerposition;
//		
//		try {
//		lagerposition = em.createNamedQuery(Lagerposition.FIND_LAGERPOSITION_BY_LAGER, Lagerposition.class)
//									        .setParameter(Lagerposition.PARAM_LAGER_ID, id)
//											.getSingleResult();
//		}
//		catch (NoResultException e) {
//			return null;
//		}
//		
//		return lagerposition;
//	}
	
//	private void validateLagerId(Long lagerId, Locale locale) {
//		final Validator validator = validatorProvider.getValidator(locale);
//		final Set<ConstraintViolation<Lager>> violations = validator.validateValue(Lager.class,
//			                                                                           "id",
//			                                                                           lagerId,
//			                                                                           IdGroup.class);
//	
//		if (!violations.isEmpty())
//			throw new LagerInvalidIdException(lagerId, violations);
//	}
//	
//	public Lager createLager(Lager lager, Locale locale) {
//		if (lager == null) {
//			return lager;
//		}
//
//		// Werden alle Constraints beim Einfuegen gewahrt?
//		validateLager(lager, locale);
//		
//		lager.setId(KEINE_ID);
//		em.persist(lager);
//		return lager;		
//	}
//	
//	private void validateLager(Lager lager, Locale locale) {
//		
//		// Werden alle Constraints beim Einfuegen gewahrt?
//		final Validator validator = validatorProvider.getValidator(locale);
//		
//		final Set<ConstraintViolation<Lager>> violations = validator.validate(lager);
//		if (!violations.isEmpty()) {
//			throw new LagerValidationException(lager, violations);
//		}
//	}
//	
//	public Lager updateLager(Lager lager, Locale locale) {
//		if (lager == null) {
//				return null;
//		}
//
//		// Werden alle Constraints beim Modifizieren gewahrt?
//		validateLager(lager, locale);
//		
//		try {
//			final Lager vorhandeneLager = em.createNamedQuery(Lager.FIND_LAGER_BY_ID,
//						                                               Lager.class)
//						                              .setParameter(Lager.PARAM_ID, lager.getId())
//						                              .getSingleResult();
//		
//			if (vorhandeneLager.getId().longValue() != lager.getId().longValue()) {
//				throw new LagerIdExistsException(lager.getId());
//			}
//		}
//		catch (NoResultException e) {
//			logger.debugf("Lager mit id:" + lager.getId() + " konnte nicht gefunden werden");
//		}
//
//		em.merge(lager);
//		return lager;
//	}
//	
//	public void deleteLager(Lager lager) {
//		if (lager == null) {
//			return;
//		}
//		
//		try {
//			lager = findLagerById(lager.getId(), Locale.getDefault());
//		}
//		catch (LagerInvalidIdException e) {
//			return;
//		}
//		
//		if (lager == null) {
//			return;
//		}
//
//		em.remove(lager);
//	}
	
	private void validateLagerposition(Lagerposition lagerposition, Locale locale) {
		
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Lagerposition>> violations = validator.validate(lagerposition);
		if (!violations.isEmpty()) {
			throw new LagerpositionValidationException(lagerposition, violations);
		}
	}
	
	private void validateLagerpositionId(Long lagerpositionId, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
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
		
//		try {
			lagerposition = findLagerpositionById(lagerposition.getId(), Locale.getDefault());
//		}
//		catch (LagerInvalidIdException e) {
//			return;
//		}
		
		if (lagerposition == null) {
			return;
		}

		em.remove(lagerposition);
	}
	
	public List<Lagerposition> findLagerpositionByArtikel(Artikel artikel, Locale locale) {
		List<Lagerposition> lagerpositionen = null;
	
		try {
			lagerpositionen = em.createNamedQuery(Lagerposition.FIND_LAGERPOSITION_BY_ARTIKEL, Lagerposition.class)
					        .setParameter(Lagerposition.PARAM_ARTIKEL_ID, artikel.getId())
					        .getResultList();
								
		}
		catch (NoResultException e) {
			return null;
		}

		return lagerpositionen;
	}
	
	public void removeArtikelAusLager(Artikel artikel, int anzahl) {	
			List<Lagerposition> lagerpositionen = null;
			
			try {
				lagerpositionen = em.createNamedQuery(Lagerposition.FIND_LAGERPOSITION_BY_ARTIKEL, Lagerposition.class)
									.setParameter(Lagerposition.PARAM_ARTIKEL_ID, artikel.getId())
									.getResultList();
			}
			
			catch (NoResultException e) {
				throw new NoArtikelInLagerException(artikel.getId());
			}
		
			int anz = 0;
			for (Lagerposition el : lagerpositionen) {
				
				if (el.getAnzahl() > anzahl) {
					anz = el.getAnzahl() - anzahl;
				}
				else {
					anz = el.getAnzahl();
				}
			}
			if (anz < anzahl) {
				throw new NoArtikelInLagerException(artikel.getId());
			}
			
	}
	
	public Lagerposition updateLagerposition(Lagerposition lagerpos, Locale locale) {
		if (lagerpos == null) {
				return null;
		}

		// Werden alle Constraints beim Modifizieren gewahrt?
		validateLagerposition(lagerpos, locale);
		
		try {
			final Lagerposition vorhandeneLagerposition = em.createNamedQuery(Lagerposition.FIND_LAGERPOSITION_BY_ID,
						                                               Lagerposition.class)
						                              .setParameter(Lagerposition.PARAM_ID, lagerpos.getId())
						                              .getSingleResult();			
		
			if (vorhandeneLagerposition.getId().longValue() != lagerpos.getId().longValue()) {
				throw new LagerIdExistsException(lagerpos.getId());
			}
		}
		catch (NoResultException e) {
			logger.debugf("Lagerposition mit id:" + lagerpos.getId() + " konnte nicht gefunden werden");
		}

		em.merge(lagerpos);
		return lagerpos;
	}
	
	
	
}
