package de.shop.Kundenverwaltung.service;

import javax.ejb.ApplicationException;

import de.shop.Kundenverwaltung.domain.Kunde;


/**
 * Exception, die ausgel&ouml;st wird, wenn ein Kunde gel&ouml;scht werden soll, aber mindestens eine Bestellung hat
 */
@ApplicationException(rollback = true)
public class KundeDeleteBestellungException extends KundeServiceException {
	private static final long serialVersionUID = 2237194289969083093L;
	private final Long kundeId;
	private final int anzahlBestellungen;
	
	public KundeDeleteBestellungException(Kunde kunde) {
		super("Kunde mit der Kundennummer=" + kunde.getKundenNr() + " kann nicht geloescht werden: "
			  + kunde.getAuftraege().size() + " Aufträge");
		this.kundeId = kunde.getKundenNr();
		this.anzahlBestellungen = kunde.getAuftraege().size();;
	}

	public Long getKundeId() {
		return kundeId;
	}
	public int getAnzahlBestellungen() {
		return anzahlBestellungen;
	}
}
