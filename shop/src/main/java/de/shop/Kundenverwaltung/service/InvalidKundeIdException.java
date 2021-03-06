package de.shop.Kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Kundenverwaltung.domain.Kunde;

@ApplicationException(rollback = true)
public class InvalidKundeIdException extends KundeServiceException {
	private static final long serialVersionUID = -8973151010781329074L;
	
	private final Long kundeId;
	private final Collection<ConstraintViolation<Kunde>> violations;
	
	public InvalidKundeIdException(Long kundeId, Collection<ConstraintViolation<Kunde>> violations) {
		super("Ungueltige Kundennummer: " + kundeId + ", Violations: " + violations);
		this.kundeId = kundeId;
		this.violations = violations;
	}

	public Long getKundeId() {
		return kundeId;
	}

	public Collection<ConstraintViolation<Kunde>> getViolations() {
		return violations;
	}
}
