package de.shop.Artikelverwaltung.Service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Artikelverwaltung.domain.Lager;

@ApplicationException(rollback = true)
public class LagerInvalidIdException extends LagerServiceException {
	private static final long serialVersionUID = 1L;
	
	private final Long lagerId;
	private final Collection<ConstraintViolation<Lager>> violations;
	
	public LagerInvalidIdException(Long lagerId, Collection<ConstraintViolation<Lager>> violations) {
		super("Ungueltige Lager-ID: " + lagerId + ", Violations: " + violations);
		this.lagerId = lagerId;
		this.violations = violations;
	}

	public Long getLagerId() {
		return lagerId;
	}

	public Collection<ConstraintViolation<Lager>> getViolations() {
		return violations;
	}
}
