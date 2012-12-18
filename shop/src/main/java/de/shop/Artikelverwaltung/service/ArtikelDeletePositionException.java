package de.shop.Artikelverwaltung.service;

import javax.ejb.ApplicationException;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Kundenverwaltung.domain.Kunde;


/**
 * Exception, die ausgel&ouml;st wird, wenn ein Kunde gel&ouml;scht werden soll, aber mindestens eine Bestellung hat
 */
@ApplicationException(rollback = true)
public class ArtikelDeletePositionException extends ArtikelServiceException {
	private static final long serialVersionUID = 1L;
	private final Long artikelId;
	private final int anzahlPositionen;
	
	public ArtikelDeletePositionException(Artikel artikel) {
		super("Artikel mit der ID=" + artikel.getId() + " kann nicht geloescht werden: "
			  + artikel.getLagerposition().size() + " Position(en)");
		this.artikelId = artikel.getId();
		this.anzahlPositionen = artikel.getLagerposition().size();;
	}

	public Long getArtikelId() {
		return artikelId;
	}
	public int getAnzahlPositionen() {
		return anzahlPositionen;
	}
}
