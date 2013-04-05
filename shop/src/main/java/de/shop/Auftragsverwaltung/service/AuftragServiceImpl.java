package de.shop.Auftragsverwaltung.service;

import static de.shop.Util.Constants.KEINE_ID;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.jboss.logging.Logger;

import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Auftragsverwaltung.domain.Auftragsposition;
import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Kundenverwaltung.service.KundeService;
import de.shop.Kundenverwaltung.service.KundeService.FetchType;
import de.shop.Util.Log;
import de.shop.Util.ValidatorProvider;

@Log
public class AuftragServiceImpl implements Serializable, AuftragService {
	private static final long serialVersionUID = -9145947650157430928L;

	@Inject
	private transient Logger logger;
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ArtikelService artikelService;
	
	@Inject
	private ValidatorProvider validatorProvider;
	
	@Inject
	@NeuerAuftrag
	private transient Event<Auftrag> event;
	
	@PostConstruct
	private void postConstruct() {
		logger.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		logger.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@Override
	public List<Auftrag> findAuftragAll() {
		final List<Auftrag> auftraege = em.createNamedQuery(Auftrag.FIND_AUFTRAG_ALL, Auftrag.class)
											.getResultList();
		return auftraege;
	}

	@Override
	public Auftrag findAuftragById(Long id) {
		final Auftrag auftrag = em.find(Auftrag.class, id);
		return auftrag;
	}
	
	@Override
	public List<Auftrag> findAuftragByKundeId(Long kundeId) {
		final List<Auftrag> auftraege = em.createNamedQuery(Auftrag.FIND_AUFTRAG_BY_KUNDE, Auftrag.class)
										.setParameter(Auftrag.PARAM_KUNDEID, kundeId)
										.getResultList();
		return auftraege;
	}
	
	@Override
	public Auftrag createAuftrag(Auftrag auftrag, 
									Kunde kunde, 
									Locale locale) {
		if (auftrag == null) {
			return null;
		}
		
		for (Auftragsposition ap : auftrag.getAuftragspositionen()) {
			logger.debugf("Auftragsposition %s", ap);
		}
		
		// managed kunden holen; Auftrag dem Kunden zuordnen
		kunde = ks.findKundenByKundennummer(kunde.getKundenNr(), FetchType.MIT_BESTELLUNGEN, locale);
		kunde.addAuftrag(auftrag);
		auftrag.setKunde(kunde);
		
		//id's müssen bei neuen Objekten leer sein
		auftrag.setId(KEINE_ID);
		for (Auftragsposition ap : auftrag.getAuftragspositionen()) {
			ap.setId(KEINE_ID);
		}
		
		validateAuftrag(auftrag, locale, Default.class);
		em.persist(auftrag);
		event.fire(auftrag);
		
		return auftrag;
	}
	
	private void validateAuftrag(Auftrag auftrag, Locale locale, Class<?>... groups) {
		final Validator validator = validatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Auftrag>> violations = validator.validate(auftrag);
		if (violations != null && !violations.isEmpty()) {
			//LOGGER.exiting("AuftragService", "createAuftrag", violations);
			throw new AuftragValidationException(auftrag, violations);
		}
	}
}
