package de.shop.Kundenverwaltung.service;

import de.shop.Util.AbstractShopException;

public abstract class KundeServiceException extends AbstractShopException {
	private static final long serialVersionUID = -2849585609393128387L;

	public KundeServiceException(String msg) {
		super(msg);
	}
	
	public KundeServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
