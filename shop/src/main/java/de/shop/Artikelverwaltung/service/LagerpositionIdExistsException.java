package de.shop.Artikelverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class LagerpositionIdExistsException extends AbstractLagerpositionServiceException {
	private static final long serialVersionUID = 1L;
	private final long id;
	
	public LagerpositionIdExistsException(long id) {
		super("Die Lagerpositions-ID " + id + " existiert bereits");
		this.id = id;
	}

	public long getId() {
		return id;
	}
}
