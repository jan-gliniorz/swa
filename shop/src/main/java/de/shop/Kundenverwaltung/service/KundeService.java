package de.shop.Kundenverwaltung.service;

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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.jboss.logging.Logger;

import de.shop.Auftragsverwaltung.domain.Auftragsposition;
import de.shop.Auftragsverwaltung.domain.Auftragsposition_;
import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Auftragsverwaltung.domain.Auftrag_;
import de.shop.Auth.service.jboss.AuthService;
import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Kundenverwaltung.domain.Kunde_;
import de.shop.Kundenverwaltung.domain.PasswordGroup;
import de.shop.Util.IdGroup;
import de.shop.Util.Log;
import de.shop.Util.ValidatorProvider;

@Log
public class KundeService implements Serializable {
	private static final long serialVersionUID = -5520738420154763865L;

	
	public enum FetchType {
		NUR_KUNDE,
		MIT_BESTELLUNGEN
	}
	
	public enum OrderType {
		KEINE,
		ID
	}
	
	@Inject
	private transient Logger logger;
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private AuthService authService;
	
	@Inject
	private ValidatorProvider validatorProvider;
	
	@PostConstruct
	private void postConstruct() {
		logger.debugf("CDI-faehiges Bean =%s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		logger.debugf("CDI-faehiges Bean =%s wird geloescht", this);
	}

	/**
	 */
	public List<Kunde> findAllKunden(FetchType fetch, OrderType order) {
		List<Kunde> kunden;
		switch (fetch) {
			case NUR_KUNDE:
				kunden = OrderType.ID.equals(order)
				         ? em.createNamedQuery(Kunde.KUNDE_BY_KNR, Kunde.class)
				             .getResultList()
				         : em.createNamedQuery(Kunde.KUNDEN_ALL, Kunde.class)
				             .getResultList();
				break;
			
			case MIT_BESTELLUNGEN:
				kunden = em.createNamedQuery(Kunde.KUNDE_BY_ID_AUFTRAEGE, Kunde.class)
						   .getResultList();
				break;

			default:
				kunden = OrderType.ID.equals(order)
		                 ? em.createNamedQuery(Kunde.KUNDE_BY_KNR, Kunde.class)
		                	 .getResultList()
		                 : em.createNamedQuery(Kunde.KUNDEN_ALL, Kunde.class)
		                     .getResultList();
				break;
		}

		return kunden;
	}
	
	/**
	 */
	public List<Kunde> findKundenByNachname(String nachname, FetchType fetch, Locale locale) {
		validateNachname(nachname, locale);
		
		List<Kunde> kunden;
		switch (fetch) {
			case NUR_KUNDE:
				kunden = em.createNamedQuery(Kunde.KUNDE_BY_NACHNAME, Kunde.class)
						   .setParameter(Kunde.PARAM_NACHNAME, nachname)
						   .getResultList();
				break;
			
			case MIT_BESTELLUNGEN:
				kunden = em.createNamedQuery(Kunde.KUNDE_BY_NAME_AUFTRAEGE, Kunde.class)
						   .setParameter(Kunde.PARAM_NACHNAME, nachname)
						   .getResultList();
				break;

			default:
				kunden = em.createNamedQuery(Kunde.KUNDE_BY_NACHNAME, Kunde.class)
						   .setParameter(Kunde.PARAM_NACHNAME, nachname)
						   .getResultList();
				break;
		}

		return kunden;
	}
	
	public List<String> findNachnamenByPrefix(String nachnamePrefix) {
		final List<String> nachnamen = em.createNamedQuery(Kunde.FIND_NACHNAMEN_BY_PREFIX, String.class)
				                         .setParameter(Kunde.PARAM_KUNDE_NACHNAME_PREFIX, nachnamePrefix + '%')
				                         .getResultList();
		return nachnamen;
	}
	
	private void validateNachname(String nachname, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "nachname",
				                                                                           nachname,
				                                                                           Default.class);
		if (!violations.isEmpty())
			throw new InvalidNachnameException(nachname, violations);
	}
	
	public List<String> findNachnamen(String nachname) {
		final List<String> nachnamen = em.createNamedQuery(Kunde.KUNDE_BY_NACHNAME,
				                                           String.class)
				                         .setParameter(Kunde.PARAM_NACHNAME, nachname)
				                         .getResultList();
		return nachnamen;
	}

	/**
	 */
	public Kunde findKundenByKundennummer(Long id, FetchType fetch, Locale locale) {
		validateKundeId(id, locale);
		
		Kunde kunde = null;
		try {
			switch (fetch) {
				case NUR_KUNDE:
					kunde = em.find(Kunde.class, id);
					break;
				
				case MIT_BESTELLUNGEN:
					kunde = em.createNamedQuery(Kunde.KUNDE_BY_ID_AUFTRAEGE, Kunde.class)
							  .setParameter(Kunde.PARAM_KUNDENNUMMER, id)
							  .getSingleResult();
					break;
	
				default:
					kunde = em.find(Kunde.class, id);
					break;
			}
		}
		catch (NoResultException e) {
			return null;
		}

		return kunde;
	}
	
	public List<Kunde> findKundenByKundenNrPrefix(Long id) {
		if (id == null) {
			return Collections.emptyList();
		}
		
		final List<Kunde> kunden = em.createNamedQuery(Kunde.FIND_KUNDEN_BY_KUNDENR_PREFIX,
				                                               Kunde.class)
				                             .setParameter(Kunde.PARAM_KUNDE_KUNDENUMMER_PREFIX, id.toString() + '%')
				                             .getResultList();
		return kunden;
	}
	
	private void validateKundeId(Long kundeId, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "kundenNr",
				                                                                           kundeId,
				                                                                           IdGroup.class);
		if (!violations.isEmpty())
			throw new InvalidKundeIdException(kundeId, violations);
	}

	/**
	 */
	public Kunde findKundeByEmail(String email, Locale locale) {
		validateEmail(email, locale);
		try {
			final Kunde kunde = em.createNamedQuery(Kunde.KUNDE_BY_EMAIL, Kunde.class)
					                      .setParameter(Kunde.PARAM_EMAIL, email)
					                      .getSingleResult();
			return kunde;
		}
		catch (NoResultException e) {
			return null;
		}
	}
	
	private void validateEmail(String email, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "email",
				                                                                           email,
				                                                                           Default.class);
		if (!violations.isEmpty())
			throw new InvalidEmailException(email, violations);
	}

	/**
	 */
	public Kunde createKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return kunde;
		}

		// Werden alle Constraints beim Einfuegen gewahrt?
		validateKunde(kunde, locale, PasswordGroup.class);
		
		// Pruefung, ob die Email-Adresse schon existiert
		try {
			em.createNamedQuery(Kunde.KUNDE_BY_EMAIL, Kunde.class)
			  .setParameter(Kunde.PARAM_EMAIL, kunde.getEmail())
			  .getSingleResult();
			throw new EmailExistsException(kunde.getEmail());
		}
		catch (NoResultException e) {
			// Noch kein Kunde mit dieser Email-Adresse
			logger.debugf("Email-Adresse existiert noch nicht");
		}
		
		// Password verschluesseln
		passwordVerschluesseln(kunde);
		
		kunde.setKundenNr(KEINE_ID);
		em.persist(kunde);
		return kunde;		
	}
	
	private void validateKunde(Kunde kunde, Locale locale, Class<?>... groups) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Kunde>> violations = validator.validate(kunde);
		if (!violations.isEmpty()) {
			throw new KundeValidationException(kunde, violations);
		}
	}
	
	/**
	 */
	public Kunde updateKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return null;
		}

		// Werden alle Constraints beim Modifizieren gewahrt?
		validateKunde(kunde, locale, PasswordGroup.class);
		
		// Pruefung, ob die Email-Adresse schon existiert
		try {
			final Kunde vorhandenerKunde = em.createNamedQuery(Kunde.KUNDE_BY_EMAIL,
					                                                   Kunde.class)
					                                 .setParameter(Kunde.PARAM_EMAIL, kunde.getEmail())
					                                 .getSingleResult();
			
			// Gibt es die Email-Adresse bei einem anderen Kunden?
			if (vorhandenerKunde.getKundenNr().longValue() != kunde.getKundenNr().longValue()) {
				throw new EmailExistsException(kunde.getEmail());
			}
		}
		catch (NoResultException e) {
			logger.debugf("Neue Email-Adresse");
		}
		
		em.merge(kunde);

		return kunde;
	}

	/**
	 */
	public void deleteKunde(Kunde kunde) {
		if (kunde == null) {
			return;
		}
		
		// Bestellungen laden, damit sie anschl. ueberprueft werden koennen
		try {
			kunde = findKundenByKundennummer(kunde.getKundenNr(), FetchType.MIT_BESTELLUNGEN, Locale.getDefault());
		}
		catch (InvalidKundeIdException e) {
			return;
		}
		
		if (kunde == null) {
			return;
		}
		
		// Gibt es Bestellungen?
		if (!kunde.getAuftraege().isEmpty()) {
			throw new KundeDeleteBestellungException(kunde);
		}

		em.remove(kunde);
	}

	/**
	 */
	public List<Kunde> findKundenByPLZ(String plz) {
		final List<Kunde> kunden = em.createNamedQuery(Kunde.KUNDE_BY_PLZ, Kunde.class)
                                             .setParameter(Kunde.PARAM_PLZ, plz)
                                             .getResultList();
		return kunden;
	}

	/**
	 */
	public List<Kunde> findKundenByNachnameCriteria(String nachname) {
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Kunde> criteriaQuery = builder.createQuery(Kunde.class);
		final Root<Kunde> k = criteriaQuery.from(Kunde.class);

		final Path<String> nachnamePath = k.get(Kunde_.nachname);
		//final Path<String> nachnamePath = k.get("nachname");
		
		final Predicate pred = builder.equal(nachnamePath, nachname);
		criteriaQuery.where(pred);

		final List<Kunde> kunden = em.createQuery(criteriaQuery).getResultList();
		return kunden;
	}
	
	/**
	 */
	public List<Kunde> findKundenMitMinBestMenge(short minMenge) {
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Kunde> criteriaQuery  = builder.createQuery(Kunde.class);
		final Root<Kunde> k = criteriaQuery.from(Kunde.class);

		final Join<Kunde, Auftrag> b = k.join(Kunde_.auftraege);
		final Join<Auftrag, Auftragsposition> bp = b.join(Auftrag_.auftragspositionen);
		criteriaQuery.where(builder.gt(bp.<Integer>get(Auftragsposition_.anzahl), minMenge))
		             .distinct(true);
		
		final List<Kunde> kunden = em.createQuery(criteriaQuery).getResultList();
		return kunden;
	}

	private void passwordVerschluesseln(Kunde kunde) {
		logger.debugf("passwordVerschluesseln BEGINN: %s", kunde);

		final String unverschluesselt = kunde.getPasswort();
		final String verschluesselt = authService.verschluesseln(unverschluesselt);
		kunde.setPasswort(verschluesselt);
		kunde.setPasswortWdh(verschluesselt);

		logger.debugf("passwordVerschluesseln ENDE: %s", verschluesselt);
	}

}
