package de.shop.Artikelverwaltung.service;

import de.shop.Util.AbstractShopException;

public abstract class LieferungspositionServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public LieferungspositionServiceException(String msg) {
		super(msg);
	}
	
	public LieferungspositionServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
