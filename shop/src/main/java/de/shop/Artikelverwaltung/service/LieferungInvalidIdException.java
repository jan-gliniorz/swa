package de.shop.Artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Artikelverwaltung.domain.Lieferung;

@ApplicationException(rollback = true)
public class LieferungInvalidIdException extends AbstractLieferungServiceException {
	private static final long serialVersionUID = 1L;
	
	private final Long lieferungId;
	private final Collection<ConstraintViolation<Lieferung>> violations;
	
	public LieferungInvalidIdException(
		   Long lieferungId, 
		   Collection<ConstraintViolation<Lieferung>> violations) {
		super("Ungueltige Lieferungs-ID: " + lieferungId + ", Violations: " + violations);
		this.lieferungId = lieferungId;
		this.violations = violations;
	}

	public Long getLieferungId() {
		return lieferungId;
	}

	public Collection<ConstraintViolation<Lieferung>> getViolations() {
		return violations;
	}
}
