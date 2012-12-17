package de.shop.Artikelverwaltung.Service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Artikelverwaltung.domain.Lager;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class LagerValidationException extends LagerServiceException {
	private static final long serialVersionUID = 1L;
	private final Lager lager;
	private final Collection<ConstraintViolation<Lager>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;

	public LagerValidationException(Lager lager,
			                        Collection<ConstraintViolation<Lager>> violations) {
		super("Ungueltige Lager: " + lager + ", Violations: " + violations);
		this.lager = lager;
		this.violations = violations;
	}
	

	public Lager getLagerposition() {
		return lager;
	}

	public Collection<ConstraintViolation<Lager>> getViolations() {
		return violations;
	}
}
