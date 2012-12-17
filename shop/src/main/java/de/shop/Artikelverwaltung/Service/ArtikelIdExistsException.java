package de.shop.Artikelverwaltung.Service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ArtikelIdExistsException extends ArtikelServiceException {
	private static final long serialVersionUID = 1L;
	private final long id;
	
	public ArtikelIdExistsException(long id) {
		super("Die Artikels-ID " + id + " existiert bereits");
		this.id = id;
		ArtikelService a;
	}

	public long getId() {
		return id;
	}
}
