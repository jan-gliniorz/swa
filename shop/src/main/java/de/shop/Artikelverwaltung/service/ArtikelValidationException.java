package de.shop.Artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Artikelverwaltung.domain.Artikel;

/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class ArtikelValidationException extends AbstractArtikelServiceException {
	private static final long serialVersionUID = 1L;
	private final Artikel artikel;
	private final Collection<ConstraintViolation<Artikel>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;

	public ArtikelValidationException(Artikel artikel,
			                        Collection<ConstraintViolation<Artikel>> violations) {
		super("Ungueltige Lieferung: " + artikel + ", Violations: " + violations);
		this.artikel = artikel;
		this.violations = violations;
	}
	

	public Artikel getLieferung() {
		return artikel;
	}

	public Collection<ConstraintViolation<Artikel>> getViolations() {
		return violations;
	}
}
