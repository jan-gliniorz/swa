package de.shop.Artikelverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ArtikelIdExistsException extends AbstractArtikelServiceException {
	private static final long serialVersionUID = 1L;
	private final long id;
	
	public ArtikelIdExistsException(long id) {
		super("Die Artikels-ID " + id + " existiert bereits");
		this.id = id;
	}

	public long getId() {
		return id;
	}
}
