package de.shop.Auftragsverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Auftragsverwaltung.domain.Lieferungsposition;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class LieferungspositionValidationException extends LieferungspositionServiceException {
	private static final long serialVersionUID = 1L;
	private final Lieferungsposition lieferungsposition;
	private final Collection<ConstraintViolation<Lieferungsposition>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;

	public LieferungspositionValidationException(Lieferungsposition lieferungsposition,
			                        Collection<ConstraintViolation<Lieferungsposition>> violations) {
		super("Ungueltige Lieferungsposition: " + lieferungsposition + ", Violations: " + violations);
		this.lieferungsposition = lieferungsposition;
		this.violations = violations;
	}
	
//	@PostConstruct
//	private void setRollbackOnly() {
//		try {
//			if (trans.getStatus() == STATUS_ACTIVE) {
//				trans.setRollbackOnly();
//			}
//		}
//		catch (IllegalStateException | SystemException e) {
//			throw new InternalError(e);
//		}
//	}

	public Lieferungsposition getLieferungsposition() {
		return lieferungsposition;
	}

	public Collection<ConstraintViolation<Lieferungsposition>> getViolations() {
		return violations;
	}
}
