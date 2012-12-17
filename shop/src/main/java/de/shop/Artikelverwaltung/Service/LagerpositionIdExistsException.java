package de.shop.Artikelverwaltung.Service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class LagerpositionIdExistsException extends LagerpositionServiceException {
	private static final long serialVersionUID = 1L;
	private final long id;
	
	public LagerpositionIdExistsException(long id) {
		super("Die Lagerpositions-ID " + id + " existiert bereits");
		this.id = id;
		ArtikelService a;
	}

	public long getId() {
		return id;
	}
}
