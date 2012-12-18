package de.shop.Artikelverwaltung.service;

import de.shop.Util.AbstractShopException;

public abstract class ArtikelServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public ArtikelServiceException(String msg) {
		super(msg);
	}
	
	public ArtikelServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
