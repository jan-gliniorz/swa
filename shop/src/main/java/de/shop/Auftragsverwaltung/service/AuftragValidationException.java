package de.shop.Auftragsverwaltung.service;

import java.util.Collection;
import java.util.Date;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Kundenverwaltung.domain.Kunde;;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte einer Bestellung nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class AuftragValidationException extends AbstractAuftragsverwaltungException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Date erzeugt;
	private final Long kundeId;
	private final Collection<ConstraintViolation<Auftrag>> violations;
	
	public AuftragValidationException(Auftrag auftrag,
			                             Collection<ConstraintViolation<Auftrag>> violations) {
		super(violations.toString());
		
		if (auftrag == null) {
			this.erzeugt = null;
			this.kundeId = null;
		}
		else {
			this.erzeugt = auftrag.getErstelltAm();
			final Kunde kunde = auftrag.getKunde();
			this.kundeId = kunde == null ? null : kunde.getKundenNr();
		}
		
		this.violations = violations;
	}
	
	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}
	
	public Long getKundeId() {
		return kundeId;
	}
	
	public Collection<ConstraintViolation<Auftrag>> getViolations() {
		return violations;
	}
}
