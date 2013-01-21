package de.shop.Auftragsverwaltung.service;

import java.util.Collection;
import java.util.Date;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Auftragsverwaltung.domain.Rechnung;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte einer Bestellung nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class RechnungValidationException extends AbstractAuftragsverwaltungException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Date erzeugt;
	private final Long auftragId;
	private final Collection<ConstraintViolation<Rechnung>> violations;
	
	public RechnungValidationException(Rechnung rechnung,
			                             Collection<ConstraintViolation<Rechnung>> violations) {
		super(violations.toString());
		
		if (rechnung == null) {
			this.erzeugt = null;
			this.auftragId = null;
		}
		else {
			this.erzeugt = rechnung.getErstelltAm();
			final Auftrag auftrag = rechnung.getAuftrag();
			this.auftragId = auftrag == null ? null : auftrag.getId();
		}
		
		this.violations = violations;
	}
	
	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}
	
	public Long getAuftragId() {
		return auftragId;
	}
	
	public Collection<ConstraintViolation<Rechnung>> getViolations() {
		return violations;
	}
}
