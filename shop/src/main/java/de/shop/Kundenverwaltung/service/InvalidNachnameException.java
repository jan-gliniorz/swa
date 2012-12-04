package de.shop.Kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Kundenverwaltung.domain.Kunde;

@ApplicationException(rollback = true)
public class InvalidNachnameException extends KundeServiceException {
	private static final long serialVersionUID = -8973151010781329074L;
	
	private final String nachname;
	private final Collection<ConstraintViolation<Kunde>> violations;
	
	public InvalidNachnameException(String nachname, Collection<ConstraintViolation<Kunde>> violations) {
		super("Ungueltiger Nachname: " + nachname + ", Violations: " + violations);
		this.nachname = nachname;
		this.violations = violations;
	}

	public String getNachname() {
		return nachname;
	}

	public Collection<ConstraintViolation<Kunde>> getViolations() {
		return violations;
	}
}
