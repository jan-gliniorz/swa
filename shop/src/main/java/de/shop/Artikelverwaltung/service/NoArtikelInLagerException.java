package de.shop.Artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lager;

@ApplicationException(rollback = true)
public class NoArtikelInLagerException extends LagerServiceException {
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
