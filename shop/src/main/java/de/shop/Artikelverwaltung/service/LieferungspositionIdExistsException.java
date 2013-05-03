package de.shop.Artikelverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class LieferungspositionIdExistsException extends AbstractLieferungspositionServiceException {
	private static final long serialVersionUID = 1L;
	private final long id;
	
	public LieferungspositionIdExistsException(long id) {
		super("Die Lieferungspositionen-ID " + id + " existiert bereits");
		this.id = id;
	}

	public long getId() {
		return id;
	}
}
