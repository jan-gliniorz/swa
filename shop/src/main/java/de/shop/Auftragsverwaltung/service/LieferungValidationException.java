package de.shop.Auftragsverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Auftragsverwaltung.domain.Lieferung;
import de.shop.Kundenverwaltung.domain.Kunde;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class LieferungValidationException extends LieferungServiceException {
	private static final long serialVersionUID = 1L;
	private final Lieferung lieferung;
	private final Collection<ConstraintViolation<Lieferung>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;

	public LieferungValidationException(Lieferung lieferung,
			                        Collection<ConstraintViolation<Lieferung>> violations) {
		super("Ungueltige Lieferung: " + lieferung + ", Violations: " + violations);
		this.lieferung = lieferung;
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

	public Lieferung getLieferung() {
		return lieferung;
	}

	public Collection<ConstraintViolation<Lieferung>> getViolations() {
		return violations;
	}
}
