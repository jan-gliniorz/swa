package de.shop.Auftragsverwaltung.service;

import static de.shop.Util.Constants.KEINE_ID;
import static java.util.logging.Level.FINER;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Auftragsverwaltung.domain.Rechnung;
import de.shop.Util.Log;
import de.shop.Util.ValidatorProvider;

@Log
public class RechnungServiceImpl implements Serializable, RechnungService {
	private static final long serialVersionUID = -9145947650157430928L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private AuftragService auftragService;
	
	@Inject
	private ValidatorProvider validatorProvider;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	@Override
	public List<Rechnung> findRechnungAll() {
		final List<Rechnung> rechnungen = em.createNamedQuery(Rechnung.FIND_RECHNUNG_ALL, Rechnung.class)
											.getResultList();
		return rechnungen;
	}

	@Override
	public Rechnung findRechnungById(Long id) {
		final Rechnung rechnung = em.find(Rechnung.class, id);
		return rechnung;
	}

	@Override
	public Rechnung createRechnung(Rechnung rechnung, 
									Locale locale) {
		if (rechnung == null) {
			return null;
		}
		
		//id's müssen bei neuen Objekten leer sein
		rechnung.setId(KEINE_ID);
		
		// managed Auftrag holen; Rechnung dem Auftrag zuordnen
		Auftrag auftrag = auftragService.findAuftragById(rechnung.getAuftrag().getId());
		auftrag.setRechnung(rechnung);
		rechnung.setAuftrag(auftrag);
		
		validateRechnung(rechnung, locale, Default.class);
		em.persist(rechnung);
		
		return rechnung;
	}
	
	private void validateRechnung(Rechnung rechnung, Locale locale, Class<?>... groups) {
		final Validator validator = validatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Rechnung>> violations = validator.validate(rechnung);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.exiting("RechnungService", "createRechnung", violations);
			throw new RechnungValidationException(rechnung, violations);
		}
	}
}
