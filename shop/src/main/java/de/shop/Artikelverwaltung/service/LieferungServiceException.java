package de.shop.Artikelverwaltung.service;

import de.shop.Util.AbstractShopException;

public abstract class LieferungServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public LieferungServiceException(String msg) {
		super(msg);
	}
	
	public LieferungServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
