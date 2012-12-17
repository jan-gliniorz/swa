package de.shop.Artikelverwaltung.service;

import javax.ejb.ApplicationException;

import de.shop.Artikelverwaltung.domain.Lieferung;


/**
 * Exception, die ausgel&ouml;st wird, wenn ein Kunde gel&ouml;scht werden soll, aber mindestens eine Bestellung hat
 */
@ApplicationException(rollback = true)
public class LieferungDeletePositionException extends LieferungServiceException {
	private static final long serialVersionUID = 1L;
	private final Long lieferungId;
	private final int anzahlPositionen;
	
	public LieferungDeletePositionException(Lieferung lieferung) {
		super("Lieferung mit der ID=" + lieferung.getId() + " kann nicht geloescht werden: "
			  + lieferung.getLieferungsposition().size() + " Position(en)");
		this.lieferungId = lieferung.getId();
		this.anzahlPositionen = lieferung.getLieferungsposition().size();;
	}

	public Long getLieferungId() {
		return lieferungId;
	}
	public int getAnzahlPositionen() {
		return anzahlPositionen;
	}
}
