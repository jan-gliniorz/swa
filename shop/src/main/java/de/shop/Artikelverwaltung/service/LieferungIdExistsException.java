package de.shop.Artikelverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class LieferungIdExistsException extends LieferungServiceException {
	private static final long serialVersionUID = 1L;
	private final long id;
	
	public LieferungIdExistsException(long id) {
		super("Die Lieferungs-ID " + id + " existiert bereits");
		this.id = id;
	}

	public long getId() {
		return id;
	}
}
