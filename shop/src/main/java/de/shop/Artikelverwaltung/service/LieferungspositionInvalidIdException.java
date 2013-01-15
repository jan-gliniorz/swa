package de.shop.Artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Artikelverwaltung.domain.Lieferungsposition;

@ApplicationException(rollback = true)
public class LieferungspositionInvalidIdException extends LieferungspositionServiceException {
	private static final long serialVersionUID = 1L;
	
	private final Long lieferungspositionId;
	private final Collection<ConstraintViolation<Lieferungsposition>> violations;
	
	public LieferungspositionInvalidIdException(Long lieferungspositionId, 
												Collection<ConstraintViolation<Lieferungsposition>> violations) {
		super("Ungueltige Lieferungspositionen-ID: " + lieferungspositionId + ", Violations: " + violations);
		this.lieferungspositionId = lieferungspositionId;
		this.violations = violations;
	}

	public Long getLieferungspositionId() {
		return lieferungspositionId;
	}

	public Collection<ConstraintViolation<Lieferungsposition>> getViolations() {
		return violations;
	}
}
