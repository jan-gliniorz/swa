package de.shop.Artikelverwaltung.service;

import de.shop.Util.AbstractShopException;

public abstract class AbstractLieferungServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public AbstractLieferungServiceException(String msg) {
		super(msg);
	}
	
	public AbstractLieferungServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
