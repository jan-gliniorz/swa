package de.shop.Artikelverwaltung.service;

import de.shop.Util.AbstractShopException;

public abstract class AbstractArtikelServiceException extends AbstractShopException {
	private static final long serialVersionUID = -4898182940794392820L;

	public AbstractArtikelServiceException(String msg) {
		super(msg);
	}
	
	public AbstractArtikelServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
