package de.shop.Artikelverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class LagerIdExistsException extends AbstractLagerServiceException {
	private static final long serialVersionUID = 1L;
	private final long id;
	
	public LagerIdExistsException(long id) {
		super("Die Lager-ID " + id + " existiert bereits");
		this.id = id;
	}

	public long getId() {
		return id;
	}
}
