package de.shop.Artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Artikelverwaltung.domain.Artikel;

@ApplicationException(rollback = true)
public class ArtikelInvalidIdException extends ArtikelServiceException {
	private static final long serialVersionUID = 1L;
	
	private final Long artikelId;
	private final Collection<ConstraintViolation<Artikel>> violations;
	
	public ArtikelInvalidIdException(Long artikelId, Collection<ConstraintViolation<Artikel>> violations) {
		super("Ungueltige Lieferungs-ID: " + artikelId + ", Violations: " + violations);
		this.artikelId = artikelId;
		this.violations = violations;
	}

	public Long getArtikelId() {
		return artikelId;
	}

	public Collection<ConstraintViolation<Artikel>> getViolations() {
		return violations;
	}
}
