package de.shop.Artikelverwaltung.service;

import java.util.Collection;
import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Artikelverwaltung.domain.Lagerposition;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class LagerpositionValidationException extends LagerServiceException {
	private static final long serialVersionUID = 1L;
	private final Lagerposition lagerposition;
	private final Collection<ConstraintViolation<Lagerposition>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;

	public LagerpositionValidationException(Lagerposition lagerposition,
			                        Collection<ConstraintViolation<Lagerposition>> violations) {
		super("Ungueltige Lager: " + lagerposition + ", Violations: " + violations);
		this.lagerposition = lagerposition;
		this.violations = violations;
	}
	

	public Lagerposition getLagerposition() {
		return lagerposition;
	}

	public Collection<ConstraintViolation<Lagerposition>> getViolations() {
		return violations;
	}
}
