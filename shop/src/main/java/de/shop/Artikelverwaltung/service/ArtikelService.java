package de.shop.Artikelverwaltung.service;

import static de.shop.Util.Constants.KEINE_ID;

import java.io.Serializable;
import java.util.Collections;
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
import de.shop.Util.IdGroup;
import de.shop.Util.Log;
import de.shop.Util.ValidatorProvider;

@Log
public class ArtikelService implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private transient Logger logger;
	
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
	public List<Artikel> findArtikelAll(FetchType fetch, OrderType order) {
		List<Artikel> artikel;
		
		switch (fetch) {
			case NUR_Artikel:
				artikel = OrderType.ID.equals(order)
				         ? em.createNamedQuery(Artikel.FIND_ARTIKEL_ALL, Artikel.class)
				             .getResultList()
				         : em.createNamedQuery(Artikel.FIND_ARTIKEL_ALL, Artikel.class)
				             .getResultList();
				break;
			
			case MIT_POSITIONEN:
				artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_ALL_LAGERPOSITIONEN, Artikel.class)
						   .getResultList();
				break;

			default:
				artikel = OrderType.ID.equals(order)
		         		? em.createNamedQuery(Artikel.FIND_ARTIKEL_ALL, Artikel.class)
		         			.getResultList()
		             	: em.createNamedQuery(Artikel.FIND_ARTIKEL_ALL, Artikel.class)
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
					artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_ID_LAGERPOSITIONEN, Artikel.class)
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
	
	
	public List<Artikel> findArtikelByIdPrefix(Long id) {
		if (id == null) {
			return Collections.emptyList();
		}
		
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_ID_PREFIX,
				                                               Artikel.class)
				                             .setParameter(Artikel.PARAM_ID, id.toString() + '%')
				                             .getResultList();
		return artikel;
	}
	
	
	public List<String> findBezeichnungByPrefix(String nachnamePrefix) {
		final List<String> bezeichnung = em.createNamedQuery(Artikel.FIND_BEZEICHNUNG_BY_PREFIX, String.class)
				                         .setParameter(Artikel.PARAM_BEZEICHNUNG, nachnamePrefix + '%')
				                         .getResultList();
		return bezeichnung;
	}
	
	
	
	public List<Artikel> findArtikelByBezeichnung(String bez) {
		//validateArtikelId(bez, locale);

		List<Artikel> artikel = null;
		
					artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG, Artikel.class)
							  .setParameter(Artikel.PARAM_BEZEICHNUNG, bez)
							  .getResultList();

		return artikel;
	}
	
	public List<Artikel> findArtikelByIDs(List<Long> ids, FetchType fetch, Locale locale) {
		for (Long id : ids) {
			validateArtikelId(id, locale);
		}
		
		List<Artikel> artikel = null;
		try {
			switch (fetch) {
				case NUR_Artikel:
					artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_ARTIKEL_IDS, Artikel.class)
					  .setParameter(Artikel.PARAM_ID, ids)
					  .getResultList();
					break;
				
				case MIT_POSITIONEN:
					artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_IDS_LAGERPOSITIONEN, Artikel.class)
							  .setParameter(Artikel.PARAM_ID, ids)
							  .getResultList();
					break;
	
				default:
					artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_ARTIKEL_IDS, Artikel.class)
							  .setParameter(Artikel.PARAM_ID, ids)
							  .getResultList();
					break;
			}
		}
		catch (NoResultException e) {
			return null;
		}

		return artikel;
	}
	
	
	
	
	private void validateArtikelId(Long artikelId, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
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
	
	private void validateArtikel(Artikel artikel, Locale locale) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Artikel>> violations = validator.validate(artikel);
		if (!violations.isEmpty()) {
			throw new ArtikelValidationException(artikel, violations);
		}
	}
	
	/**
	 */
	public Artikel updateArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return null;
		}

		// Werden alle Constraints beim Modifizieren gewahrt?
		validateArtikel(artikel, locale);
		
		
		try {
			final Artikel vorhandeneArtikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_ARTIKEL_ID,
					                                                   Artikel.class)
					                                 .setParameter(Artikel.PARAM_ID, artikel.getId())
					                                 .getSingleResult();
			
			
			if (vorhandeneArtikel.getId().longValue() != artikel.getId().longValue()) {
				throw new ArtikelIdExistsException(artikel.getId());
			}
		}
		catch (NoResultException e) {
			logger.debugf("Neuer Artikel");
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
