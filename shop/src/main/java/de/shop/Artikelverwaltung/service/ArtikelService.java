package de.shop.Artikelverwaltung.service;

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


import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Util.IdGroup;
import de.shop.Util.Log;
import de.shop.Util.ValidationService;

@Log
public class ArtikelService implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	public enum FetchType {
		NUR_Artikel, 
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
	public List<Artikel> findArtikelAll(FetchType fetch, OrderType order) {
		
		List<Artikel> artikel;
		
		switch (fetch) {
			case NUR_Artikel:
				artikel = OrderType.ID.equals(order)
				         ? em.createNamedQuery(Artikel.FIND_Artikel_All, Artikel.class)
				             .getResultList()
				         : em.createNamedQuery(Artikel.FIND_Artikel_All, Artikel.class)
				             .getResultList();
				break;
			
			case MIT_POSITIONEN:
				artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_ALL_LAGERPOSITIONEN, Artikel.class)
						   .getResultList();
				break;

			default:
				artikel = OrderType.ID.equals(order)
		         		? em.createNamedQuery(Artikel.FIND_Artikel_All, Artikel.class)
		         			.getResultList()
		             	: em.createNamedQuery(Artikel.FIND_Artikel_All, Artikel.class)
		             		.getResultList();
				break;
		}

		return artikel;
	}
	
	/**
	 */
	
	public Artikel findArtikelByID(Long id, FetchType fetch, Locale locale) {
		
		validateArtikelId(id, locale);
		
		Artikel artikel = null;
		try {
			switch (fetch) {
				case NUR_Artikel:
					artikel = em.find(Artikel.class, id);
					break;
				
				case MIT_POSITIONEN:
					artikel = em.createNamedQuery(Artikel.FIND_Artikel_BY_ID_Lagerpositionen, Artikel.class)
							  .setParameter(Artikel.PARAM_ID, id)
							  .getSingleResult();
					break;
	
				default:
					artikel = em.find(Artikel.class, id);
					break;
			}
		}
		catch (NoResultException e) {
			return null;
		}

		return artikel;
	}
	
	private void validateArtikelId(Long artikelId, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Artikel>> violations = validator.validateValue(Artikel.class,
				                                                                           "id",
				                                                                           artikelId,
				                                                                           IdGroup.class);
		
		if (!violations.isEmpty())
			throw new ArtikelInvalidIdException(artikelId, violations);
	}

	/**
	 */
	public Artikel createArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return artikel;
		}

		// Werden alle Constraints beim Einfuegen gewahrt?
		validateArtikel(artikel, locale);
		
		artikel.setId(KEINE_ID);
		em.persist(artikel);
		return artikel;		
	}
	
	private void validateArtikel (Artikel artikel, Locale locale) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Artikel>> violations = validator.validate(artikel);
		if (!violations.isEmpty()) {
			throw new ArtikelValidationException(artikel, violations);
		}
	}
	
	/**
	 */
	public Artikel updateArtikel (Artikel artikel, Locale locale) {
		if (artikel == null) {
			return null;
		}

		// Werden alle Constraints beim Modifizieren gewahrt?
		validateArtikel(artikel, locale);
		
		
		try {
			final Artikel vorhandeneArtikel = em.createNamedQuery(Artikel.FIND_Artikel_BY_Artikel_ID,
					                                                   Artikel.class)
					                                 .setParameter(Artikel.PARAM_ID, artikel.getId())
					                                 .getSingleResult();
			
			
			if (vorhandeneArtikel.getId().longValue() != artikel.getId().longValue()) {
				throw new ArtikelIdExistsException(artikel.getId());
			}
		}
		catch (NoResultException e) {
			LOGGER.finest("Neuer Artikel");
		}

		em.merge(artikel);
		return artikel;
	}

	/**
	 */
	public void deleteArtikel(Artikel artikel) {
		if (artikel == null) {
			return;
		}
		
		try {
			artikel = findArtikelByID(artikel.getId(), FetchType.MIT_POSITIONEN, Locale.getDefault());
		}
		catch (ArtikelInvalidIdException e) {
			return;
		}
		
		if (artikel == null) {
			return;
		}
		
		// Gibt es Artikelpositionen?
		if (!artikel.getLagerposition().isEmpty()) {
			throw new ArtikelDeletePositionException(artikel);
		}

		em.remove(artikel);
	}


}
