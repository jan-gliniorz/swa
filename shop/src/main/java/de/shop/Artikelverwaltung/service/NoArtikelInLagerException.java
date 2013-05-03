package de.shop.Artikelverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class NoArtikelInLagerException extends AbstractLagerServiceException {
	private static final long serialVersionUID = 1L;
	
	private final Long artikelId;
	
	public NoArtikelInLagerException(Long artikelId) {
		super("Der Artikel mit der ID: " + artikelId +  " ist nicht vorhanden");
		this.artikelId = artikelId;
	}

	public Long getArtikelId() {
		return this.artikelId;
	}
}
