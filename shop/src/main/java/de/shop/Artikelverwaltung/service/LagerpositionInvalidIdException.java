package de.shop.Artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lagerposition;
import de.shop.Kundenverwaltung.domain.Kunde;

@ApplicationException(rollback = true)
public class LagerpositionInvalidIdException extends LagerpositionServiceException {
	private static final long serialVersionUID = 1L;
	
	private final Long lagerpositionId;
	private final Collection<ConstraintViolation<Lagerposition>> violations;
	
	public LagerpositionInvalidIdException(Long lagerpositionId, Collection<ConstraintViolation<Lagerposition>> violations) {
		super("Ungueltige Lieferungs-ID: " + lagerpositionId + ", Violations: " + violations);
		this.lagerpositionId = lagerpositionId;
		this.violations = violations;
	}

	public Long getLagerpositionId() {
		return lagerpositionId;
	}

	public Collection<ConstraintViolation<Lagerposition>> getViolations() {
		return violations;
	}
}
